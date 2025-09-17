import { useCallback, useMemo, useRef, useState, useEffect } from 'react'
import ChatHeader from './components/ChatHeader'
import ChatInput from './components/ChatInput'
import MessageBubble from './components/MessageBubble'
import Spinner from './components/Spinner'
import type { Message } from './types'
import { sendMessage } from './api'

export default function App() {
  const [messages, setMessages] = useState<Message[]>([{
    id: crypto.randomUUID(),
    role: 'assistant',
    content: 'Hi! Type a prompt and I\'ll call /chat-controller/chat.'
  }])
  const [busy, setBusy] = useState(false)
  const [conversationId, setConversationId] = useState<string | undefined>(undefined)
  const bottomRef = useRef<HTMLDivElement | null>(null)

  const scrollToBottom = useCallback(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [])

  useEffect(() => { scrollToBottom() }, [messages, scrollToBottom])

  const onSend = useCallback(async (text: string) => {
    if (busy) return

    const userMsg: Message = { id: crypto.randomUUID(), role: 'user', content: text }
    const draftAssistant: Message = { id: crypto.randomUUID(), role: 'assistant', content: '…' }
    setMessages(prev => [...prev, userMsg, draftAssistant])

    setBusy(true)
    try {
      const res = await sendMessage(text, conversationId)
      setMessages(prev => {
        const copy = [...prev]
        const idx = copy.findIndex(m => m.id === draftAssistant.id)
        if (idx >= 0) copy[idx] = { ...copy[idx], content: res.content }
        return copy
      })
      if (res.conversationId) setConversationId(res.conversationId)
    } catch (err: any) {
      const msg = err?.message || 'Something went wrong'
      setMessages(prev => {
        const copy = [...prev]
        const idx = copy.findIndex(m => m.id === draftAssistant.id)
        if (idx >= 0) copy[idx] = { ...copy[idx], content: `⚠️ ${msg}` }
        return copy
      })
    } finally {
      setBusy(false)
      scrollToBottom()
    }
  }, [busy, conversationId, scrollToBottom])

  const header = useMemo(() => <ChatHeader />, [])

  return (
    <div className="min-h-screen flex flex-col bg-bg text-text">
      {header}
      <main className="flex-1">
        <div className="mx-auto max-w-4xl px-4 py-6">
          {messages.map(m => (
            <MessageBubble key={m.id} m={m} />
          ))}
          {busy && (
            <div className="mb-4 flex justify-start"><div className="bg-panel border border-border rounded-2xl px-4 py-3"><Spinner /></div></div>
          )}
          <div ref={bottomRef} />
        </div>
      </main>
      <ChatInput disabled={busy} onSend={onSend} />
    </div>
  )
}