export async function sendMessage(
  message: string,
  conversationId?: string,
  _onToken?: (chunk: string) => void, // kept for compatibility; ignored
): Promise<{ content: string; conversationId?: string }> {
  const res = await fetch('/api/mcp/chat', {
    method: 'POST',
    headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, conversationId }),
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  const ct = res.headers.get('Content-Type') || ''
  if (ct.includes('application/json')) {
    const data = await res.json()
    let content: string
    if (typeof data === 'string') content = data
    else if (typeof (data as any)?.content === 'string') content = (data as any).content
    else if (typeof (data as any)?.text === 'string') content = (data as any).text
    else if (typeof (data as any)?.message === 'string') content = (data as any).message
    else content = JSON.stringify(data)
    const conversationIdOut = typeof (data as any)?.conversationId === 'string' ? (data as any).conversationId : conversationId
    return { content, conversationId: conversationIdOut }
  }
  const text = await res.text()
  return { content: text, conversationId }
}
