import { DEFAULT_OPTIONS, type RenderOptions } from "./api";

export interface Variant {
  /** Every route that renders this variant, preferred first. */
  routes: string[];
  caption: string;
  options: RenderOptions;
}

export interface RouteFamily {
  id: string;
  title: string;
  description: string;
  /** Syntax line shown for families with combinable segments. */
  syntax?: string;
  /** Bullet points, with `backticks` around code. */
  notes?: string[];
  variants: Variant[];
}

function opts(partial: Partial<RenderOptions>): RenderOptions {
  return { ...DEFAULT_OPTIONS, ...partial };
}

function flat(family: "bust" | "body"): Variant[] {
  return [
    { routes: [family], caption: "No helmet layer.", options: opts({ family, helmet: "none" }) },
    { routes: [`${family}/overlay`], caption: "Helmet layer drawn over the head.", options: opts({ family, helmet: "overlay" }) },
    { routes: [`${family}/helm`], caption: "Helmet layer scaled up around the head.", options: opts({ family, helmet: "helm" }) },
  ];
}

export const ROUTE_FAMILIES: RouteFamily[] = [
  {
    id: "head",
    title: "Head",
    description: "The front face of the head. These are RGB images with no transparency.",
    variants: [
      { routes: ["head"], caption: "Plain head.", options: opts({ family: "head", helmet: "none" }) },
      { routes: ["overlay"], caption: "Helmet layer drawn over the head.", options: opts({ family: "head", helmet: "overlay" }) },
      {
        routes: ["helm"],
        caption: "Helmet layer scaled up around the head, like heads are drawn in-game.",
        options: opts({ family: "head", helmet: "helm" }),
      },
    ],
  },
  {
    id: "bust",
    title: "Bust",
    description: "Flat head plus the upper half of the torso and arms.",
    variants: flat("bust"),
  },
  {
    id: "body",
    title: "Body",
    description: "Flat full body: head, torso, arms and legs.",
    variants: flat("body"),
  },
  {
    id: "skull",
    title: "Skull",
    description: "A 3D skull block showing the front, top and one side of the head.",
    syntax: "skull[/isometric][/left|/right][/helm]",
    notes: [
      "`left` (the default) shows the player's left side; `right` shows their right side.",
      "Without `isometric`, the skull uses an oblique projection: the front face is drawn flat and the top and side recede diagonally. The image is square.",
      "`isometric` uses a true isometric projection, where the top, front and side are identical rhombi. The image is narrower than it is tall.",
      "`helm` adds the helmet layer. The plain and `helm` images of a skin are cropped identically, so they line up exactly.",
      "Supports camera angles.",
    ],
    variants: [
      { routes: ["skull", "skull/left"], caption: "Oblique, left.", options: opts({ family: "skull", helm: false }) },
      { routes: ["skull/right"], caption: "Oblique, right.", options: opts({ family: "skull", helm: false, side: "right" }) },
      { routes: ["skull/helm", "skull/left/helm"], caption: "Oblique with helmet, left.", options: opts({ family: "skull" }) },
      { routes: ["skull/right/helm"], caption: "Oblique with helmet, right.", options: opts({ family: "skull", side: "right" }) },
      {
        routes: ["skull/isometric", "skull/isometric/left"],
        caption: "Isometric, left.",
        options: opts({ family: "skull", helm: false, projection: "isometric" }),
      },
      {
        routes: ["skull/isometric/right"],
        caption: "Isometric, right.",
        options: opts({ family: "skull", helm: false, projection: "isometric", side: "right" }),
      },
      {
        routes: ["skull/isometric/helm", "skull/isometric/left/helm"],
        caption: "Isometric with helmet, left.",
        options: opts({ family: "skull", projection: "isometric" }),
      },
      {
        routes: ["skull/isometric/right/helm"],
        caption: "Isometric with helmet, right.",
        options: opts({ family: "skull", projection: "isometric", side: "right" }),
      },
    ],
  },
  {
    id: "player",
    title: "Player",
    description: "A full 3D render of the player in a neutral standing pose, viewed from slightly above.",
    syntax: "player[/left|/right]",
    notes: ["`left` (the default) shows the player's left side; `right` shows their right side.", "Supports camera angles."],
    variants: [
      { routes: ["player", "player/left"], caption: "Left (default).", options: opts({ family: "player" }) },
      { routes: ["player/right"], caption: "Right.", options: opts({ family: "player", side: "right" }) },
    ],
  },
  {
    id: "texture",
    title: "Texture",
    description:
      "The raw skin texture sheet, without any processing. Its size is rounded to the nearest multiple of 64, between 64 and 512.",
    variants: [{ routes: ["texture"], caption: "The texture as uploaded.", options: opts({ family: "texture" }) }],
  },
];

export interface AngleExample {
  label: string;
  options: RenderOptions;
}

export const ANGLE_EXAMPLES: { title: string; examples: AngleExample[] }[] = [
  {
    title: "Player",
    examples: [
      { label: "Front", options: opts({ family: "player", yaw: 0, pitch: 0 }) },
      { label: "Left profile", options: opts({ family: "player", yaw: 90, pitch: 0 }) },
      { label: "Back", options: opts({ family: "player", yaw: 180, pitch: 15 }) },
      { label: "High angle", options: opts({ family: "player", yaw: 30, pitch: 60 }) },
      { label: "From below", options: opts({ family: "player", yaw: -30, pitch: -30 }) },
    ],
  },
  {
    title: "Skull",
    examples: [
      { label: "Front", options: opts({ family: "skull", yaw: 0, pitch: 0 }) },
      { label: "Shallow", options: opts({ family: "skull", yaw: 30, pitch: 10 }) },
      { label: "Top-down", options: opts({ family: "skull", yaw: 0, pitch: 90 }) },
      { label: "Back", options: opts({ family: "skull", yaw: 200, pitch: 30 }) },
      { label: "From below", options: opts({ family: "skull", yaw: -45, pitch: -35.26 }) },
    ],
  },
];
