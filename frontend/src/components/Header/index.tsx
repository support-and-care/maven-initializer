"use client";

import React from "react";
import { ThemeToggle } from "@/components/ThemeToggle";

export const Header: React.FC = () => {
  return (
    <header className="sticky top-0 z-50 bg-white/95 dark:bg-slate-900/95 backdrop-blur-md border-b border-slate-200/50 dark:border-slate-700/50 shadow-lg">
      <nav className="flex items-center justify-between px-6 py-4 max-w-7xl mx-auto">
        {/* Brand */}
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">
            Maven Initializer
          </h1>
          <p className="text-sm text-slate-600 dark:text-slate-400">
            Bootstrap your Maven project with ease
          </p>
        </div>

        {/* Navigation */}
        <div className="flex items-center space-x-6">
          <ThemeToggle />
          <a
            href="#"
            className="text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-300 transition-colors flex items-center space-x-2"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
              />
            </svg>
            <span>Documentation</span>
          </a>
          <a
            href="https://github.com/support-and-care/maven-initializer"
            className="text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-300 transition-colors"
          >
            GitHub
          </a>
        </div>
      </nav>
    </header>
  );
};
