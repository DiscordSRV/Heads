import {
  DEFAULT_OPTIONS,
  DEFAULT_PLAYER_SIZE,
  DEFAULT_SIZE,
  EXAMPLE_FLOODGATE_UUID,
  EXAMPLE_TARGET,
  EXAMPLE_UUID,
  MAX_SIZE,
  STEVE_TEXTURE_ID,
  imageUrl,
  renderPath,
  type RenderOptions,
} from "@/lib/api";
import { Origin } from "@/components/Origin";
import { Playground } from "@/components/Playground";
import { PlaygroundProvider } from "@/components/PlaygroundContext";
import { ExampleTarget } from "@/components/RenderTile";
import { AngleGallery, RouteCatalog } from "@/components/RouteCatalog";

const GITHUB = "https://github.com/DiscordSRV/Heads";

const SHOWCASE: { options: Partial<RenderOptions>; alt: string }[] = [
  { options: { family: "head", helmet: "helm" }, alt: "Head" },
  { options: { family: "skull", projection: "isometric" }, alt: "Isometric skull" },
  { options: { family: "player", yaw: 20, pitch: 10 }, alt: "Player" },
  { options: { family: "body", helmet: "helm" }, alt: "Body" },
  { options: { family: "skull" }, alt: "Skull" },
  { options: { family: "bust", helmet: "helm" }, alt: "Bust" },
];

const HIGHLIGHTS = [
  "Java and Bedrock (via Geyser) players",
  "Any camera angle for 3D renders",
  "Up to 512 px, anti-aliased",
  "Slim (Alex) arms",
  "Straight from Mojang, with a CraftHead fallback",
];

function render(target: string, options: Partial<RenderOptions>, size: number): string {
  return imageUrl(renderPath(target, { ...DEFAULT_OPTIONS, ...options, size }));
}

const TARGETS: { kind: string; example: string; description: string }[] = [
  {
    kind: "UUID",
    example: EXAMPLE_UUID,
    description: "Dashed or non-dashed. Preferred, since UUIDs never change.",
  },
  {
    kind: "Username",
    example: EXAMPLE_TARGET,
    description: "Resolved to a UUID through Mojang. Username → UUID mappings are cached for one hour.",
  },
  {
    kind: "Texture ID",
    example: STEVE_TEXTURE_ID,
    description: "The hash at the end of a textures.minecraft.net URL. The slim model is detected from the texture itself.",
  },
  {
    kind: "Floodgate UUID",
    example: EXAMPLE_FLOODGATE_UUID,
    description:
      "Bedrock players joining through Geyser. The last 16 hex digits are the player's XUID, and the skin comes from the GeyserMC global API, which only knows skins of players it has seen join a Geyser server. Unknown players get the default Steve texture.",
  },
  {
    kind: "Offline UUID",
    example: "e2a5b2c1-7f53-3b8e-9d8a-1c2e3f4a5b6c",
    description: "Any other UUID that isn't version 4, such as offline-mode servers' UUIDs, always renders the default Steve texture.",
  },
];

