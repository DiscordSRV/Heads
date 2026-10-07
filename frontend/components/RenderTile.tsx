"use client";

import { Fragment } from "react";
import { imageUrl, renderPath, type RenderOptions } from "@/lib/api";
import { usePlayground } from "./PlaygroundContext";

/** A route with line break opportunities after each "/". */
function Route({ route, className }: { route: string; className?: string }) {
  return (
    <code className={className}>
      {route.split("/").map((segment, i) => (
        <Fragment key={i}>
          {i > 0 && (
            <>
              /<wbr />
            </>
          )}
          {segment}
        </Fragment>
      ))}
    </code>
  );
}

/** An example render of the playground's last valid target, which opens in the playground when clicked. */
/** The target the examples are rendering. */
export function ExampleTarget() {
  return <code>{usePlayground().exampleTarget}</code>;
}

export function RenderTile({
  options,
  label,
  aliases = [],
  caption,
  size = 128,
}: {
  options: RenderOptions;
  label: string;
  aliases?: string[];
  caption?: string;
  size?: number;
}) {
  const { open, exampleTarget } = usePlayground();
  return (
    <figure className="tile">
      <button type="button" className="tile-image" onClick={() => open(options, exampleTarget)} title="Open in the playground">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src={imageUrl(renderPath(exampleTarget, { ...options, size }))} alt={label} loading="lazy" />
        <span className="tile-try">Try it →</span>
      </button>
      <figcaption>
        <Route route={label} />
        {aliases.map((alias) => (
          <Route key={alias} route={alias} className="alias" />
        ))}
        {caption && <span>{caption}</span>}
      </figcaption>
    </figure>
  );
}
