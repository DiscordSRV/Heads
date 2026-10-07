import type { Metadata, Viewport } from "next";
import type { ReactNode } from "react";
import "./globals.css";

export const metadata: Metadata = {
  title: "DiscordSRV Heads",
  description:
    "No frills Minecraft avatar renderer: heads, busts, bodies, 3D skulls and full player renders for any Java or Bedrock player, straight from a URL.",
};

export const viewport: Viewport = {
  themeColor: "#0b0d12",
  colorScheme: "dark",
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
