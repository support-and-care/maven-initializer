"use client";

import React, { useState } from "react";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { ProjectConfig, ValidationErrors } from "@/types/project";
import { Header } from "@/components/Header";
import { ProjectMetadataForm } from "@/components/ProjectMetadataForm";
import { Button } from "@/components/ui/button";

export default function ConfigurePage() {
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
      const includeSpotless =
        formData.get("includeSpotless") === "on" ||
        formData.get("includeSpotless") === "true";
      const includeCheckstyle =
        formData.get("includeCheckstyle") === "on" ||
        formData.get("includeCheckstyle") === "true";
      const projectConfig: ProjectConfig = {
        groupId: formData.get("groupId") as string,
        artifactId: formData.get("artifactId") as string,
        version: (formData.get("version") as string) || "1.0.0-SNAPSHOT",
        name: (formData.get("name") as string) || "",
        description: (formData.get("description") as string) || "",
        javaVersion: (formData.get("javaVersion") as string) || "25",
        includeMavenWrapper: includeMavenWrapper,
        includeSpotless: includeSpotless,
        includeCheckstyle: includeCheckstyle,
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
      <main
        className="flex-1 overflow-y-auto"
        style={{ scrollPaddingTop: "6rem" }}
      >
        <div className="relative min-h-full">
          {/* Spacer to prevent content from going behind sticky navbar */}
          <div className="h-20 lg:h-24 flex-shrink-0 relative">
            {/* Back Button - Positioned in spacer area */}
            <div className="absolute bottom-4 left-4 lg:left-8 z-10">
              <Button
                asChild
                className="group inline-flex items-center justify-center gap-2 rounded-2xl bg-transparent px-6 py-2.5 text-sm font-semibold text-muted-foreground shadow-none transition-smooth hover:bg-gradient-to-r hover:from-primary hover:via-primary/90 hover:to-accent hover:text-primary-foreground hover:shadow-xl hover:shadow-primary/40"
              >
                <Link href="/" className="flex items-center gap-2">
                  <ArrowLeft
                    size={16}
                    className="transition-transform group-hover:-translate-x-1"
                  />
                  Back to Home
                </Link>
              </Button>
            </div>
          </div>

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

          <div className="mx-auto max-w-[95vw] xl:max-w-[1400px] px-4 pb-4 lg:px-8">
            <div className="flex h-full items-center justify-center py-4">
              <div className="w-full max-w-7xl">
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
        </div>
      </main>
    </div>
  );
}