export default function Home() {
  return (
    <PlaygroundProvider>
      <header className="nav">
        <div className="container nav-inner">
          <a href="#" className="brand">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img src="/favicon.ico" alt="" width={28} height={28} />
            DiscordSRV Heads
          </a>
          <nav>
            <a href="#try">Try it</a>
            <a href="#usage">Usage</a>
            <a href="#types">Types</a>
            <a href="#camera">Camera</a>
            <a href="#targets">Targets</a>
            <a href={GITHUB} target="_blank" rel="noreferrer">
              GitHub
            </a>
          </nav>
        </div>
      </header>

      <main className="container">
        <section className="hero">
          <div className="hero-text">
            <p className="eyebrow">The avatar API behind DiscordSRV</p>
            <h1>
              Any player. Any angle.
              <br />
              <span className="gradient">One URL.</span>
            </h1>
            <p className="lead">
              Heads, busts, bodies, 3D skulls and full 3D player renders for any Java or Bedrock player. Put a name or UUID
              in a URL and get a PNG back, ready for Discord embeds, websites and READMEs. No API key, no setup.
            </p>
            <pre className="hero-url">
              <Origin />
              <span className="url-target">/{EXAMPLE_TARGET}</span>
              <span className="url-route">/player</span>
            </pre>
            <ul className="highlights">
              {HIGHLIGHTS.map((highlight) => (
                <li key={highlight}>{highlight}</li>
              ))}
            </ul>
            <div className="hero-actions">
              <a className="button button-primary" href="#try">
                Try it yourself
              </a>
              <a className="button" href="#types">
                Browse render types
              </a>
              <a className="button button-ghost" href={GITHUB} target="_blank" rel="noreferrer">
                View on GitHub
              </a>
            </div>
          </div>
          <div className="showcase" aria-hidden>
            {SHOWCASE.map((item) => (
              // eslint-disable-next-line @next/next/no-img-element
              <img key={item.alt} src={render(EXAMPLE_TARGET, item.options, 256)} alt={item.alt} />
            ))}
          </div>
        </section>

        <Playground />

        <section id="usage" className="section">
          <div className="section-heading">
            <h2>Usage</h2>
            <p>Every render is a single GET request, so it can be dropped straight into an embed, an img tag or Markdown.</p>
          </div>
          <pre className="anatomy">
            <span className="url-method">GET </span>
            <Origin />
            <span className="url-target">/&lt;target&gt;</span>
            <span className="url-route">/&lt;type&gt;</span>
            <span className="url-size">[/&lt;size&gt;]</span>
            <span className="url-query">[?yaw=&lt;degrees&gt;&amp;pitch=&lt;degrees&gt;]</span>
          </pre>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Part</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <code className="url-target">&lt;target&gt;</code>
                  </td>
                  <td>
                    A UUID (dashed or non-dashed), a username, or a texture ID. See <a href="#targets">targets</a>.
                  </td>
                </tr>
                <tr>
                  <td>
                    <code className="url-route">&lt;type&gt;</code>
                  </td>
                  <td>
                    One of the <a href="#types">render types</a> below.
                  </td>
                </tr>
                <tr>
                  <td>
                    <code className="url-size">&lt;size&gt;</code>
                  </td>
                  <td>
                    Width of the image in pixels, defaulting to <code>{DEFAULT_PLAYER_SIZE}</code> for <code>player</code>{" "}
                    and <code>{DEFAULT_SIZE}</code> for everything else, and capped at <code>{MAX_SIZE}</code>.
                    Non-square images keep their aspect ratio. For <code>texture</code>, it&apos;s rounded to the nearest
                    multiple of <code>64</code>.
                  </td>
                </tr>
                <tr>
                  <td>
                    <code className="url-query">yaw</code>, <code className="url-query">pitch</code>
                  </td>
                  <td>
                    Camera angles in degrees for the 3D types, <code>skull</code> and <code>player</code>. See{" "}
                    <a href="#camera">camera angles</a>.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <ul className="facts">
            <li>
              <strong>Always PNG.</strong> <code>head</code> and <code>overlay</code> are opaque RGB; everything else has a
              transparent background.
            </li>
            <li>
              <strong>Slim skins.</strong> Alex-model skins get 3 px wide arms in <code>bust</code>, <code>body</code> and{" "}
              <code>player</code>. The model comes from the player&apos;s profile, or is detected from the texture.
            </li>
            <li>
              <strong>Unknown players.</strong> Usernames, Java UUIDs and texture IDs that don&apos;t exist return{" "}
              <code>404 Not Found</code>. Floodgate and offline-mode UUIDs fall back to the default Steve skin instead.
            </li>
          </ul>
        </section>

        <section id="types" className="section">
          <div className="section-heading">
            <h2>Render types</h2>
            <p>
              Examples render <ExampleTarget />, or whichever player you last rendered in the playground; click any of them
              to open it there. Routes listed together are aliases that render the same image.
            </p>
          </div>
          <RouteCatalog />
        </section>

        <section id="camera" className="section">
          <div className="section-heading">
            <h2>Camera angles</h2>
            <p>
              <code>skull</code> and <code>player</code> accept <code>yaw</code> and <code>pitch</code> query parameters, in
              degrees, to view the model from any angle.
            </p>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Parameter</th>
                  <th>Range</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <code className="url-query">yaw</code>
                  </td>
                  <td>Any number</td>
                  <td>
                    How far the camera turns around the player. <code>0</code> faces their front, positive turns toward their
                    left (<code>90</code> is their left profile), negative toward their right, and <code>180</code> faces
                    their back.
                  </td>
                </tr>
                <tr>
                  <td>
                    <code className="url-query">pitch</code>
                  </td>
                  <td>
                    <code>-90</code> to <code>90</code>
                  </td>
                  <td>
                    How far the camera looks down. <code>0</code> is level, <code>90</code> looks straight down from above,
                    and negative values look up from below.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p>
            <code>left</code> and <code>right</code> are aliases for each type&apos;s default angles. Any angle that isn&apos;t
            given keeps the route&apos;s default:
          </p>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Route</th>
                  <th>
                    <code>left</code> (default)
                  </th>
                  <th>
                    <code>right</code>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <code>player</code>
                  </td>
                  <td>
                    <code>?yaw=35&amp;pitch=15</code>
                  </td>
                  <td>
                    <code>?yaw=-35&amp;pitch=15</code>
                  </td>
                </tr>
                <tr>
                  <td>
                    <code>skull/isometric</code>
                  </td>
                  <td>
                    <code>?yaw=45&amp;pitch=35.26</code>*
                  </td>
                  <td>
                    <code>?yaw=-45&amp;pitch=35.26</code>*
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <ul className="facts">
            <li>
              * The exact isometric pitch is <code>atan(1/√2)</code> ≈ <code>35.2644°</code>; leave <code>pitch</code> out to
              use it.
            </li>
            <li>
              For example, <code>player?pitch=40</code> is <code>?yaw=35&amp;pitch=40</code>, and{" "}
              <code>player/right?yaw=-60</code> is <code>?yaw=-60&amp;pitch=15</code>.
            </li>
            <li>
              The oblique <code>skull</code> view is a fixed projection rather than a camera angle, so giving either angle
              renders it like <code>skull/isometric</code> instead: <code>skull/helm?yaw=20</code> is the same as{" "}
              <code>skull/isometric/helm?yaw=20</code>.
            </li>
            <li>
              Angles that aren&apos;t numbers, or a <code>pitch</code> outside <code>-90</code> to <code>90</code>, return{" "}
              <code>400 Bad Request</code>.
            </li>
          </ul>
          <AngleGallery />
        </section>

        <section id="targets" className="section">
          <div className="section-heading">
            <h2>Targets</h2>
            <p>
              The target is told apart by its length: up to 16 characters is a username, 32 or 36 is a UUID, and anything else
              is a texture ID.
            </p>
          </div>
          <div className="targets">
            {TARGETS.map((target) => (
              <div key={target.kind} className="panel target">
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img src={render(target.example, { family: "head", helmet: "helm" }, 64)} alt="" width={48} height={48} loading="lazy" />
                <div>
                  <h3>{target.kind}</h3>
                  <code className="target-example">{target.example}</code>
                  <p>{target.description}</p>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>

      <footer className="footer">
        <div className="container footer-inner">
          <span>
            DiscordSRV Heads is open source on <a href={GITHUB}>GitHub</a>.
          </span>
          <span className="muted">Not affiliated with Mojang or Microsoft.</span>
        </div>
      </footer>
    </PlaygroundProvider>
  );
}
