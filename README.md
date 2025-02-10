# DiscordSRV Heads
No frills Minecraft headshot provider. Retrieves profiles & textures directly from Mojang, falling back to [CraftHead](https://crafthead.net/) if/when issues with Mojang's API are encountered.

## Usage
`GET https://heads.discordsrv.com/<uuid|username>/<head|overlay|helm|texture>/<size?>`
- Image types:
    - `head`: plain head from the texture.
    - `overlay`: `head` overlaid with the `helm` layer.
    - `helm`: same as `overlay` except the helmet is scaled up to resemble how heads are rendered in-game.
    - `texture`: raw texture sheet without any processing
- `head` and `overlay` heads are internally rendered as 8x8<sub>px</sub>; `helm` is rendered using 64x64<sub>px</sub> heads to accommodate the scaled `helm` layer.
- Resulting PNGs from `head` and `overlay` are RGB (no alpha channel/transparency); `helm` uses ARGB (transparency under the scaled up helmet).
- UUIDs (dashed or non-dashed) are preferred; `usernames -> UUID` mappings are cached for one hour.

### Examples
| Image                                               | URL                                         |
|-----------------------------------------------------|---------------------------------------------|
| ![](https://heads.discordsrv.com/Scarsz/head/64)    | https://heads.discordsrv.com/Scarsz/head    |
| ![](https://heads.discordsrv.com/Scarsz/overlay/64) | https://heads.discordsrv.com/Scarsz/overlay |
| ![](https://heads.discordsrv.com/Scarsz/helm/64)    | https://heads.discordsrv.com/Scarsz/helm    |
| ![](https://heads.discordsrv.com/Scarsz/texture)    | https://heads.discordsrv.com/Scarsz/texture |

<sub>The above images are displayed with `/64` to request the head at 64x64 resolution.
In the above URLs, `Scarsz` could be replaced with the UUID `d7c1db4d-e57b-488b-b8bc-4462fe49a3e8` for the same results.</sub>
