export default function ChatHeader() {
  return (
    <header className="sticky top-0 z-10 border-b border-border bg-bg/80 backdrop-blur-sm">
      <div className="mx-auto max-w-4xl px-4 py-3 flex items-center justify-between">
        <h1 className="text-lg font-semibold text-text">MCP Chat</h1>
        <a href="https://modelcontextprotocol.io" target="_blank" rel="noreferrer" className="text-xs text-gray-400 hover:text-gray-300">Model Context Protocol</a>
      </div>
    </header>
  )
}