"use client";

import React from "react";
import { Zap, Sparkles, Rocket } from "lucide-react";

export const HeroSection: React.FC = () => {
  return (
    <section className="flex h-full items-center justify-center">
      <div className="space-y-5 text-foreground dark:text-white text-center">
        <div className="inline-flex items-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.35em] text-primary dark:border-primary/40 dark:bg-primary/10 dark:text-primary">
          <Sparkles size={14} />
          Generate projects for Apache Maven in seconds
        </div>
        <h1 className="text-balance text-3xl font-semibold leading-tight text-foreground dark:text-white sm:text-4xl md:text-5xl">
          Build Better Java Projects
        </h1>
        <p className="max-w-md mx-auto text-pretty text-base text-muted-foreground dark:text-white/75">
          Create production-ready projects for Apache Maven with the perfect
          dependencies, configuration, and structure. No more manual setup.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3 text-xs uppercase tracking-[0.3em] text-muted-foreground dark:text-white/60 pt-2">
          <span className="inline-flex items-center gap-2 rounded-full bg-primary/10 px-3 py-1.5 dark:bg-white/10">
            <Zap size={14} />
            Lightning fast
          </span>
          <span className="inline-flex items-center gap-2 rounded-full bg-primary/10 px-3 py-1.5 dark:bg-white/10">
            <Rocket size={14} />
            Enterprise-ready
          </span>
          <span className="inline-flex items-center gap-2 rounded-full bg-primary/10 px-3 py-1.5 dark:bg-white/10">
            <Sparkles size={14} />
            Best practices
          </span>
        </div>
      </div>
    </section>
  );
};
