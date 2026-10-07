import type { NextConfig } from "next";

// Exported as static files, which Gradle bundles into the jar for Javalin to serve
const nextConfig: NextConfig = {
  output: "export",
  images: { unoptimized: true },
};

export default nextConfig;
