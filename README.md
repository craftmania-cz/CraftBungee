# CraftBungee

## Permisse
- `craftbungee.queue.ignore` - ignorace fronty
- `craftbungee.completions.whitelist`
- `craftbungee.completions.blacklist`

## Dependencies
- HikariCP
- Unirest-java
- OkHttp3
- Jetty-Server
- SimpleClient
- SLF4J
- BungeeCord API
- Waterfall API
- nuVotifier
- Litebans API

**[!]** LiteBans API musí být ručně nainstalován do lokální maven repository, protože nelze načíst z Jitpacku.
```git
git clone https://gitlab.com/ruany/LiteBansAPI.git && cd LiteBansAPI && mvn install
```