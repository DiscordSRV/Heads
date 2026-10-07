"use client";

import { useEffect, useState, useSyncExternalStore } from "react";
import { PUBLIC_ORIGIN } from "./api";

const noSubscribe = () => () => {};

/** The page's origin, or the public site's while prerendering. */
export function useOrigin(): string {
  return useSyncExternalStore(
    noSubscribe,
    () => window.location.origin,
    () => PUBLIC_ORIGIN,
  );
}

/** `value`, once it has stopped changing for `delay` ms. */
export function useDebounced<T>(value: T, delay: number): T {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const timeout = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timeout);
  }, [value, delay]);
  return debounced;
}
