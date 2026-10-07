"use client";

import { useEffect, useEffectEvent, useRef, useState } from "react";
import {
  DEFAULT_OPTIONS,
  EXAMPLE_FLOODGATE_UUID,
  EXAMPLE_TARGET,
  EXAMPLE_UUID,
  MAX_SIZE,
  STEVE_TEXTURE_ID,
  defaultAngles,
  defaultSize,
  imageUrl,
  publicUrl,
  renderParts,
  supportsAngles,
  targetOfPath,
  supportsHelmet,
  textureSize,
  type Family,
  type Helmet,
  type Projection,
  type RenderOptions,
  type Side,
} from "@/lib/api";
import { useDebounced, useOrigin } from "@/lib/hooks";
import { classifyTarget, describeTarget } from "@/lib/target";
import { CopyButton } from "./CopyButton";
import { Field, Segmented, Slider, Toggle } from "./Controls";
import { PLAYGROUND_ID, usePlayground, wrapYaw } from "./PlaygroundContext";

const FAMILIES: { value: Family; label: string }[] = [
  { value: "head", label: "Head" },
  { value: "bust", label: "Bust" },
  { value: "body", label: "Body" },
  { value: "skull", label: "Skull" },
  { value: "player", label: "Player" },
  { value: "texture", label: "Texture" },
];

const HELMETS: { value: Helmet; label: string }[] = [
  { value: "none", label: "None" },
  { value: "overlay", label: "Overlay" },
  { value: "helm", label: "Helm" },
];

const SIDES: { value: Side; label: string }[] = [
  { value: "left", label: "Left" },
  { value: "right", label: "Right" },
];

const PROJECTIONS: { value: Projection; label: string }[] = [
  { value: "oblique", label: "Oblique" },
  { value: "isometric", label: "Isometric" },
];

const QUICK_TARGETS: { label: string; target: string }[] = [
  { label: "Technoblade", target: EXAMPLE_TARGET },
  { label: "Scarsz", target: "Scarsz" },
  { label: "Notch", target: "Notch" },
  { label: "jeb_", target: "jeb_" },
  { label: "UUID", target: EXAMPLE_UUID },
  { label: "Bedrock player", target: EXAMPLE_FLOODGATE_UUID },
  { label: "Texture ID", target: STEVE_TEXTURE_ID },
];

const ANGLE_PRESETS: { label: string; yaw: number | null; pitch: number | null }[] = [
  { label: "Default", yaw: null, pitch: null },
  { label: "Front", yaw: 0, pitch: 0 },
  { label: "Left", yaw: 90, pitch: 0 },
  { label: "Right", yaw: -90, pitch: 0 },
  { label: "Back", yaw: 180, pitch: 15 },
  { label: "Top-down", yaw: 0, pitch: 90 },
  { label: "From below", yaw: -30, pitch: -30 },
];

type Background = "dark" | "light" | "checker";
/** Padding around the image in fit mode; matches .preview-fit img. */
const FIT_PADDING = 32;
type Fit = "fit" | "actual";

const SPIN_STEP = 10;

