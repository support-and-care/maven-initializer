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

  const detectFallbackUsage = async (blob: Blob): Promise<boolean> => {
    try {
      const arrayBuffer =
        typeof blob.arrayBuffer === "function"
          ? await blob.arrayBuffer()
          : await new Response(blob).arrayBuffer();
      const { default: JSZip } = await import("jszip");
      const zip = await JSZip.loadAsync(arrayBuffer);
      const pomFile = zip.file("pom.xml");

      if (!pomFile) {
        return false;
      }

      const pomContent = await pomFile.async("string");
      return pomContent.includes("TODO");
    } catch (error) {
      console.warn(
        "Could not inspect generated ZIP for fallback versions",
        error,
      );
      return false;
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsGenerating(true);
    setGenerationMessage("");
    setServerErrors({});
    setFallbackMessage("");

    try {
      const formData = new FormData(event.currentTarget);
      const projectConfig: ProjectConfig = {
        groupId: formData.get("groupId") as string,
        artifactId: formData.get("artifactId") as string,
        version: (formData.get("version") as string) || "1.0.0-SNAPSHOT",
        name: (formData.get("name") as string) || "",
        description: (formData.get("description") as string) || "",
        javaVersion: (formData.get("javaVersion") as string) || "25",
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
        const fallbackPromise = detectFallbackUsage(blob);

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

        const fallbackUsed = await fallbackPromise;
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
    <div className="min-h-screen bg-white dark:bg-slate-900">
      <Header />
      <HeroSection />
      <ProjectMetadataForm
        onSubmit={handleSubmit}
        isGenerating={isGenerating}
        generationMessage={generationMessage}
        serverErrors={serverErrors}
        fallbackMessage={fallbackMessage}
      />
    </div>
  );
};
