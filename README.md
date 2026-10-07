# DiscordSRV Heads
No frills Minecraft headshot provider. Retrieves profiles & textures directly from Mojang, falling back to [CraftHead](https://crafthead.net/) if/when issues with Mojang's API are encountered. Bedrock players joining through [Geyser](https://geysermc.org/) are supported via the [GeyserMC global API](https://geysermc.org/wiki/api/api.geysermc.org/global-api/).

## Usage
Try every render type and parameter interactively at [heads.discordsrv.com](https://heads.discordsrv.com/).

```
GET https://heads.discordsrv.com/<target>/<type>[/<size>][?yaw=<degrees>&pitch=<degrees>]
```

| Part           | Description                                                                                                                                                                                                                                                  |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `<target>`     | A UUID (dashed or non-dashed), a username, or a texture ID. UUIDs are preferred; `username -> UUID` mappings are cached for one hour. See [offline UUIDs](#offline-uuids) for non-Mojang UUIDs.                                                              |
| `<type>`       | One of the [image types](#image-types) below.                                                                                                                                                                                                                |
| `<size>`       | Width of the resulting image in pixels, defaulting to `256` for `player` and `64` for everything else, and capped at `512`. Non-square images keep their aspect ratio. For `texture`, it's rounded to the nearest multiple of `64` (between `64` and `512`). |
| `yaw`, `pitch` | Camera angles for 3D types (`skull` and `player`). See [camera angles](#camera-angles).                                                                                                                                                                      |

All images are PNGs. `head` and `overlay` are RGB (no transparency); everything else is ARGB with a transparent background.

Slim (Alex model) skins are rendered with 3px wide arms in `bust`, `body` and `player`. The model comes from the player's profile; when the target is a texture ID, it's detected from the texture instead.

### Offline UUIDs

- **Floodgate UUIDs** (Bedrock players joining through [Geyser](https://geysermc.org/)), such as `00000000-0000-0000-0009-01f64f65c7c3`, carry the player's Xbox ID (XUID) in their last 16 hex digits. Their skin is fetched from the [GeyserMC global API](https://api.geysermc.org/), which only has skins for players it has seen join a Geyser server. Players it has no skin for get the default Steve texture.
- **Other offline-mode UUIDs** (any other version than v4) always resolve to the default Steve texture.

## Image types
<sub>Examples are displayed with `/64` to request them at 64px wide. `Scarsz` could be replaced with the UUID `d7c1db4d-e57b-488b-b8bc-4462fe49a3e8` for the same results.</sub>

### Head
The front face of the head.

|                      `head`                      |                      `overlay`                      |                                       `helm`                                       |
|:------------------------------------------------:|:---------------------------------------------------:|:----------------------------------------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/head/64) | ![](https://heads.discordsrv.com/Scarsz/overlay/64) |                  ![](https://heads.discordsrv.com/Scarsz/helm/64)                  |
|                   Plain head.                    |          Helmet layer drawn over the head.          | Helmet layer scaled up around the head, resembling how heads are rendered in-game. |

### Bust
Flat head plus the upper half of the torso and arms.

|                      `bust`                      |                      `bust/overlay`                      |                      `bust/helm`                      |
|:------------------------------------------------:|:--------------------------------------------------------:|:-----------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/bust/64) | ![](https://heads.discordsrv.com/Scarsz/bust/overlay/64) | ![](https://heads.discordsrv.com/Scarsz/bust/helm/64) |
|                 No helmet layer.                 |            Helmet layer drawn over the head.             |        Helmet layer scaled up around the head.        |

### Body
Flat full body: head, torso, arms and legs.

|                      `body`                      |                      `body/overlay`                      |                      `body/helm`                      |
|:------------------------------------------------:|:--------------------------------------------------------:|:-----------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/body/64) | ![](https://heads.discordsrv.com/Scarsz/body/overlay/64) | ![](https://heads.discordsrv.com/Scarsz/body/helm/64) |
|                 No helmet layer.                 |            Helmet layer drawn over the head.             |        Helmet layer scaled up around the head.        |

### Skull
A 3D skull block, showing the front, top and one side of the head.

```
skull[/isometric][/left|/right][/helm]
```

- `left` (the default) shows the player's left side; `right` shows their right side.
- Without `isometric`, the skull uses an oblique projection: the front face is drawn flat and the top and side recede diagonally. The image is square.
- `isometric` uses a true isometric projection, where the top, front and side are identical rhombi. The image is narrower than it is tall.
- `helm` adds the helmet layer. The plain and `helm` images of a skin are cropped identically, so they line up exactly.
- Supports [camera angles](#camera-angles).

|                  |                                                      Left (default)                                                       |                                                 Right                                                  |
|------------------|:-------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------:|
| Oblique          |                       ![](https://heads.discordsrv.com/Scarsz/skull/64)<br>`skull`<br>`skull/left`                        |                ![](https://heads.discordsrv.com/Scarsz/skull/right/64)<br>`skull/right`                |
| Oblique + helm   |                ![](https://heads.discordsrv.com/Scarsz/skull/helm/64)<br>`skull/helm`<br>`skull/left/helm`                |           ![](https://heads.discordsrv.com/Scarsz/skull/right/helm/64)<br>`skull/right/helm`           |
| Isometric        |        ![](https://heads.discordsrv.com/Scarsz/skull/isometric/64)<br>`skull/isometric`<br>`skull/isometric/left`         |      ![](https://heads.discordsrv.com/Scarsz/skull/isometric/right/64)<br>`skull/isometric/right`      |
| Isometric + helm | ![](https://heads.discordsrv.com/Scarsz/skull/isometric/helm/64)<br>`skull/isometric/helm`<br>`skull/isometric/left/helm` | ![](https://heads.discordsrv.com/Scarsz/skull/isometric/right/helm/64)<br>`skull/isometric/right/helm` |

### Player
A full 3D render of the player in a neutral standing pose, viewed from slightly above.

```
player[/left|/right]
```

- `left` (the default) shows the player's left side; `right` shows their right side.
- Supports [camera angles](#camera-angles).

|                                 Left (default)                                  |                                   Right                                    |
|:-------------------------------------------------------------------------------:|:--------------------------------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/player/64)<br>`player`<br>`player/left` | ![](https://heads.discordsrv.com/Scarsz/player/right/64)<br>`player/right` |

### Texture
The raw texture sheet, without any processing.

|                    `texture`                     |
|:------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/texture) |

## Camera angles
`skull` and `player` accept `yaw` and `pitch` query parameters, in degrees, to view the model from any angle.

| Parameter | Range         | Description                                                                                                                                                                                |
|-----------|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `yaw`     | Any number    | How far the camera turns around the player. `0` faces their front, positive turns toward their left (`90` is their left profile), negative toward their right, and `180` faces their back. |
| `pitch`   | `-90` to `90` | How far the camera looks down. `0` is level, `90` looks straight down from above, and negative values look up from below.                                                                  |

`left` and `right` are aliases for each type's default angles. Any angle that isn't given keeps the route's default:

| Route             | `left` (default)          | `right`                    |
|-------------------|---------------------------|----------------------------|
| `player`          | `?yaw=35&pitch=15`        | `?yaw=-35&pitch=15`        |
| `skull/isometric` | `?yaw=45&pitch=35.26`\*   | `?yaw=-45&pitch=35.26`\*   |

<sub>\* The exact isometric pitch is `atan(1/√2)` ≈ `35.2644°`; leave `pitch` out to use it.</sub>

For example, `player?pitch=40` is `?yaw=35&pitch=40`, and `player/right?yaw=-60` is `?yaw=-60&pitch=15`.

The oblique `skull` view is a fixed projection rather than a camera angle, so giving either angle renders it like `skull/isometric` instead: `skull/helm?yaw=20` is the same as `skull/isometric/helm?yaw=20`.

Angles that aren't numbers, or a `pitch` outside `-90` to `90`, return `400 Bad Request`.

### Player
|                                    Front                                    |                                 Left profile                                 |                                     Back                                     |                                 High angle                                  |                                   From below                                   |
|:---------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:---------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|
|      ![](https://heads.discordsrv.com/Scarsz/player/64?yaw=0&pitch=0)       |      ![](https://heads.discordsrv.com/Scarsz/player/64?yaw=90&pitch=0)       |     ![](https://heads.discordsrv.com/Scarsz/player/64?yaw=180&pitch=15)      |     ![](https://heads.discordsrv.com/Scarsz/player/64?yaw=30&pitch=60)      |      ![](https://heads.discordsrv.com/Scarsz/player/64?yaw=-30&pitch=-30)      |
|                           `player?yaw=0&pitch=0`                            |                           `player?yaw=90&pitch=0`                            |                          `player?yaw=180&pitch=15`                           |                          `player?yaw=30&pitch=60`                           |                           `player?yaw=-30&pitch=-30`                           |

### Skull
|                                Front                                 |                                Shallow                                 |                               Top-down                                |                                  Back                                   |                                 From below                                  |
|:--------------------------------------------------------------------:|:----------------------------------------------------------------------:|:---------------------------------------------------------------------:|:-----------------------------------------------------------------------:|:---------------------------------------------------------------------------:|
| ![](https://heads.discordsrv.com/Scarsz/skull/helm/64?yaw=0&pitch=0) | ![](https://heads.discordsrv.com/Scarsz/skull/helm/64?yaw=30&pitch=10) | ![](https://heads.discordsrv.com/Scarsz/skull/helm/64?yaw=0&pitch=90) | ![](https://heads.discordsrv.com/Scarsz/skull/helm/64?yaw=200&pitch=30) | ![](https://heads.discordsrv.com/Scarsz/skull/helm/64?yaw=-45&pitch=-35.26) |
|                      `skull/helm?yaw=0&pitch=0`                      |                      `skull/helm?yaw=30&pitch=10`                      |                      `skull/helm?yaw=0&pitch=90`                      |                      `skull/helm?yaw=200&pitch=30`                      |                      `skull/helm?yaw=-45&pitch=-35.26`                      |
