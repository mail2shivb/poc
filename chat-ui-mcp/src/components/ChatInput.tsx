import { useEffect, useRef, useState } from 'react'

export default function ChatInput({
  disabled,
  onSend,
}: {
  disabled?: boolean
  onSend: (text: string) => void
}) {
  const [text, setText] = useState('')
  const taRef = useRef<HTMLTextAreaElement | null>(null)

  useEffect(() => {
    const el = taRef.current
    if (!el) return
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault()
        if (text.trim().length) {
          onSend(text.trim())
          setText('')
        }
      }
    }
    el.addEventListener('keydown', handler)
    return () => el.removeEventListener('keydown', handler)
  }, [text, onSend])

  return (
    <div className="border-t border-border bg-bg/80">
      <div className="mx-auto max-w-4xl px-4 py-3">
        <div className="flex items-end gap-2">
          <textarea
            ref={taRef}
            className="flex-1 resize-none rounded-xl bg-panel text-text placeholder-gray-500 border border-border px-4 py-3 focus:outline-none focus:ring-1 focus:ring-accent"
            placeholder="Send a message..."
            rows={1}
            value={text}
            onChange={(e) => setText(e.target.value)}
            disabled={disabled}
          />
          <button
            className="rounded-xl px-4 py-3 bg-accent text-white font-medium disabled:opacity-50"
            disabled={disabled || !text.trim().length}
            onClick={() => { if (text.trim()) { onSend(text.trim()); setText('') } }}
          >
            Send
          </button>
        </div>
        <p className="mt-2 text-[11px] text-gray-400">Press Enter to send • Shift+Enter for newline</p>
      </div>
    </div>
  )
}