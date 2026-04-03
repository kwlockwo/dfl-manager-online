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
      '/fixtures': {
        target: 'http://localhost:5001',
        bypass: (req) => {
          if (req.headers.accept?.includes('text/html')) return req.url;
        },
      },
      '/results': {
        target: 'http://localhost:5001',
        bypass: (req) => {
          if (req.headers.accept?.includes('text/html')) return req.url;
        },
      },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    globals: true,
  },
});
