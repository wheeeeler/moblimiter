# MobLimiter — Minecraft 1.21.1 (NeoForge)

Basic chunkbased mob limiting mod for **servers**, incl. **hybrid jars**, to reduce unreasonable amounts of mobs in a lazy way

---

## USECASE?

- **Mob limit per chunk** (default: 10, configurable via `moblim-common.toml` in config dir)
- **Permission controlled commands**:
    - `LuckPerms` supported (via Bukkit)
    - Fallback: Minecraft **OP level 4**
- On-the-fly configuration:
    - `/moblimiter enable`
    - `/moblimiter disable`
    - `/moblimiter setlimit <val>`

---

## BUILD INSTRUCTION

```bash
./gradlew build
