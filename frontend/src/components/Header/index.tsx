"use client";

import React, { useState, useEffect } from "react";
import Image from "next/image";
import { useTheme } from "next-themes";
import { Menu, Moon, Sun, X } from "lucide-react";
import { ThemeToggle } from "@/components/ThemeToggle";

export const Header: React.FC = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [mounted, setMounted] = useState(false);
  const { resolvedTheme, setTheme } = useTheme();

  useEffect(() => {
    setMounted(true);
  }, []);

  const isDark = resolvedTheme === "dark";

  return (
    <header className="sticky top-2 z-50 flex justify-center px-4 py-3">
      <div className="relative w-full max-w-7xl">
        <div className="absolute inset-0 rounded-3xl bg-gradient-to-r from-primary/15 via-accent/10 to-transparent blur-xl"></div>
        <nav className="relative flex items-center justify-between rounded-3xl border border-border/60 bg-background/90 px-5 py-3 shadow-[0_20px_50px_-25px_rgba(7,8,45,0.45)] backdrop-blur-xl">
          <div className="flex items-center gap-3 transition-smooth hover:opacity-90">
            <div className="relative flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br from-primary/20 via-primary/30 to-accent/30 shadow-inner overflow-hidden">
              <Image
                src="/maven.jpeg"
                alt="Apache Maven Logo"
                width={32}
                height={32}
                className="h-8 w-8 object-contain relative z-10"
                priority
                unoptimized
              />
              <div className="absolute inset-0 rounded-2xl ring-1 ring-primary/30 z-0" />
            </div>
            <div className="hidden flex-col leading-tight sm:flex">
              <span className="text-xs uppercase tracking-[0.3em] text-muted-foreground">
                Maven
              </span>
              <span className="text-sm font-semibold text-foreground">
                Initializer
              </span>
            </div>
          </div>

          <div className="hidden items-center gap-2 lg:flex">
            <a
              href="#"
              className="relative rounded-2xl px-4 py-2 text-sm font-medium text-muted-foreground transition-smooth hover:text-foreground"
            >
              Documentation
            </a>
            <a
              href="https://github.com/support-and-care/maven-initializer"
              target="_blank"
              rel="noopener noreferrer"
              className="relative rounded-2xl px-4 py-2 text-sm font-medium text-muted-foreground transition-smooth hover:text-foreground"
            >
              GitHub
            </a>
          </div>

          <div className="hidden items-center gap-3 lg:flex">
            {mounted && (
              <button
                onClick={() => setTheme(isDark ? "light" : "dark")}
                aria-label="Toggle theme"
                className="flex h-10 w-10 items-center justify-center rounded-2xl border border-border/60 bg-background/60 text-muted-foreground transition-smooth hover:border-primary/50 hover:text-foreground"
              >
                {isDark ? <Sun size={18} /> : <Moon size={18} />}
              </button>
            )}
          </div>

          <div className="flex items-center gap-2 lg:hidden">
            {mounted && (
              <button
                onClick={() => setTheme(isDark ? "light" : "dark")}
                aria-label="Toggle theme"
                className="flex h-10 w-10 items-center justify-center rounded-2xl border border-border/60 bg-background/60 text-muted-foreground transition-smooth hover:border-primary/50 hover:text-foreground"
              >
                {isDark ? <Sun size={18} /> : <Moon size={18} />}
              </button>
            )}
            <button
              onClick={() => setIsMenuOpen((prev) => !prev)}
              className="flex h-10 w-10 items-center justify-center rounded-2xl border border-border/60 bg-background/60 text-muted-foreground transition-smooth hover:border-primary/50 hover:text-foreground"
            >
              {isMenuOpen ? <X size={18} /> : <Menu size={18} />}
            </button>
          </div>
        </nav>

        {isMenuOpen && (
          <div className="mt-4 flex flex-col gap-2 rounded-3xl border border-border/60 bg-background/95 p-4 shadow-2xl backdrop-blur-xl lg:hidden">
            <a
              href="#"
              className="flex items-center justify-between rounded-2xl px-4 py-3 text-sm font-medium transition-smooth text-muted-foreground hover:bg-muted/40 hover:text-foreground"
            >
              Documentation
              <span className="text-xs uppercase tracking-[0.3em] text-primary/70">
                Explore
              </span>
            </a>
            <a
              href="https://github.com/support-and-care/maven-initializer"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center justify-between rounded-2xl px-4 py-3 text-sm font-medium transition-smooth text-muted-foreground hover:bg-muted/40 hover:text-foreground"
            >
              GitHub
              <span className="text-xs uppercase tracking-[0.3em] text-primary/70">
                Explore
              </span>
            </a>
          </div>
        )}
      </div>
    </header>
  );
};
