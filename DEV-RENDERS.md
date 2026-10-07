# Local Development
Run the server locally with `./gradlew run`. It listens on [http://localhost:7070](http://localhost:7070).

Opening this file in a Markdown preview while the server is running renders every route below. Set `Heads.DEBUG = true` to log which service resolved each profile and texture.

See [README.md](README.md) for what each route and parameter does. Unlike the README, this file shows every alias as its own image, so you can check that aliases match.

## Flat types
|          | Plain                                                    | `/overlay`                                                       | `/helm`                                                       |
|----------|:--------------------------------------------------------:|:----------------------------------------------------------------:|:-------------------------------------------------------------:|
| Head     | ![](http://localhost:7070/Scarsz/head/64)<br>`head`      | ![](http://localhost:7070/Scarsz/overlay/64)<br>`overlay`        | ![](http://localhost:7070/Scarsz/helm/64)<br>`helm`           |
| `bust`   | ![](http://localhost:7070/Scarsz/bust/64)<br>`bust`      | ![](http://localhost:7070/Scarsz/bust/overlay/64)<br>`bust/overlay` | ![](http://localhost:7070/Scarsz/bust/helm/64)<br>`bust/helm` |
| `body`   | ![](http://localhost:7070/Scarsz/body/64)<br>`body`      | ![](http://localhost:7070/Scarsz/body/overlay/64)<br>`body/overlay` | ![](http://localhost:7070/Scarsz/body/helm/64)<br>`body/helm` |

## Skull
Each row should show the same image in the first two columns.

|                  | Default                                                                         | `/left`                                                                                   | `/right`                                                                                    |
|------------------|:-------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|
| Oblique          | ![](http://localhost:7070/Scarsz/skull/64)<br>`skull`                           | ![](http://localhost:7070/Scarsz/skull/left/64)<br>`skull/left`                           | ![](http://localhost:7070/Scarsz/skull/right/64)<br>`skull/right`                           |
| Oblique + helm   | ![](http://localhost:7070/Scarsz/skull/helm/64)<br>`skull/helm`                 | ![](http://localhost:7070/Scarsz/skull/left/helm/64)<br>`skull/left/helm`                 | ![](http://localhost:7070/Scarsz/skull/right/helm/64)<br>`skull/right/helm`                 |
| Isometric        | ![](http://localhost:7070/Scarsz/skull/isometric/64)<br>`skull/isometric`       | ![](http://localhost:7070/Scarsz/skull/isometric/left/64)<br>`skull/isometric/left`       | ![](http://localhost:7070/Scarsz/skull/isometric/right/64)<br>`skull/isometric/right`       |
| Isometric + helm | ![](http://localhost:7070/Scarsz/skull/isometric/helm/64)<br>`skull/isometric/helm` | ![](http://localhost:7070/Scarsz/skull/isometric/left/helm/64)<br>`skull/isometric/left/helm` | ![](http://localhost:7070/Scarsz/skull/isometric/right/helm/64)<br>`skull/isometric/right/helm` |

## Player
The first two images should match.

| Default                                                 | `/left`                                                           | `/right`                                                            |
|:-------------------------------------------------------:|:-----------------------------------------------------------------:|:-------------------------------------------------------------------:|
| ![](http://localhost:7070/Scarsz/player/64)<br>`player` | ![](http://localhost:7070/Scarsz/player/left/64)<br>`player/left` | ![](http://localhost:7070/Scarsz/player/right/64)<br>`player/right` |

## Texture
| `texture`                                 |
|:-----------------------------------------:|
| ![](http://localhost:7070/Scarsz/texture) |

## Camera angles
### Yaw sweep
`pitch=15`, turning all the way around the player.

| `yaw=0`                                                     | `yaw=45`                                                     | `yaw=90`                                                     | `yaw=135`                                                     | `yaw=180`                                                     | `yaw=-135`                                                     | `yaw=-90`                                                     | `yaw=-45`                                                     |
|:-----------------------------------------------------------:|:------------------------------------------------------------:|:------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|:--------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|
| ![](http://localhost:7070/Scarsz/player/64?yaw=0&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=45&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=90&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=135&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=180&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=-135&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=-90&pitch=15)  | ![](http://localhost:7070/Scarsz/player/64?yaw=-45&pitch=15)  |
| ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=0&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=45&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=90&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=135&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=180&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=-135&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=-90&pitch=15) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=-45&pitch=15) |

### Pitch sweep
`yaw=35`, from straight below to straight above.

| `pitch=-90`                                                  | `pitch=-45`                                                  | `pitch=0`                                                   | `pitch=45`                                                   | `pitch=90`                                                   |
|:------------------------------------------------------------:|:------------------------------------------------------------:|:-----------------------------------------------------------:|:------------------------------------------------------------:|:------------------------------------------------------------:|
| ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=-90) | ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=-45) | ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=0)  | ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=45)  | ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=90)  |
| ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=35&pitch=-90) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=35&pitch=-45) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=35&pitch=0) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=35&pitch=45) | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=35&pitch=90) |

