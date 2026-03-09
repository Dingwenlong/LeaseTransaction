/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'cyan-neon': '#22d3ee',
        'magenta-neon': '#ec4899',
        'violet-neon': '#8b5cf6',
        'acid-green': '#84cc16',
        'navy-deep': '#0f172a',
        'slate-850': '#1e293b',
      },
      animation: {
        'pulse-glow': 'pulse-glow 2s ease-in-out infinite',
        'float': 'float 3s ease-in-out infinite',
        'shimmer': 'shimmer 2s linear infinite',
        'glow-pulse': 'glow-pulse 2s ease-in-out infinite',
      },
      keyframes: {
        'pulse-glow': {
          '0%, 100%': { boxShadow: '0 0 20px rgba(34, 211, 238, 0.4)' },
          '50%': { boxShadow: '0 0 40px rgba(236, 72, 153, 0.6)' },
        },
        'float': {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        'shimmer': {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        'glow-pulse': {
          '0%, 100%': { opacity: '0.5' },
          '50%': { opacity: '1' },
        },
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic': 'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
      },
      boxShadow: {
        'neon-cyan': '0 0 20px rgba(34, 211, 238, 0.4)',
        'neon-pink': '0 0 20px rgba(236, 72, 153, 0.4)',
        'neon-green': '0 0 20px rgba(132, 204, 22, 0.4)',
        'card-hover': '0 20px 60px rgba(34, 211, 238, 0.15)',
        'button-glow': '0 0 30px rgba(34, 211, 238, 0.4)',
      },
      transitionDuration: {
        '150': '150ms',
        '200': '200ms',
        '300': '300ms',
      },
    },
  },
  plugins: [],
}
