import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/example/example-ui/',
  server: {
    port: 3000,
    proxy: {
      '/example/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
