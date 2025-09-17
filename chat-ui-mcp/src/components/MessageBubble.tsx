import type { Message } from '../types'
import TradesTable, { type TradeRow } from './TradesTable'

function cleanNumber(input: any): number | undefined {
  if (typeof input === 'number') return input
  if (typeof input === 'string') {
    const m = input.match(/[+-]?[0-9]*\.?[0-9]+/)
    if (m) return Number(m[0])
  }
  return undefined
}

function pick<T = any>(obj: any, keys: string[]): T | undefined {
  if (!obj || typeof obj !== 'object') return undefined
  for (const k of keys) {
    const found = Object.keys(obj).find((kk) => kk.toLowerCase() === k.toLowerCase())
    if (found) return obj[found]
  }
  return undefined
}

function toTradeRow(val: any): TradeRow | undefined {
  if (!val || typeof val !== 'object') return undefined
  const symbol = pick<string>(val, ['symbol', 'sym'])
  const quantityRaw = pick<any>(val, ['quantity', 'qty', 'amount'])
  const priceRaw = pick<any>(val, ['price', 'px', 'value'])
  const executedAt = pick<any>(val, ['executedAt', 'executed_at', 'executed', 'timestamp', 'ts', 'time', 'executedTime'])
  const quantity = cleanNumber(quantityRaw)
  const price = cleanNumber(priceRaw)
  if (symbol == null && quantity == null && price == null && executedAt == null) return undefined
  return { symbol, quantity, price, executedAt: executedAt ?? null }
}

function balancedJsonAt(s: string, start: number): string | undefined {
  const open = s[start]
  const close = open === '{' ? '}' : open === '[' ? ']' : ''
  if (!close) return undefined
  let depth = 0
  let inStr = false
  let esc = false
  for (let i = start; i < s.length; i++) {
    const ch = s[i]
    if (inStr) {
      if (esc) { esc = false; continue }
      if (ch === '\\') { esc = true; continue }
      if (ch === '"') inStr = false
      continue
    }
    if (ch === '"') { inStr = true; continue }
    if (ch === open) depth++
    else if (ch === close) depth--
    if (depth === 0) {
      return s.slice(start, i + 1)
    }
  }
  return undefined
}

function findFirstJsonValue(content: string): any | undefined {
  // Try full content first
  try {
    return JSON.parse(content)
  } catch {}
  // If markers like text= or data= exist, start from there
  const markers = ['text=', 'data=', 'value=', 'payload=']
  for (const mk of markers) {
    const idx = content.indexOf(mk)
    if (idx !== -1) {
      for (let j = idx; j < content.length; j++) {
        const c = content[j]
        if (c === '{' || c === '[') {
          const raw = balancedJsonAt(content, j)
          if (raw) {
            try { return JSON.parse(raw) } catch {}
          }
        }
      }
    }
  }
  // Generic scan for any JSON block
  for (let i = 0; i < content.length; i++) {
    const c = content[i]
    if (c === '{' || c === '[') {
      const raw = balancedJsonAt(content, i)
      if (raw) {
        try { return JSON.parse(raw) } catch {}
      }
    }
  }
  return undefined
}

function parseTradeRows(content: string): TradeRow[] | undefined {
  const value = findFirstJsonValue(content)
  if (Array.isArray(value)) {
    const rows = value.map(toTradeRow).filter(Boolean) as TradeRow[]
    return rows.length ? rows : undefined
  }
  if (value && typeof value === 'object') {
    const row = toTradeRow(value)
    return row ? [row] : undefined
  }
  return undefined
}

export default function MessageBubble({ m }: { m: Message }) {
  const isUser = m.role === 'user'
  const rows = !isUser ? parseTradeRows(m.content) : undefined
  const shouldShowRaw = isUser || !rows || rows.length === 0
  return (
    <div className={`w-full flex ${isUser ? 'justify-end' : 'justify-start'} mb-4`}>
      <div className={`${isUser ? 'bg-accent text-white' : 'bg-panel text-text'} max-w-[80%] rounded-2xl px-4 py-3 border border-border whitespace-pre-wrap leading-6`}>
        {!isUser && rows && rows.length > 0 && (
          <TradesTable rows={rows} />
        )}
        {shouldShowRaw ? (m.content || (isUser ? '' : '…')) : null}
      </div>
    </div>
  )
}