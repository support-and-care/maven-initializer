"use client";

import React, { useState } from "react";
import { ProjectConfig, ValidationErrors } from "@/types/project";
import { Header } from "@/components/Header";
import { HeroSection } from "@/components/HeroSection";
import { ProjectMetadataForm } from "@/components/ProjectMetadataForm";

export const MavenInitializerPage: React.FC = () => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationMessage, setGenerationMessage] = useState<string>("");
  const [serverErrors, setServerErrors] = useState<ValidationErrors>({});
  const [fallbackMessage, setFallbackMessage] = useState<string>("");

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsGenerating(true);
    setGenerationMessage("");
    setServerErrors({});
    setFallbackMessage("");

    try {
      const formData = new FormData(event.currentTarget);
      const includeMavenWrapper =
        formData.get("includeMavenWrapper") === "on" ||
        formData.get("includeMavenWrapper") === "true";
      const projectConfig: ProjectConfig = {
        groupId: formData.get("groupId") as string,
        artifactId: formData.get("artifactId") as string,
        version: (formData.get("version") as string) || "1.0.0-SNAPSHOT",
        name: (formData.get("name") as string) || "",
        description: (formData.get("description") as string) || "",
        javaVersion: (formData.get("javaVersion") as string) || "25",
        includeMavenWrapper: includeMavenWrapper,
      };

      const response = await fetch("/api/projects/generate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(projectConfig),
      });

      if (response.ok) {
        const blob = await response.blob();

        // Check for fallback version header
        const fallbackUsed =
          response.headers.get("X-Fallback-Version-Used") === "true";

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${projectConfig.artifactId}.zip`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        setGenerationMessage(
          "Project generated successfully! Download started.",
        );

        if (fallbackUsed) {
          setFallbackMessage(
            'Some dependencies could not be resolved automatically. The generated pom.xml contains placeholder version "TODO". Please update these versions manually.',
          );
        }
      } else {
        const errorData = await response.json();
        if (errorData.errors) {
          setServerErrors(errorData.errors);
        }
        setGenerationMessage(errorData.message || "Failed to generate project");
      }
    } catch (error) {
      console.error("Error generating project:", error);
      setGenerationMessage(
        "Error: Could not connect to backend. Make sure the backend server is running.",
      );
    } finally {
      setIsGenerating(false);
    }
  };

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
            <div className="grid h-full gap-4 lg:grid-cols-[1fr_1.2fr] lg:items-center">
              <HeroSection />
              <ProjectMetadataForm
                onSubmit={handleSubmit}
                isGenerating={isGenerating}
                generationMessage={generationMessage}
                serverErrors={serverErrors}
                fallbackMessage={fallbackMessage}
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};
