package com.example.trademcpclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TraderMcpClientService {
    private final List<McpSyncClient> mcpSyncClientList;
    private McpSchema.ListToolsResult tools;
    private McpSyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void getAllToolsOnStartup() {
        if (!mcpSyncClientList.isEmpty()) {
            client = mcpSyncClientList.getFirst();
            tools = client.listTools();
            System.out.println("Available tools on startup: " + tools.tools());
        } else {
            System.out.println("No MCP clients available on startup.");
        }
    }

    public String runPrompt(String prompt) {
        if (client == null || tools == null) {
            throw new IllegalStateException("MCP client is not initialized or tools are unavailable.");
        }
        try {
            String toolName;
            Map<String, Object> argsMap = new HashMap<>();
            prompt = prompt.trim().toLowerCase();
            if (prompt.matches("fetch all trades|get all trades|show all trades")) {
                toolName = "getAllTrades";
            } else if (prompt.matches("delete trade( by id)?( [0-9]+)?")) {
                toolName = "deleteTrade";
                Matcher m = Pattern.compile("delete trade( by id)? ([0-9]+)").matcher(prompt);
                if (m.find()) {
                    argsMap.put("id", Integer.parseInt(m.group(2)));
                } else {
                    throw new IllegalArgumentException(
                        "To delete a trade, use: 'delete trade <id>' or 'delete trade by id <id>'. " +
                        "For example: 'delete trade 4'.");
                }
            } else if (prompt.matches("get trade( by id)?( [0-9]+)?")) {
                toolName = "getTradeById";
                Matcher m = Pattern.compile("get trade( by id)? ([0-9]+)").matcher(prompt);
                if (m.find()) {
                    argsMap.put("id", Integer.parseInt(m.group(2)));
                } else {
                    throw new IllegalArgumentException(
                        "To get a trade, use: 'get trade <id>' or 'get trade by id <id>'. " +
                        "For example: 'get trade 4'.");
                }
            } else if (prompt.startsWith("create a trade for")) {
                toolName = "createTrade";
                Matcher m = Pattern.compile("create a trade for quantity (\\d+) symbol ([A-Za-z]+) at price \\$?(\\d+(?:\\.\\d+)?)").matcher(prompt);
                if (m.find()) {
                    argsMap.put("quantity", Integer.parseInt(m.group(1)));
                    argsMap.put("symbol", m.group(2).toUpperCase());
                    argsMap.put("price", Double.parseDouble(m.group(3)));
                } else {
                    throw new IllegalArgumentException("Please specify quantity, symbol, and price (e.g., 'create a trade for 100 AAPL at $150').");
                }
            } else if (prompt.startsWith("update trade")) {
                toolName = "updateTrade";
                Matcher m = Pattern.compile("update trade (\\d+) to (\\d+) ([A-Za-z]+) at \\$?(\\d+(?:\\.\\d+)?)").matcher(prompt);
                if (m.find()) {
                    argsMap.put("id", Integer.parseInt(m.group(1)));
                    argsMap.put("quantity", Integer.parseInt(m.group(2)));
                    argsMap.put("symbol", m.group(3).toUpperCase());
                    argsMap.put("price", Double.parseDouble(m.group(4)));
                } else {
                    throw new IllegalArgumentException("Please specify id, quantity, symbol, and price (e.g., 'update trade 4 to 200 AAPL at $155').");
                }
            } else {
                    throw new IllegalArgumentException(
                        "To update a trade, use: 'update trade <id> to <quantity> <symbol> at $<price>'. " +
                        "For example: 'update trade 4 to 200 AAPL at $155'.");
            }
            if (tools.tools().stream().noneMatch(t -> toolName.equalsIgnoreCase(t.name()))) {
                throw new IllegalArgumentException(
                    "Unrecognized prompt. Supported prompts are:\n" +
                    "- fetch all trades\n" +
                    "- get trade <id>\n" +
                    "- delete trade <id>\n" +
                    "- create a trade for <quantity> <symbol> at $<price>\n" +
                    "- update trade <id> to <quantity> <symbol> at $<price>\n" +
                    "For example: 'create a trade for 100 AAPL at $150'.");
            }
            String argsJson = objectMapper.writeValueAsString(argsMap);

            McpSchema.CallToolResult callResult = client.callTool(new McpSchema.CallToolRequest(toolName, argsJson));
            return explainToolResult(toolName, callResult);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while running prompt: " + e.getMessage(), e);
        }
    }

    private String explainToolResult(String toolName, McpSchema.CallToolResult result) {
        try {
            if (result == null) {
                return "No result returned from tool '" + toolName + "'.";
            }
            String json = objectMapper.writeValueAsString(result.content().getFirst());
            if (toolName.equalsIgnoreCase("getAllTrades")) {
                // Expecting a list of trades
                String text = ((McpSchema.TextContent) result.content().getFirst()).text();
                List<?> trades = objectMapper.readValue(text, List.class);
                String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(trades);

                if (trades.isEmpty()) {
                    return "No trades found.";
                }
                return "Found " + trades.size() + " trade(s):\n" + value;
            } else if (toolName.equalsIgnoreCase("getTradeById")) {
                Map<?,?> trade = objectMapper.readValue(json, Map.class);
                if (trade.isEmpty()) {
                    return "No trade found with the specified ID.";
                }
                return "Trade details: " + trade;
            } else if (toolName.equalsIgnoreCase("createTrade")) {
                Map<?,?> trade = objectMapper.readValue(json, Map.class);
                return "Trade created: " + trade;
            } else if (toolName.equalsIgnoreCase("updateTrade")) {
                Map<?,?> trade = objectMapper.readValue(json, Map.class);
                return "Trade updated: " + trade;
            } else if (toolName.equalsIgnoreCase("deleteTrade")) {
                Map<?,?> resultMap = objectMapper.readValue(json, Map.class);
                if (resultMap.containsKey("success") && Boolean.TRUE.equals(resultMap.get("success"))) {
                    return "Trade deleted successfully.";
                } else {
                    return "Trade could not be deleted. Details: " + resultMap;
                }
            } else {
                return "Tool '" + toolName + "' executed. Result: " + json;
            }
        } catch (Exception e) {
            return "Tool '" + toolName + "' executed, but result could not be explained: " + e.getMessage();
        }
    }

}
