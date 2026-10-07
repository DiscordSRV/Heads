import { EXAMPLE_TARGET, renderParts } from "@/lib/api";
import { ANGLE_EXAMPLES, ROUTE_FAMILIES } from "@/lib/routes";
import { RenderTile } from "./RenderTile";

/** Renders `backticked` spans as code. */
function inlineCode(text: string) {
  return text.split("`").map((part, i) => (i % 2 ? <code key={i}>{part}</code> : part));
}

export function RouteCatalog() {
  return (
    <div className="families">
      {ROUTE_FAMILIES.map((family) => (
        <article key={family.id} id={`type-${family.id}`} className="panel family">
          <header>
            <h3>{family.title}</h3>
            <p>{family.description}</p>
            {family.syntax && <pre className="syntax">{family.syntax}</pre>}
          </header>
          {family.notes && (
            <ul className="notes">
              {family.notes.map((note) => (
                <li key={note}>{inlineCode(note)}</li>
              ))}
            </ul>
          )}
          <div className="tiles">
            {family.variants.map((variant) => (
              <RenderTile
                key={variant.routes[0]}
                options={variant.options}
                label={variant.routes[0]}
                aliases={variant.routes.slice(1)}
                caption={variant.caption}
              />
            ))}
          </div>
        </article>
      ))}
    </div>
  );
}

export function AngleGallery() {
  return (
    <div className="families">
      {ANGLE_EXAMPLES.map((group) => (
        <article key={group.title} className="panel family">
          <header>
            <h3>{group.title}</h3>
          </header>
          <div className="tiles">
            {group.examples.map((example) => {
              // Only the route and query are shown, so the target doesn't matter
              const parts = renderParts(EXAMPLE_TARGET, example.options);
              return (
                <RenderTile
                  key={example.label}
                  options={example.options}
                  label={parts.route.slice(1) + parts.query}
                  caption={example.label}
                />
              );
            })}
          </div>
        </article>
      ))}
    </div>
  );
}
