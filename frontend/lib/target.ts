export type TargetKind =
  | { kind: "empty" }
  | { kind: "username"; valid: boolean }
  | { kind: "uuid"; variant: "java" | "floodgate" | "offline" | "invalid" }
  | { kind: "texture" };

/** Classifies a target the same way the server does: by length first. */
export function classifyTarget(raw: string): TargetKind {
  const target = raw.trim();
  if (!target) return { kind: "empty" };
  if (target.length <= 16) return { kind: "username", valid: /^[A-Za-z0-9_]{3,16}$/.test(target) };
  if (target.length === 32 || target.length === 36) return { kind: "uuid", variant: uuidVariant(target) };
  return { kind: "texture" };
}

function uuidVariant(uuid: string): "java" | "floodgate" | "offline" | "invalid" {
  const hex = uuid.length === 36 ? (/^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$/i.test(uuid) ? uuid.replace(/-/g, "") : "") : uuid;
  if (!/^[0-9a-f]{32}$/i.test(hex)) return "invalid";
  if (/^0{16}/.test(hex) && !/^0{32}$/.test(hex)) return "floodgate";
  return hex[12] === "4" ? "java" : "offline";
}

export function describeTarget(t: TargetKind): string {
  switch (t.kind) {
    case "empty":
      return "Enter a username, UUID or texture ID.";
    case "username":
      return t.valid
        ? "Username, resolved to a UUID through Mojang (cached for an hour)."
        : "Usernames are 3-16 letters, digits and underscores; this one probably won't resolve.";
    case "uuid":
      switch (t.variant) {
        case "java":
          return "Java Edition UUID, the preferred way to identify players.";
        case "floodgate":
          return "Floodgate UUID: a Bedrock player's skin from the GeyserMC global API.";
        case "offline":
          return "Offline-mode UUID: always renders the default Steve skin.";
        case "invalid":
          return "Not a valid UUID.";
      }
    case "texture":
      return "Texture ID, the hash at the end of a textures.minecraft.net URL.";
  }
}
