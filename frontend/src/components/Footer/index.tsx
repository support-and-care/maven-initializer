"use client";

import React from "react";

export const Footer: React.FC = () => {
  return (
    <footer className="w-full border-t border-border/60 bg-background/90 py-6 backdrop-blur-xl">
      <div className="mx-auto max-w-7xl px-4 lg:px-8">
        <p className="text-center text-xs text-muted-foreground">
          Apache, Apache Maven™, and the Apache Maven™ logo are either
          registered trademarks or trademarks of the Apache Software Foundation.
        </p>
      </div>
    </footer>
  );
};
