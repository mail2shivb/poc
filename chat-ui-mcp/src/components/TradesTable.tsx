// filepath: /Users/SHIV/work/Spring-AI/poc/chat-ui-mcp/src/components/TradesTable.tsx
export type TradeRow = {
  symbol?: string
  quantity?: number
  price?: number
  executedAt?: number | string | null
}

function fmtDate(v: number | string | null | undefined): string {
  if (v == null || v === '') return ''
  const n = typeof v === 'string' ? Number(v) : v
  if (Number.isFinite(n)) {
    const ms = (n as number) > 1e12 ? (n as number) : (n as number) * 1000
    const d = new Date(ms)
    if (!isNaN(d.getTime())) return d.toISOString()
  }
  return String(v)
}

export default function TradesTable({ rows }: { rows: TradeRow[] }) {
  return (
    <div className="overflow-x-auto">
      <table className="min-w-full text-sm border border-border rounded-md overflow-hidden">
        <thead className="bg-muted text-text/80">
          <tr>
            <th className="px-3 py-2 text-left border-b border-border">Symbol</th>
            <th className="px-3 py-2 text-right border-b border-border">Quantity</th>
            <th className="px-3 py-2 text-right border-b border-border">Price</th>
            <th className="px-3 py-2 text-left border-b border-border">Executed At</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i} className="odd:bg-panel/60">
              <td className="px-3 py-2 border-b border-border">{r.symbol ?? ''}</td>
              <td className="px-3 py-2 border-b border-border text-right">{r.quantity ?? ''}</td>
              <td className="px-3 py-2 border-b border-border text-right">{r.price ?? ''}</td>
              <td className="px-3 py-2 border-b border-border">{fmtDate(r.executedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

