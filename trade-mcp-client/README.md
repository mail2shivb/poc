# trade-mcp-client-rest

Spring Boot **REST** wrapper around an **MCP stdio** client. Java 21 / Maven / (Lombok included but not required).

## Build
```bash
mvn -q -DskipTests package
```

## Configure the MCP server command
Set the command that starts your MCP server (it must speak MCP over stdio and write logs to **stderr**). For a Spring Boot server JAR:
```bash
export MAVEN_OPTS="-Dmcp.server.command=\"java -Dlogging.console.target=SYSTEM_ERR -Dspring.main.banner-mode=off -jar /abs/path/trade-mcp-server-0.0.1-SNAPSHOT.jar\""
```
Or pass at runtime:
```bash
java -Dmcp.server.command="java -Dlogging.console.target=SYSTEM_ERR -Dspring.main.banner-mode=off -jar /abs/path/trade-mcp-server-0.0.1-SNAPSHOT.jar"   -jar target/trade-mcp-client-rest-0.1.0-SNAPSHOT.jar
```

## Run
```bash
java -jar target/trade-mcp-client-rest-0.1.0-SNAPSHOT.jar
```

## Endpoints
- `POST /api/initialize` – starts the server process (if needed) and sends `initialize`.
- `GET /api/tools` – list tool names.
- `GET /api/tools/raw` – full tool metadata from the MCP server.
- `POST /api/tools/{name}` – call a tool with JSON body as arguments.

### Examples
```bash
# initialize
curl -X POST localhost:8080/api/initialize

# list tools
curl localhost:8080/api/tools

# call a tool
curl -X POST localhost:8080/api/tools/trade.echo -H 'content-type: application/json' -d '{"message":"hello"}'
```

### Notes about robustness
- The client tolerates LF or CRLF headers and will **skip any non-framed stdout** until it sees a header block.
- Still, redirect your server logs to **stderr** (example `-Dlogging.console.target=SYSTEM_ERR` for Spring Boot).
- Per-request timeout is configurable via `mcp.client.timeout.ms` (default 30000 ms).
