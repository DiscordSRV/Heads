/** Where render requests go. Empty in production, since the page is served by the renderer itself. */
export const API_BASE = (process.env.NEXT_PUBLIC_API_BASE ?? "").replace(/\/+$/, "");

/** Shown in copyable URLs when the page isn't running in a browser yet (static export). */
export const PUBLIC_ORIGIN = "https://heads.discordsrv.com";

export const DEFAULT_SIZE = 64;
export const DEFAULT_PLAYER_SIZE = 256;
export const MAX_SIZE = 512;
export const ISOMETRIC_PITCH = (Math.atan(1 / Math.SQRT2) * 180) / Math.PI; // ≈ 35.2644°

export const EXAMPLE_TARGET = "Technoblade";
export const EXAMPLE_UUID = "b876ec32-e396-476b-a115-8438d83c67d4";
export const EXAMPLE_FLOODGATE_UUID = "00000000-0000-0000-0009-01f64f65c7c3";
export const STEVE_TEXTURE_ID = "9f954e93fe1640f47e916c26622eacf92fd4586371f5f07fd9a7ddedaf4";

export type Family = "head" | "bust" | "body" | "skull" | "player" | "texture";
export type Helmet = "none" | "overlay" | "helm";
export type Side = "left" | "right";
export type Projection = "oblique" | "isometric";

export interface RenderOptions {
  family: Family;
  /** Helmet layer for head, bust and body. */
  helmet: Helmet;
  /** Helmet layer for skull. */
  helm: boolean;
  projection: Projection;
  side: Side;
  /** Path size segment; null leaves it out (64 px). */
  size: number | null;
  /** Camera angle overrides for skull and player; null keeps the route's default. */
  yaw: number | null;
  pitch: number | null;
}

export const DEFAULT_OPTIONS: RenderOptions = {
  family: "head",
  helmet: "helm",
  helm: true,
  projection: "oblique",
  side: "left",
  size: null,
  yaw: null,
  pitch: null,
};

/** The width a render gets when the size is left out. */
export function defaultSize(family: Family): number {
  return family === "player" ? DEFAULT_PLAYER_SIZE : DEFAULT_SIZE;
}

export function supportsHelmet(family: Family): boolean {
  return family === "head" || family === "bust" || family === "body";
}

export function supportsAngles(family: Family): boolean {
  return family === "skull" || family === "player";
}

/** The route after the target, e.g. "skull/isometric/right/helm". */
export function routePath(o: RenderOptions): string {
  switch (o.family) {
    case "head":
      return o.helmet === "none" ? "head" : o.helmet;
    case "bust":
    case "body":
      return o.helmet === "none" ? o.family : `${o.family}/${o.helmet}`;
    case "skull":
      return ["skull", o.projection === "isometric" && "isometric", o.side === "right" && "right", o.helm && "helm"]
        .filter(Boolean)
        .join("/");
    case "player":
      return o.side === "right" ? "player/right" : "player";
    case "texture":
      return "texture";
  }
}

/** The camera angles a skull or player route uses when neither angle is given. */
export function defaultAngles(o: Pick<RenderOptions, "family" | "side">): { yaw: number; pitch: number } {
  const sign = o.side === "right" ? -1 : 1;
  return o.family === "player" ? { yaw: 35 * sign, pitch: 15 } : { yaw: 45 * sign, pitch: ISOMETRIC_PITCH };
}

/** Rounds like the server does for texture sizes: to the nearest multiple of 64 between 64 and 512. */
export function textureSize(size: number): number {
  return Math.min(Math.max(Math.floor((size + 32) / 64) * 64, 64), MAX_SIZE);
}

function formatAngle(angle: number): string {
  return String(Math.round(angle * 100) / 100);
}

/** The segments of a render's path, each with its leading "/" or "?", or empty if left out. */
export function renderParts(target: string, o: RenderOptions): { target: string; route: string; size: string; query: string } {
  const query = new URLSearchParams();
  if (supportsAngles(o.family)) {
    if (o.yaw !== null) query.set("yaw", formatAngle(o.yaw));
    if (o.pitch !== null) query.set("pitch", formatAngle(o.pitch));
  }
  return {
    target: `/${encodeURIComponent(target)}`,
    route: `/${routePath(o)}`,
    size: o.size !== null ? `/${o.size}` : "",
    query: query.size > 0 ? `?${query}` : "",
  };
}

/** The path and query of a render, e.g. "/Technoblade/player/128?yaw=0". */
export function renderPath(target: string, o: RenderOptions): string {
  const parts = renderParts(target, o);
  return parts.target + parts.route + parts.size + parts.query;
}

/** The target a render path is for; the inverse of renderPath's first segment. */
export function targetOfPath(path: string): string {
  return decodeURIComponent(path.split("/")[1] ?? "");
}

/** URL for an <img> to load. */
export function imageUrl(path: string): string {
  return API_BASE + path;
}

/** Absolute URL to show and copy, given the page's origin (see useOrigin). */
export function publicUrl(path: string, origin: string): string {
  return (API_BASE || origin) + path;
}
