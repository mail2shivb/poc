// Ensure this file is a module so global augmentations are applied
export {}

declare global {
  interface ImportMetaEnv {
    readonly DEV: boolean
    readonly VITE_API_BASE?: string
    readonly VITE_CHAT_PATH?: string
    readonly VITE_STREAM_PATH?: string
    readonly VITE_STREAMING?: string
  }

  interface ImportMeta {
    readonly env: ImportMetaEnv
  }
}
