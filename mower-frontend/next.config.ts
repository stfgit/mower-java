import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone',
  // API URL will be overridden by environment variable in Kubernetes
  env: {
    API_BASE_URL: process.env.API_BASE_URL || 'http://localhost:8080'
  }
};

export default nextConfig;
