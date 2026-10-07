"use client";

import { API_BASE } from "@/lib/api";
import { useOrigin } from "@/lib/hooks";

/** The origin renders are served from, e.g. "https://heads.discordsrv.com". */
export function Origin() {
  const origin = useOrigin();
  return <>{API_BASE || origin}</>;
}
