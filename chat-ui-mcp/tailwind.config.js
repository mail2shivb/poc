/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        bg: '#0e0f12',
        panel: '#16181d',
        border: '#2a2e36',
        text: '#e4e6eb',
        accent: '#10a37f'
      }
    },
  },
  plugins: [],
}