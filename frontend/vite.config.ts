import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/ladder': 'http://localhost:5001',
      '/fixtures': 'http://localhost:5001',
      '/results': 'http://localhost:5001',
    },
  },
});