### Defaults
An angle that isn't given keeps the route's default. Each row should show the same image in both columns.

| Partial angles                                                                           | Equivalent full angles                                                                          |
|:----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|
| ![](http://localhost:7070/Scarsz/player/64?pitch=40)<br>`player?pitch=40`                | ![](http://localhost:7070/Scarsz/player/64?yaw=35&pitch=40)<br>`player?yaw=35&pitch=40`         |
| ![](http://localhost:7070/Scarsz/player/right/64?yaw=-60)<br>`player/right?yaw=-60`      | ![](http://localhost:7070/Scarsz/player/64?yaw=-60&pitch=15)<br>`player?yaw=-60&pitch=15`       |
| ![](http://localhost:7070/Scarsz/player/right/64)<br>`player/right`                      | ![](http://localhost:7070/Scarsz/player/64?yaw=-35&pitch=15)<br>`player?yaw=-35&pitch=15`       |
| ![](http://localhost:7070/Scarsz/skull/isometric/right/helm/64)<br>`skull/isometric/right/helm` | ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=-45)<br>`skull/helm?yaw=-45`                 |
| ![](http://localhost:7070/Scarsz/skull/helm/64?yaw=20)<br>`skull/helm?yaw=20`            | ![](http://localhost:7070/Scarsz/skull/isometric/helm/64?yaw=20)<br>`skull/isometric/helm?yaw=20` |
| ![](http://localhost:7070/Scarsz/skull/right/64?pitch=0)<br>`skull/right?pitch=0`        | ![](http://localhost:7070/Scarsz/skull/64?yaw=-45&pitch=0)<br>`skull?yaw=-45&pitch=0`           |

### Errors
Each of these should return `400 Bad Request`.

| URL                                                     | Reason                  |
|---------------------------------------------------------|-------------------------|
| http://localhost:7070/Scarsz/player?pitch=91            | `pitch` above `90`      |
| http://localhost:7070/Scarsz/player?pitch=-91           | `pitch` below `-90`     |
| http://localhost:7070/Scarsz/player?yaw=abc             | `yaw` isn't a number    |
| http://localhost:7070/Scarsz/skull/helm?yaw=NaN         | `yaw` isn't finite      |
| http://localhost:7070/Scarsz/skull/isometric?pitch=Infinity | `pitch` isn't finite |

## Targets
Each route accepts a username, a UUID (dashed or non-dashed), or a texture ID.

| Image                                                                                                         | Target                    | URL                                                                                                   |
|---------------------------------------------------------------------------------------------------------------|---------------------------|-------------------------------------------------------------------------------------------------------|
| ![](http://localhost:7070/Scarsz/overlay/64)                                                                  | Username                  | http://localhost:7070/Scarsz/overlay                                                                  |
| ![](http://localhost:7070/d7c1db4d-e57b-488b-b8bc-4462fe49a3e8/overlay/64)                                    | Dashed UUID               | http://localhost:7070/d7c1db4d-e57b-488b-b8bc-4462fe49a3e8/overlay                                    |
| ![](http://localhost:7070/d7c1db4de57b488bb8bc4462fe49a3e8/overlay/64)                                        | Non-dashed UUID           | http://localhost:7070/d7c1db4de57b488bb8bc4462fe49a3e8/overlay                                        |
| ![](http://localhost:7070/9f954e93fe1640f47e916c26622eacf92fd4586371f5f07fd9a7ddedaf4/overlay/64)             | Texture ID (Steve)        | http://localhost:7070/9f954e93fe1640f47e916c26622eacf92fd4586371f5f07fd9a7ddedaf4/overlay             |
| ![](http://localhost:7070/00000000-0000-3000-8000-000000000000/overlay/64)                                    | Offline-mode UUID (Steve) | http://localhost:7070/00000000-0000-3000-8000-000000000000/overlay                                    |

## Sizes
`<size>` sets the output width (default `256` for `player`, `64` otherwise, max `512`). `texture` rounds to the nearest multiple of `64`.

| Image                                          | URL                                        |
|------------------------------------------------|--------------------------------------------|
| ![](http://localhost:7070/Scarsz/helm/16)      | http://localhost:7070/Scarsz/helm/16       |
| ![](http://localhost:7070/Scarsz/helm/128)     | http://localhost:7070/Scarsz/helm/128      |
| ![](http://localhost:7070/Scarsz/player/256)   | http://localhost:7070/Scarsz/player/256    |
| ![](http://localhost:7070/Scarsz/texture/128)  | http://localhost:7070/Scarsz/texture/128   |
| ![](http://localhost:7070/Scarsz/player/right/128?yaw=-120&pitch=20) | http://localhost:7070/Scarsz/player/right/128?yaw=-120&pitch=20 |
