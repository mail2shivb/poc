import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  return {
    plugins: [react()],
    server: {
      port: 5173,
      strictPort: false,
      proxy: {
        '/api': { target: 'http://localhost:8081', changeOrigin: true },
        '/chat-controller': { target: 'http://localhost:8081', changeOrigin: true }
      }
    }
  }
})