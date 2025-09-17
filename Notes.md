java -jar target/trade-mcp-client-0.0.2-SNAPSHOT.jar --cmd="java -jar /Users/SHIV/work/Spring-AI/demo/trade-mcp-server-0.0.1-SNAPSHOT.jar" --action=list-tools

npx @modelcontextprotocol/inspector java -jar /Users/SHIV/work/Spring-AI/trade-mcp-server-0.0.1-SNAPSHOT.jar

kill -9 $(lsof -t -i:8081)

