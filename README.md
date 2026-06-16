# ghooxx

GitHub webhook dispatcher and OpenCode notifier.

## What it is

`ghooxx` sits between GitHub and OpenCode:

1. **Receiver service** — receives GitHub webhooks, stores/normalizes events, and exposes an API.
2. **OpenCode plugin** — adds `github_notify` and `github_watch` tools that talk to the receiver.

Later, this API surface will be compatible with Sol's opencode-style API so agents can subscribe to PR/issue/review events instead of polling.

## Repo layout

```
src/ghooxx/
  plugin/          # OpenCode plugin (Shadow-CLJS → dist/plugin.js)
    entry.cljs
    client.cljs
    schema.cljs
  server/          # Webhook receiver + API (Shadow-CLJS → dist/server.cjs)
    main.cljs
    webhooks.cljs
    api.cljs
    store.cljs
services/ghooxx/   # Runtime/devops
  ecosystem.config.cjs
  docker-compose.yml
  .env.example
```

## Development

Install dependencies:

```bash
pnpm install
```

Build both plugin and server:

```bash
pnpm build
```

Run the receiver locally:

```bash
pnpm start
# or
pnpm watch:server
```

Copy/link the plugin into OpenCode:

```bash
pnpm plugin:link
```

## Runtime

### PM2

```bash
cd services/ghooxx
pm2 start ecosystem.config.cjs
```

### Docker Compose

```bash
cd services/ghooxx
cp .env.example .env
docker compose up -d
```

## Webhook setup

Point your GitHub repository webhook at:

```
https://<your-host>/webhook/github
```

Configure a webhook secret and set `GHOOXX_GITHUB_SECRET` to verify signatures (signature verification is a TODO).

## Tools

### `github_notify`

Send a one-time notification/event into ghooxx from OpenCode.

Args:
- `repo`: repository full name, e.g. `open-hax/eta-mu`
- `event`: event type, e.g. `pr_review`
- `payload`: arbitrary event payload
- `channels`: optional array of channels (default `["default"]`)

### `github_watch`

Register or list watches for GitHub events.

Args:
- `action`: `add` or `list`
- `repo`: repository full name
- `events`: optional array of event types to watch
- `channel`: optional channel name

## License

GPL-3.0-only