export function Playground() {
  const { target, setTarget, options, setOptions, setExampleTarget } = usePlayground();
  const origin = useOrigin();
  const [background, setBackground] = useState<Background>("checker");
  const [fit, setFit] = useState<Fit>("actual");
  const [spinning, setSpinning] = useState(false);

  const set = (patch: Partial<RenderOptions>) => setOptions((o) => ({ ...o, ...patch }));

  const trimmed = target.trim();
  const kind = classifyTarget(trimmed);
  const angled = supportsAngles(options.family);
  const angles = defaultAngles(options);
  const yaw = options.yaw ?? angles.yaw;
  const pitch = options.pitch ?? angles.pitch;
  const size = options.size ?? defaultSize(options.family);

  const parts = renderParts(trimmed || EXAMPLE_TARGET, options);
  const path = parts.target + parts.route + parts.size + parts.query;
  const url = publicUrl(path, origin);
  const spinFast = spinning && angled;
  const debouncedPath = useDebounced(trimmed ? path : "", spinFast ? 0 : 200);

  // Stop spinning when switching to a type without camera angles
  useEffect(() => {
    if (!angled) setSpinning(false);
  }, [angled]);

  const spinStep = () => setOptions((o) => ({ ...o, yaw: wrapYaw((o.yaw ?? defaultAngles(o).yaw) + SPIN_STEP) }));
  // Each step waits for the previous frame to load, so spinning never queues up requests
  const onLoaded = (loadedPath: string) => {
    // A target that renders is valid, so the examples elsewhere on the page switch to it
    setExampleTarget(targetOfPath(loadedPath));
    if (spinFast) spinStep();
  };

  return (
    <section id={PLAYGROUND_ID} className="section">
      <div className="section-heading">
        <h2>Try it yourself</h2>
        <p>Pick a player and play with every render type and parameter. The URL updates as you go.</p>
      </div>

      <div className="playground">
        <div className="panel playground-controls">
          <Field label="Target" hint={describeTarget(kind)}>
            <input
              className="text-input"
              value={target}
              onChange={(e) => setTarget(e.target.value)}
              placeholder="Username, UUID or texture ID"
              spellCheck={false}
              autoComplete="off"
            />
            <div className="chips">
              {QUICK_TARGETS.map((quick) => (
                <button
                  key={quick.label}
                  type="button"
                  className={quick.target === trimmed ? "chip active" : "chip"}
                  onClick={() => setTarget(quick.target)}
                >
                  {quick.label}
                </button>
              ))}
            </div>
          </Field>

          <Field label="Type">
            <Segmented label="Type" value={options.family} options={FAMILIES} onChange={(family) => set({ family })} />
          </Field>

          {supportsHelmet(options.family) && (
            <Field
              label="Helmet layer"
              hint={
                {
                  none: "Just the base skin layer.",
                  overlay: "The helmet layer drawn flat over the head.",
                  helm: "The helmet layer scaled up around the head, like in-game.",
                }[options.helmet]
              }
            >
              <Segmented label="Helmet layer" value={options.helmet} options={HELMETS} onChange={(helmet) => set({ helmet })} />
            </Field>
          )}

          {options.family === "skull" && (
            <>
              <Field
                label="Projection"
                hint={
                  options.projection === "oblique"
                    ? "The front is drawn flat with the top and side receding diagonally."
                    : "A true isometric view: the top, front and side are identical rhombi."
                }
              >
                <Segmented
                  label="Projection"
                  value={options.projection}
                  options={PROJECTIONS}
                  onChange={(projection) => set({ projection })}
                />
              </Field>
              <div className="field">
                <Toggle checked={options.helm} onChange={(helm) => set({ helm })} label="Helmet layer" />
              </div>
            </>
          )}

          {angled && (
            <Field label="Side" hint="Which of the player's sides the default camera shows.">
              <Segmented label="Side" value={options.side} options={SIDES} onChange={(side) => set({ side })} />
            </Field>
          )}

          <Slider
            label="Size"
            value={size}
            min={8}
            max={MAX_SIZE}
            unit="px"
            isDefault={options.size === null}
            onChange={(v) => set({ size: Math.round(v) })}
            onReset={() => set({ size: null })}
            hint={
              options.family === "texture"
                ? `Textures are scaled by whole multiples of 64, so this renders at ${textureSize(size)} px.`
                : "Width of the image. Non-square renders keep their aspect ratio."
            }
          />

          {angled && (
            <div className="camera">
              <div className="field-header">
                <span className="field-label">Camera</span>
                <Toggle
                  checked={spinning}
                  onChange={(spin) => {
                    setSpinning(spin);
                    if (spin) spinStep();
                  }}
                  label="Spin"
                />
              </div>
              <Slider
                label="Yaw"
                value={yaw}
                min={-180}
                max={180}
                unit="°"
                isDefault={options.yaw === null}
                onChange={(v) => set({ yaw: v })}
                onReset={() => set({ yaw: null })}
                hint="0 faces the front, 90 the player's left side, 180 their back."
              />
              <Slider
                label="Pitch"
                value={pitch}
                min={-90}
                max={90}
                step={0.5}
                unit="°"
                isDefault={options.pitch === null}
                onChange={(v) => set({ pitch: v })}
                onReset={() => set({ pitch: null })}
                hint="0 is level, 90 looks straight down, negative looks up from below."
              />
              <div className="chips">
                {ANGLE_PRESETS.map((preset) => (
                  <button
                    key={preset.label}
                    type="button"
                    className={preset.yaw === options.yaw && preset.pitch === options.pitch ? "chip active" : "chip"}
                    onClick={() => {
                      setSpinning(false);
                      set({ yaw: preset.yaw, pitch: preset.pitch });
                    }}
                  >
                    {preset.label}
                  </button>
                ))}
              </div>
              {options.family === "skull" && options.projection === "oblique" && (options.yaw !== null || options.pitch !== null) && (
                <p className="callout">
                  The oblique skull is a fixed projection, so giving an angle renders it like <code>skull/isometric</code> instead.
                </p>
              )}
            </div>
          )}

          <button
            type="button"
            className="button button-ghost"
            onClick={() => {
              setSpinning(false);
              setOptions({ ...DEFAULT_OPTIONS, family: options.family });
            }}
          >
            Reset parameters
          </button>
        </div>

        <div className="playground-output">
          <div className="panel preview-panel">
            <div className="preview-toolbar">
              <Segmented
                label="Background"
                value={background}
                options={[
                  { value: "checker", label: "Checker" },
                  { value: "dark", label: "Dark" },
                  { value: "light", label: "Light" },
                ]}
                onChange={setBackground}
              />
              <Segmented
                label="Zoom"
                value={fit}
                options={[
                  { value: "fit", label: "Fit" },
                  { value: "actual", label: "1:1" },
                ]}
                onChange={setFit}
              />
            </div>
            <Preview path={debouncedPath} background={background} fit={fit} onLoaded={onLoaded} onError={() => setSpinning(false)} />
          </div>

          <div className="panel url-panel">
            <div className="field-header">
              <span className="field-label">URL</span>
              <span className="url-actions">
                <CopyButton text={url} />
                <a className="button button-small" href={url} target="_blank" rel="noreferrer">
                  Open
                </a>
              </span>
            </div>
            <code className="url">
              <span className="url-origin">{url.slice(0, url.length - path.length)}</span>
              <span className="url-target">{parts.target}</span>
              <span className="url-route">{parts.route}</span>
              <span className="url-size">{parts.size}</span>
              <span className="url-query">{parts.query}</span>
            </code>
            <div className="snippets">
              <CopyButton label="Copy Markdown" text={`![${trimmed}](${url})`} />
              <CopyButton label="Copy HTML" text={`<img src="${url}" alt="${trimmed}">`} />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

type PreviewState =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "loaded" }
  | { status: "error"; message: string };

/**
 * Shows the render at `path`, keeping the previous image on screen until the next one has loaded
 * so that dragging sliders doesn't flicker.
 */
function Preview({
  path,
  background,
  fit,
  onLoaded,
  onError,
}: {
  path: string;
  background: Background;
  fit: Fit;
  onLoaded: (path: string) => void;
  onError: () => void;
}) {
  const [shown, setShown] = useState<{ src: string; width: number; height: number } | null>(null);
  const [state, setState] = useState<PreviewState>({ status: "idle" });
  const loaded = useEffectEvent(onLoaded);
  const failed = useEffectEvent(onError);
  const box = useRef<HTMLDivElement>(null);
  const [boxSize, setBoxSize] = useState<{ width: number; height: number } | null>(null);

  useEffect(() => {
    const observer = new ResizeObserver(([entry]) => setBoxSize(entry.contentRect));
    if (box.current) observer.observe(box.current);
    return () => observer.disconnect();
  }, []);

  // Enlarged renders keep sharp pixels, but shrinking them that way would reintroduce stairstepped edges
  const displayScale =
    shown && boxSize && fit === "fit"
      ? Math.min((boxSize.width - 2 * FIT_PADDING) / shown.width, (boxSize.height - 2 * FIT_PADDING) / shown.height)
      : 1;

  useEffect(() => {
    if (!path) {
      setShown(null);
      setState({ status: "idle" });
      return;
    }
    const src = imageUrl(path);
    const image = new Image();
    const abort = new AbortController();
    setState({ status: "loading" });
    image.onload = () => {
      setShown({ src, width: image.naturalWidth, height: image.naturalHeight });
      setState({ status: "loaded" });
      loaded(path);
    };
    image.onerror = async () => {
      // Images don't expose why they failed, so ask again for the error message (only readable same-origin)
      let message = "The render couldn't be loaded.";
      try {
        const response = await fetch(src, { signal: abort.signal });
        if (!response.ok) message = `${response.status} ${(await response.text()).slice(0, 200) || response.statusText}`;
      } catch {
        if (abort.signal.aborted) return;
      }
      setState({ status: "error", message });
      failed();
    };
    image.src = src;
    return () => {
      abort.abort();
      image.onload = null;
      image.onerror = null;
    };
  }, [path]);

  return (
    <div ref={box} className={`preview preview-${background} preview-${fit}`}>
      {shown && state.status !== "error" && (
        // eslint-disable-next-line @next/next/no-img-element
        <img
          src={shown.src}
          alt="Render preview"
          width={shown.width}
          height={shown.height}
          style={{ imageRendering: displayScale >= 1 ? "pixelated" : "auto" }}
        />
      )}
      {state.status === "idle" && <p className="preview-message">Enter a target to render.</p>}
      {state.status === "error" && <p className="preview-message preview-error">{state.message}</p>}
      {state.status === "loading" && <span className="spinner" aria-label="Loading" />}
      {shown && state.status !== "error" && (
        <span className="preview-dimensions">
          {shown.width} × {shown.height} px
        </span>
      )}
    </div>
  );
}
