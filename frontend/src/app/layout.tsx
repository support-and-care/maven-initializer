import "./globals.css";
import type { Metadata } from "next";
import React from "react";
import { Geist, Geist_Mono } from "next/font/google";
import { ThemeProvider } from "@/components/ThemeProvider";
import { GitHubRibbon } from "@/components/GitHubRibbon";

const geistSans = Geist({
  subsets: ["latin"],
  variable: "--font-geist-sans",
  display: "swap",
});

const geistMono = Geist_Mono({
  subsets: ["latin"],
  variable: "--font-geist-mono",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Apache Maven Initializer - Starter Tool",
  description:
    "Generate production-ready Java project structures for Apache Maven.",
  icons: {
    icon: "/favicon.ico",
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body
        className={`${geistSans.variable} ${geistMono.variable} font-sans antialiased`}
        suppressHydrationWarning
      >
        <ThemeProvider
          attribute="class"
          defaultTheme="light"
          enableSystem
          disableTransitionOnChange
        >
          <div className="relative min-h-screen bg-background">
            <GitHubRibbon />
            <div className="pointer-events-none fixed inset-0 -z-10">
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(157,213,253,0.16),_transparent_55%)]"></div>
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_bottom,_rgba(93,186,159,0.12),_transparent_60%)]"></div>
              <div className="absolute inset-0 bg-[linear-gradient(135deg,_rgba(2,1,68,0.08),_transparent)]"></div>
              <div
                className="absolute inset-0 opacity-[0.25] mix-blend-overlay"
                style={{
                  backgroundImage:
                    'url(\'data:image/svg+xml,%3Csvg width="180" height="180" viewBox="0 0 180 180" fill="none" xmlns="http://www.w3.org/2000/svg"%3E%3Cpath d="M0 180L180 0" stroke="rgba(148, 163, 184, 0.18)"/%3E%3C/svg%3E\')',
                }}
              />
            </div>
            {children}
          </div>
        </ThemeProvider>
      </body>
    </html>
  );
}
