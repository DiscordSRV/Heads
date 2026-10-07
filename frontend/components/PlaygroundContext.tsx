"use client";

import { createContext, useCallback, useContext, useMemo, useState, type Dispatch, type ReactNode, type SetStateAction } from "react";
import { DEFAULT_OPTIONS, EXAMPLE_TARGET, type RenderOptions } from "@/lib/api";

interface Playground {
  target: string;
  setTarget: (target: string) => void;
  options: RenderOptions;
  setOptions: Dispatch<SetStateAction<RenderOptions>>;
  /** The last target the playground rendered successfully, which the examples elsewhere on the page show. */
  exampleTarget: string;
  setExampleTarget: (target: string) => void;
  /** Loads a render into the playground and scrolls to it. */
  open: (options: RenderOptions, target?: string) => void;
}

const PlaygroundContext = createContext<Playground | null>(null);

export const PLAYGROUND_ID = "try";

/** Keeps yaw within the playground slider's range; any angle is valid, so this doesn't change the render. */
export function wrapYaw(yaw: number): number {
  return ((((yaw + 180) % 360) + 360) % 360) - 180;
}

export function PlaygroundProvider({ children }: { children: ReactNode }) {
  const [target, setTarget] = useState(EXAMPLE_TARGET);
  const [options, setOptions] = useState<RenderOptions>(DEFAULT_OPTIONS);
  const [exampleTarget, setExampleTarget] = useState(EXAMPLE_TARGET);

  const open = useCallback((next: RenderOptions, nextTarget?: string) => {
    setOptions({ ...next, yaw: next.yaw === null ? null : wrapYaw(next.yaw) });
    if (nextTarget !== undefined) setTarget(nextTarget);
    document.getElementById(PLAYGROUND_ID)?.scrollIntoView({ behavior: "smooth", block: "start" });
  }, []);

  const value = useMemo(
    () => ({ target, setTarget, options, setOptions, exampleTarget, setExampleTarget, open }),
    [target, options, exampleTarget, open],
  );
  return <PlaygroundContext.Provider value={value}>{children}</PlaygroundContext.Provider>;
}

export function usePlayground(): Playground {
  const playground = useContext(PlaygroundContext);
  if (!playground) throw new Error("usePlayground must be used inside a PlaygroundProvider");
  return playground;
}
