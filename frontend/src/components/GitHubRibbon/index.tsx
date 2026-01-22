"use client";

import React from "react";

export const GitHubRibbon: React.FC = () => {
  return (
    <div className="github-fork-ribbon">
      <a
        href="https://github.com/support-and-care/maven-initializer"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Fork me on GitHub"
      >
        <span className="github-fork-ribbon__text">Fork me on GitHub</span>
      </a>
    </div>
  );
};

