# Loopwork frontend web

Mobile-first React PWA for the SLMS backend. It includes Commander, Admin, and Technician experiences, light/dark themes, shop management, request creation, assignment, and status progression.

## Run it

```bash
npm install
npm run dev
```

By default the app opens in demo mode with interactive seed data. To connect it to the Spring Boot backend, copy `.env.example` to `.env`, set `VITE_DEMO_MODE=false`, then set `VITE_API_URL` and a valid Clerk JWT in `VITE_API_TOKEN`.

For production/mobile installation:

```bash
npm run build
```

Deploy the generated `dist` folder over HTTPS, then use the browser's **Add to Home Screen** option on mobile. The included manifest and service worker enable standalone installation and a basic offline app shell.

## Backend contracts used

- `GET /api/commander/dashboard`, `GET /api/admin/dashboard`
- Shop CRUD under `/api/shops`
- Requests under `/api/tasks`, including technician assignment and status updates
- Users under `/api/user`

The form values match the backend enums: `SALES`, `SERVICE`, `COMPLAINT`; `LOW`, `NORMAL`, `HIGH`, `URGENT`; and `CREATED`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
