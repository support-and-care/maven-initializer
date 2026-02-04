"use client";

import React from "react";
import Link from "next/link";
import { Header } from "@/components/Header";
import { HeroSection } from "@/components/HeroSection";
import { Footer } from "@/components/Footer";
import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <div className="relative flex h-screen flex-col overflow-hidden">
      <Header />
      <main className="flex-1 overflow-y-auto">
        <div className="relative h-full">
          <div className="absolute inset-0 -z-10 bg-gradient-to-br from-secondary via-secondary/90 to-background" />
          <div className="absolute inset-x-0 top-0 -z-10 h-full bg-[radial-gradient(circle_at_20%_20%,rgba(157,213,253,0.28),transparent_55%)]" />
          <div className="absolute inset-x-0 top-0 -z-10 h-full bg-[radial-gradient(circle_at_80%_10%,rgba(93,186,159,0.28),transparent_60%)]" />
          <div
            className="absolute inset-0 -z-10 opacity-40 mix-blend-overlay"
            style={{
              backgroundImage:
                'url(\'data:image/svg+xml,%3Csvg width="400" height="400" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg"%3E%3Cg opacity="0.4"%3E%3Cpath d="M400 0H0V400" stroke="rgba(255,255,255,0.08)"/%3E%3Cpath d="M0 0L400 400" stroke="rgba(255,255,255,0.05)"/%3E%3C/g%3E%3C/svg%3E\')',
            }}
          />

          <div className="mx-auto h-full max-w-7xl px-4 py-4 lg:px-8">
            <div className="flex h-full items-center justify-center">
              <div className="w-full flex flex-col items-center justify-center text-center">
                <HeroSection />
                <div className="flex justify-center mt-8">
                  <Button
                    asChild
                    className="group inline-flex items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-primary via-primary/90 to-accent px-8 py-3 text-sm font-semibold text-primary-foreground shadow-lg shadow-primary/30 transition-smooth hover:shadow-xl hover:shadow-primary/40"
                  >
                    <Link href="/configure">
                      Get Started
                      <span className="transition-transform group-hover:translate-x-1">
                        →
                      </span>
                    </Link>
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
