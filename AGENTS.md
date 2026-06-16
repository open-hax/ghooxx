# Agent Skills Context

## Project

`ghooxx` — GitHub webhook dispatcher and OpenCode notifier.

## RELEVANT SKILLS

- `opencode-plugin-authoring` — for OpenCode plugin packaging and tool registration.
- `shadow-cljs-debug` — if the CLJS builds fail.
- `pm2-process-management` — for running the receiver under PM2.

## Technology stack

- ClojureScript (all new code)
- Shadow-CLJS for both plugin (`:esm`) and server (`:node-script`) builds
- Fastify for the receiver HTTP server
- Zod for tool argument schemas
- pnpm for package management

## Architecture notes

- `src/ghooxx/plugin/` is the OpenCode plugin. It only adds tools.
- `src/ghooxx/server/` is the webhook receiver and API. It is a separate runtime service.
- The plugin talks to the server over HTTP at `GHOOXX_URL` (default `http://127.0.0.1:7999`).
- The server stores events and watches in memory; replace with Redis/Postgres before running at scale.
- Host Proxx is for development only and must never be reverse-proxied publicly.

## Build commands

```bash
pnpm install
pnpm build            # release both plugin and server
pnpm watch:server     # dev mode server
pnpm watch:plugin     # dev mode plugin
pnpm plugin:link      # symlink dist/plugin.js to .opencode/plugins/ghooxx.js
pnpm start            # run built server
```

## Output contract

For every assistant response, satisfy the active output contract (Signal, Evidence, Frames, Countermoves, Next).
EOFEOF
EOFEOF
