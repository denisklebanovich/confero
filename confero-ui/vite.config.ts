/// <reference types="vite-plugin-svgr/client" />
import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import path from "path"
import svgr from "vite-plugin-svgr";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react(),svgr()],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080/api',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        },
    }
})
