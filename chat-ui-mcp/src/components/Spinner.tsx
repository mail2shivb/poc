export default function Spinner() {
  return (
    <div className="flex items-center gap-2 text-gray-400 text-sm">
      <div className="h-2 w-2 rounded-full bg-gray-400 animate-bounce [animation-delay:-0.2s]"></div>
      <div className="h-2 w-2 rounded-full bg-gray-400 animate-bounce"></div>
      <div className="h-2 w-2 rounded-full bg-gray-400 animate-bounce [animation-delay:0.2s]"></div>
    </div>
  )
}