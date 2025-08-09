# MobLimiter — NeoForge 1.21.1

shout out shitgpt for making readme's

Basic chunkbased mob limiting & clearing mod for **servers**, incl. **hybrid jars**, to reduce unreasonable amounts of mobs in a lazy way
---

## Features

- **Per-chunk spawn limiting**
  - Global limit with optional **per-entity** and **per-mod** strict overrides.
  - Whitelisted entities, named entities, and players are never limited.
- **Periodic auto-clear**
  - Trims excess living entities per chunk on a fixed interval.
  - Honors per-entity and per-mod strict clear limits first, then global clear limit.
  - Sends feedback only to users with `moblimiter.feedback`.
- **Runtime configuration via commands**
  - Enable/disable systems, set limits and interval, manage strict rules and whitelist, reload configs/messages, inspect status.
- **Configuration stored as JSON** in a dedicated directory.

---

## Requirements

- **Minecraft:** 1.21.1
- **Loader:** NeoForge
- **Server:** Dedicated or hybrid.
- **Permissions (optional):** LuckPerms via Bukkit on hybrid servers, or the LuckPerms forge mod. If unavailable, permission checks fall back to **OP level 4**.

---

## Permissions

- Admin/root node for commands: `moblimiter.admin`
- Feedback for clear notifications: `moblimiter.feedback`

Permission resolution:
- If a Bukkit layer is present and LuckPerms is enabled: LuckPerms checks via Bukkit API.
- Otherwise: vanilla `ServerPlayer#hasPermissions(4)`.

---

## Commands

Root command is available as `/moblimiter` and alias `/ml`.

### Status & Maintenance
- `/moblimiter status`  
  Shows enablement and current numeric settings.
- `/moblimiter reload`  
  Reloads JSON configs and `ml-messages.properties`.
- `/moblimiter forceclear`  
  Immediately runs a clear on all loaded levels.
- `/moblimiter listentities <all|dimension>`  
  Counts non-player entities either across all levels or a specific level.  
  Examples:
  - `/ml listentities all`
  - `/ml listentities minecraft:overworld`
  - `/ml listentities overworld`

### Update: Auto-clear
- `/moblimiter update autoclear enable`
- `/moblimiter update autoclear disable`
- `/moblimiter update autoclear timer <seconds>` (1–inf)
- `/moblimiter update autoclear limit <value>`
- `/moblimiter update autoclear strictlimit <entity_or_namespace> <value>`
  - `<entity_or_namespace>` accepts a full ID (e.g., `minecraft:allay`) or a namespace (e.g., `minecraft`).

### Update: Entity spawn limit
- `/moblimiter update entitylimit enable`
- `/moblimiter update entitylimit disable`
- `/moblimiter update entitylimit limit <value>`
- `/moblimiter update entitylimit strictlimit <entity_or_namespace> <value>`

### Feedback
- `/ml feedback enable`  
  Opt-in to receiving auto-clear and forceclear notifications. Requires `moblimiter.feedback`.
- `/ml feedback disable`  
  Opt-out of receiving notifications. Requires `moblimiter.feedback`.

### Whitelist
- `/moblimiter whitelist list`
- `/moblimiter whitelist add <entity_id>`
- `/moblimiter whitelist remove <entity_id>`

Notes:
- Strict limits:
  - **Entity key** (e.g., `minecraft:allay`) applies only to that entity.
  - **Namespace key** (e.g., `minecraft`) applies to all entities in that mod.
- Suggestions are provided for resource locations and known namespaces.

---

## Defaults

Default config setup `config/moblimiter/`):

- **Entity spawn limiting:** enabled
- **Auto-clear:** enabled
- **Global spawn limit (per chunk):** `10`
- **Global clear limit (per chunk):** `10`
- **Auto-clear interval:** `300` seconds (5 minutes)
- **Strict per-entity/per-mod rules:** none
- **Whitelist:** empty
- - **Feedback:** off by default

All settings are adjustable at runtime via commands and persisted to JSON.

---

## Configuration Files

Located in `config/moblimiter/`:

- `ml-config.json`
  - `moblimiter.enabled`
  - `mobclear.enabled`
  - `moblimiter.limit`
  - `mobclear.limit`
  - `mobclear.interval_seconds`
- `ml-strict.json`
  - `entitylimit.entities` `{ "<mod:id>": int }`
  - `entitylimit.mods` `{ "<namespace>": int }`
  - `autoclear.entities` `{ "<mod:id>": int }`
  - `autoclear.mods` `{ "<namespace>": int }`
- `ml-whitelist.json`
  - `whitelist` `[ "<mod:id>", ... ]`
- `ml-feedback.json`
  - `enabled` `[ "<player_uuid>", ... ]` — UUIDs of players w/ feedback enabled
- `ml-messages.properties`
  - Copied on first run from `/assets/moblim/messages.properties`.
  - Supports `&` color codes via `MLColor`.


The base directory is resolved from NeoForge’s `FMLPaths.CONFIGDIR` as `config/moblimiter`.

---

## How It Works (Summary)

- **Spawn limiting** (`EntityJoinLevelEvent`):  
  Counts living entities in the same chunk. Uses the effective spawn limit determined by:
  1. Per-entity strict spawn limit,
  2. Per-mod strict spawn limit,
  3. Global spawn limit.
     Whitelist, named entities, and players are ignored by the limiter.

- **Auto-clear** (server tick):  
  On a fixed interval, groups living entities by chunk. Applies:
  1. Per-entity strict clear limits,
  2. Per-mod strict clear limits,
  3. Global clear limit,  
     discarding the overflow. Feedback is sent only to players with `moblimiter.feedback`.

---

## Build

```bash
./gradlew build
