"use client";

import React, { useState } from "react";
import { CheckCircle, AlertCircle } from "lucide-react";
import * as Form from "@radix-ui/react-form";
import { ProjectConfig, ValidationErrors } from "@/types/project";

export const ProjectConfigurationForm: React.FC = () => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationMessage, setGenerationMessage] = useState<string>("");
  const [serverErrors, setServerErrors] = useState<ValidationErrors>({});

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsGenerating(true);
    setGenerationMessage("");
    setServerErrors({});

    try {
      const formData = new FormData(event.currentTarget);
      const projectConfig: ProjectConfig = {
        groupId: formData.get("groupId") as string,
        artifactId: formData.get("artifactId") as string,
        version: (formData.get("version") as string) || "1.0.0-SNAPSHOT",
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

  const clearServerErrors = () => {
    setServerErrors({});
  };

  return (
    <div className="min-h-screen bg-white dark:bg-slate-900">
      {/* Sticky Header */}
      <header className="sticky top-0 z-50 bg-slate-900/95 backdrop-blur-md border-b border-slate-700/50 shadow-lg">
        <nav className="flex items-center justify-between px-6 py-4 max-w-7xl mx-auto">
          {/* Brand */}
          <div>
            <h1 className="text-xl font-bold text-white">Maven Initializer</h1>
            <p className="text-sm text-slate-400">
              Bootstrap your Maven project with ease
            </p>
          </div>

          {/* Navigation */}
          <div className="flex items-center space-x-6">
            <button className="text-white hover:text-slate-300 transition-colors">
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
                  d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
                />
              </svg>
            </button>
            <a
              href="#"
              className="text-white hover:text-slate-300 transition-colors flex items-center space-x-2"
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
              href="#"
              className="text-white hover:text-slate-300 transition-colors"
            >
              GitHub
            </a>
          </div>
        </nav>
      </header>

      {/* Hero Section */}
      <div className="bg-slate-800 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-4xl mx-auto">
            <div className="inline-flex items-center gap-2 bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-200 px-4 py-2 rounded-full text-sm font-medium mb-6">
              <svg
                className="h-4 w-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
              Generate Maven projects in seconds
            </div>
            <h1 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-6">
              Build Better Java Projects
            </h1>
            <p className="text-lg sm:text-xl text-slate-300 mb-8 max-w-2xl mx-auto">
              Create production-ready Maven projects with the perfect
              dependencies, configuration, and structure. No more manual setup.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              <div className="flex items-center gap-2 text-sm text-slate-300">
                <svg
                  className="h-4 w-4 text-slate-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
                  />
                </svg>
                <span>Enterprise-ready</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-slate-300">
                <svg
                  className="h-4 w-4 text-slate-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M13 10V3L4 14h7v7l9-11h-7z"
                  />
                </svg>
                <span>Lightning fast</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-slate-300">
                <svg
                  className="h-4 w-4 text-slate-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"
                  />
                </svg>
                <span>Best practices</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Form Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        {/* Main Form Card */}
        <div className="bg-slate-800 rounded-2xl shadow-2xl border border-slate-700 overflow-hidden">
          {/* Card Header */}
          <div className="bg-slate-700 border-b border-slate-600 px-8 py-6">
            <h2 className="text-2xl font-semibold text-white mb-2">
              Project Metadata
            </h2>
            <p className="text-slate-300">
              Configure your Maven project settings
            </p>
          </div>

          {/* Form Content */}
          <div className="p-8">
            <Form.Root
              onSubmit={handleSubmit}
              onClearServerErrors={clearServerErrors}
              className="space-y-8"
            >
              {/* Form Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Group ID */}
                <Form.Field
                  name="groupId"
                  serverInvalid={!!serverErrors.groupId}
                  className="space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <Form.Label className="text-sm font-medium text-slate-200 flex items-center gap-1">
                      Group ID
                      <span className="text-red-500">*</span>
                    </Form.Label>
                    <Form.Message
                      className="text-red-500 text-xs flex items-center gap-1"
                      match="valueMissing"
                    >
                      <AlertCircle className="w-3 h-3" />
                      Required
                    </Form.Message>
                    {serverErrors.groupId && (
                      <Form.Message
                        className="text-red-500 text-xs flex items-center gap-1"
                        forceMatch
                      >
                        <AlertCircle className="w-3 h-3" />
                        {serverErrors.groupId}
                      </Form.Message>
                    )}
                  </div>
                  <Form.Control asChild>
                    <input
                      type="text"
                      name="groupId"
                      placeholder="com.example"
                      required
                      className="w-full px-4 py-3 border border-slate-600 rounded-lg bg-slate-700 text-white placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    />
                  </Form.Control>
                  <p className="text-xs text-slate-400">
                    Usually a domain name in reverse order (e.g., com.example)
                  </p>
                </Form.Field>

                {/* Artifact ID */}
                <Form.Field
                  name="artifactId"
                  serverInvalid={!!serverErrors.artifactId}
                  className="space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <Form.Label className="text-sm font-medium text-slate-200 flex items-center gap-1">
                      Artifact ID
                      <span className="text-red-500">*</span>
                    </Form.Label>
                    <Form.Message
                      className="text-red-500 text-xs flex items-center gap-1"
                      match="valueMissing"
                    >
                      <AlertCircle className="w-3 h-3" />
                      Required
                    </Form.Message>
                    <Form.Message
                      className="text-red-500 text-xs flex items-center gap-1"
                      match="patternMismatch"
                    >
                      <AlertCircle className="w-3 h-3" />
                      Invalid format
                    </Form.Message>
                    {serverErrors.artifactId && (
                      <Form.Message
                        className="text-red-500 text-xs flex items-center gap-1"
                        forceMatch
                      >
                        <AlertCircle className="w-3 h-3" />
                        {serverErrors.artifactId}
                      </Form.Message>
                    )}
                  </div>
                  <Form.Control asChild>
                    <input
                      type="text"
                      name="artifactId"
                      placeholder="my-awesome-project"
                      required
                      pattern="^[a-z][a-z0-9-]*[a-z0-9]$"
                      className="w-full px-4 py-3 border border-slate-600 rounded-lg bg-slate-700 text-white placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    />
                  </Form.Control>
                  <p className="text-xs text-slate-400">
                    The name of your project (lowercase, hyphens allowed)
                  </p>
                </Form.Field>

                {/* Version */}
                <Form.Field name="version" className="space-y-2">
                  <Form.Label className="text-sm font-medium text-slate-200">
                    Version
                  </Form.Label>
                  <Form.Control asChild>
                    <input
                      type="text"
                      name="version"
                      placeholder="1.0.0-SNAPSHOT"
                      defaultValue="1.0.0-SNAPSHOT"
                      className="w-full px-4 py-3 border border-slate-600 rounded-lg bg-slate-700 text-white placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    />
                  </Form.Control>
                  <p className="text-xs text-slate-400">
                    The version of your project
                  </p>
                </Form.Field>

                {/* Java Version */}
                <Form.Field name="javaVersion" className="space-y-2">
                  <Form.Label className="text-sm font-medium text-slate-200">
                    Java Version
                  </Form.Label>
                  <Form.Control asChild>
                    <select
                      name="javaVersion"
                      defaultValue="25"
                      className="w-full px-4 py-3 border border-slate-600 rounded-lg bg-slate-700 text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors cursor-pointer"
                    >
                      <option value="8">Java 8 (LTS)</option>
                      <option value="11">Java 11 (LTS)</option>
                      <option value="17">Java 17 (LTS)</option>
                      <option value="21">Java 21 (LTS)</option>
                      <option value="25">Java 25 (Latest)</option>
                    </select>
                  </Form.Control>
                  <p className="text-xs text-slate-400">
                    The Java version to use for your project
                  </p>
                </Form.Field>
              </div>

              {/* Description */}
              <Form.Field name="description" className="space-y-2">
                <Form.Label className="text-sm font-medium text-slate-700 dark:text-slate-200">
                  Project Description
                </Form.Label>
                <Form.Control asChild>
                  <textarea
                    name="description"
                    placeholder="Describe your project's purpose and functionality..."
                    rows={3}
                    className="w-full px-4 py-3 border border-slate-600 rounded-lg bg-slate-700 text-white placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors resize-none"
                  />
                </Form.Control>
                <p className="text-xs text-slate-400">
                  A brief description of what your project does
                </p>
              </Form.Field>

              {/* Generate Button */}
              <div className="flex justify-center pt-6">
                <Form.Submit asChild>
                  <button
                    type="submit"
                    disabled={isGenerating}
                    className="px-8 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-400 text-white font-semibold rounded-lg transition-colors flex items-center gap-2 min-w-[200px] justify-center"
                  >
                    {isGenerating ? (
                      <>
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        Generating...
                      </>
                    ) : (
                      "Generate Project"
                    )}
                  </button>
                </Form.Submit>
              </div>

              {/* Status Message */}
              {generationMessage && (
                <div
                  className={`p-4 rounded-lg border flex items-center gap-3 ${
                    generationMessage.includes("successfully")
                      ? "bg-green-900/20 border-green-800 text-green-200"
                      : "bg-red-900/20 border-red-800 text-red-200"
                  }`}
                >
                  {generationMessage.includes("successfully") ? (
                    <CheckCircle className="w-5 h-5 text-green-400" />
                  ) : (
                    <AlertCircle className="w-5 h-5 text-red-400" />
                  )}
                  <span className="text-sm font-medium">
                    {generationMessage}
                  </span>
                </div>
              )}
            </Form.Root>
          </div>
        </div>
      </div>
    </div>
  );
};
