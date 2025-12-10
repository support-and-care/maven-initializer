"use client";

import React from "react";
import { ValidationErrors } from "@/types/project";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/FormField";
import { StatusMessage } from "@/components/StatusMessage";
import { Loader2 } from "lucide-react";

interface ProjectMetadataFormProps {
  onSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
  isGenerating: boolean;
  generationMessage: string;
  serverErrors: ValidationErrors;
  fallbackMessage?: string;
}

export const ProjectMetadataForm: React.FC<ProjectMetadataFormProps> = ({
  onSubmit,
  isGenerating,
  generationMessage,
  serverErrors,
  fallbackMessage,
}) => {
  return (
    <section className="flex h-full items-center justify-center">
      <div className="w-full rounded-3xl border border-border/60 bg-card/70 p-6 lg:p-8 shadow-[0_25px_60px_-30px_rgba(7,8,45,0.35)] backdrop-blur-xl">
        <div className="flex justify-center mb-4">
          <div className="inline-flex items-center gap-2 rounded-full border border-primary/50 bg-transparent px-3 py-1.5 text-sm font-semibold uppercase tracking-[0.35em] text-primary dark:border-primary/30 dark:bg-primary/10 dark:text-primary">
            Project Configuration
          </div>
        </div>

        <form onSubmit={onSubmit} className="space-y-6">
          {/* Two Column Layout */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6">
            {/* Left Section: Configure Your Maven Project */}
            <div className="rounded-2xl border border-border/50 bg-card/50 p-5 lg:p-6">
              <div className="flex justify-center mb-4">
                <div className="inline-flex items-center gap-2 rounded-full border border-primary/50 bg-transparent px-3 py-1 text-xs font-semibold uppercase tracking-[0.35em] text-primary dark:border-primary/30 dark:bg-primary/10 dark:text-primary">
                  Configure Your Maven Project
                </div>
              </div>
              <p className="text-sm text-muted-foreground sm:text-base mb-6 text-center">
                Fill in the details below to generate your production-ready
                Maven project structure.
              </p>

              {/* Form Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 lg:gap-4">
                {/* Group ID */}
                <FormField
                  label="Group ID"
                  required
                  error={serverErrors.groupId}
                  helperText="Usually a domain name in reverse order"
                >
                  <Input
                    type="text"
                    name="groupId"
                    placeholder="com.example"
                    required
                    className="w-full h-9 text-sm"
                    aria-invalid={serverErrors.groupId ? "true" : "false"}
                  />
                </FormField>

                {/* Artifact ID */}
                <FormField
                  label="Artifact ID"
                  required
                  error={serverErrors.artifactId}
                  helperText="The name of your project"
                >
                  <Input
                    type="text"
                    name="artifactId"
                    placeholder="my-awesome-project"
                    required
                    pattern="^[a-z][a-z0-9-]*[a-z0-9]$"
                    className="w-full h-9 text-sm"
                    aria-invalid={serverErrors.artifactId ? "true" : "false"}
                  />
                </FormField>

                {/* Version */}
                <FormField
                  label="Version"
                  helperText="The version of your project"
                >
                  <Input
                    type="text"
                    name="version"
                    placeholder="1.0.0-SNAPSHOT"
                    defaultValue="1.0.0-SNAPSHOT"
                    className="w-full h-9 text-sm"
                  />
                </FormField>

                {/* Project Name */}
                <FormField
                  label="Project Name"
                  helperText="A friendly name for your project"
                >
                  <Input
                    type="text"
                    name="name"
                    placeholder="My Awesome Project"
                    className="w-full h-9 text-sm"
                  />
                </FormField>

                {/* Java Version */}
                <FormField
                  label="Java Version"
                  helperText="The Java version to use"
                >
                  <select
                    name="javaVersion"
                    defaultValue="25"
                    className="border-input placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:bg-input/30 dark:hover:bg-input/50 flex h-9 w-full items-center justify-between gap-2 rounded-md border bg-transparent px-3 py-2 text-sm shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    <option value="8">Java 8 (LTS)</option>
                    <option value="11">Java 11 (LTS)</option>
                    <option value="17">Java 17 (LTS)</option>
                    <option value="21">Java 21 (LTS)</option>
                    <option value="25">Java 25 (Latest)</option>
                  </select>
                </FormField>

                {/* Description */}
                <FormField
                  label="Project Description"
                  helperText="A brief description of what your project does"
                  className="sm:col-span-2"
                >
                  <Textarea
                    name="description"
                    placeholder="Describe your project's purpose and functionality..."
                    rows={2}
                    className="w-full text-sm"
                  />
                </FormField>
              </div>
            </div>

            {/* Right Section: Advanced Options */}
            <div className="rounded-2xl border border-border/50 bg-card/50 p-5 lg:p-6">
              <div className="flex justify-center mb-4">
                <div className="inline-flex items-center gap-2 rounded-full border border-primary/50 bg-transparent px-3 py-1 text-xs font-semibold uppercase tracking-[0.35em] text-primary dark:border-primary/30 dark:bg-primary/10 dark:text-primary">
                  Advanced Options
                </div>
              </div>
              <p className="text-sm text-muted-foreground sm:text-base mb-6 text-center">
                Configure additional options for your Maven project.
              </p>

              <div className="space-y-4">
                {/* Maven Wrapper Checkbox */}
                <div className="flex items-start gap-3 rounded-md border border-border/60 bg-card/50 p-3 lg:p-4">
                  <input
                    type="checkbox"
                    name="includeMavenWrapper"
                    id="includeMavenWrapper"
                    defaultChecked={true}
                    className="mt-0.5 h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-input/30"
                  />
                  <div className="flex-1">
                    <label
                      htmlFor="includeMavenWrapper"
                      className="text-sm font-medium text-foreground cursor-pointer block"
                    >
                      Include Maven Wrapper
                    </label>
                    <p className="text-xs text-muted-foreground mt-1">
                      Includes mvnw and mvnw.cmd scripts for building without
                      requiring Maven to be installed.
                    </p>
                  </div>
                </div>

                {/* Code Formatting Plugins Section */}
                <div className="space-y-3">
                  <div className="text-sm font-medium text-foreground">
                    Code Formatting Plugins
                  </div>
                  <p className="text-xs text-muted-foreground mb-3">
                    Select one or both plugins to ensure code style consistency.
                  </p>

                  {/* Spotless Plugin Checkbox */}
                  <div className="flex items-start gap-3 rounded-md border border-border/60 bg-card/50 p-3 lg:p-4">
                    <input
                      type="checkbox"
                      name="includeSpotless"
                      id="includeSpotless"
                      className="mt-0.5 h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-input/30"
                    />
                    <div className="flex-1">
                      <label
                        htmlFor="includeSpotless"
                        className="text-sm font-medium text-foreground cursor-pointer block"
                      >
                        Spotless Maven Plugin
                      </label>
                      <p className="text-xs text-muted-foreground mt-1">
                        Keeps your code spotless by automatically formatting it.
                        Requires configuration.
                      </p>
                    </div>
                  </div>

                  {/* Checkstyle Plugin Checkbox */}
                  <div className="flex items-start gap-3 rounded-md border border-border/60 bg-card/50 p-3 lg:p-4">
                    <input
                      type="checkbox"
                      name="includeCheckstyle"
                      id="includeCheckstyle"
                      className="mt-0.5 h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-input/30"
                    />
                    <div className="flex-1">
                      <label
                        htmlFor="includeCheckstyle"
                        className="text-sm font-medium text-foreground cursor-pointer block"
                      >
                        Maven Checkstyle Plugin
                      </label>
                      <p className="text-xs text-muted-foreground mt-1">
                        Checks that your code adheres to a coding standard.
                        Requires configuration.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Generate Button */}
          <div className="flex justify-center pt-2">
            <Button
              type="submit"
              disabled={isGenerating}
              className="group inline-flex items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-primary via-primary/90 to-accent px-8 py-3 text-sm font-semibold text-primary-foreground shadow-lg shadow-primary/30 transition-smooth hover:shadow-xl hover:shadow-primary/40 disabled:opacity-50 disabled:cursor-not-allowed min-w-[200px]"
            >
              {isGenerating ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  Generating...
                </>
              ) : (
                <>
                  Generate Project
                  <span className="transition-transform group-hover:translate-x-1">
                    →
                  </span>
                </>
              )}
            </Button>
          </div>

          {/* Status Message */}
          {generationMessage && (
            <StatusMessage
              message={generationMessage}
              type={
                generationMessage.includes("successfully") ? "success" : "error"
              }
            />
          )}
          {fallbackMessage && (
            <StatusMessage message={fallbackMessage} type="warning" />
          )}
        </form>
      </div>
    </section>
  );
};
