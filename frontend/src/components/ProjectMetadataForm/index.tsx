"use client";

import React from "react";
import { ValidationErrors } from "@/types/project";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/FormField";
import { StatusMessage } from "@/components/StatusMessage";

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
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      {/* Main Form Card */}
      <Card className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl border border-slate-200 dark:border-slate-700 overflow-hidden">
        {/* Card Header */}
        <CardHeader className="bg-slate-50 dark:bg-slate-700 border-b border-slate-200 dark:border-slate-600 px-8 py-6">
          <CardTitle className="text-2xl font-semibold text-slate-900 dark:text-white mb-2">
            Project Metadata
          </CardTitle>
          <CardDescription className="text-slate-600 dark:text-slate-300">
            Configure your Maven project settings
          </CardDescription>
        </CardHeader>

        {/* Form Content */}
        <CardContent className="p-8">
          <form onSubmit={onSubmit} className="space-y-8">
            {/* Form Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Group ID */}
              <FormField
                label="Group ID"
                required
                error={serverErrors.groupId}
                helperText="Usually a domain name in reverse order (e.g., com.example)"
              >
                <Input
                  type="text"
                  name="groupId"
                  placeholder="com.example"
                  required
                  className="w-full px-4 py-3 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-500 dark:placeholder:text-slate-400 focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-slate-900 transition-colors"
                />
              </FormField>

              {/* Artifact ID */}
              <FormField
                label="Artifact ID"
                required
                error={serverErrors.artifactId}
                helperText="The name of your project (lowercase, hyphens allowed)"
              >
                <Input
                  type="text"
                  name="artifactId"
                  placeholder="my-awesome-project"
                  required
                  pattern="^[a-z][a-z0-9-]*[a-z0-9]$"
                  className="w-full px-4 py-3 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-500 dark:placeholder:text-slate-400 focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-slate-900 transition-colors"
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
                  className="w-full px-4 py-3 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-500 dark:placeholder:text-slate-400 focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-slate-900 transition-colors"
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
                  className="w-full px-4 py-3 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-500 dark:placeholder:text-slate-400 focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-slate-900 transition-colors"
                />
              </FormField>

              {/* Java Version */}
              <FormField
                label="Java Version"
                helperText="The Java version to use for your project"
              >
                <select
                  name="javaVersion"
                  defaultValue="25"
                  className="w-full h-12 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-white dark:focus:ring-offset-slate-900 transition-colors"
                >
                  <option value="8">Java 8 (LTS)</option>
                  <option value="11">Java 11 (LTS)</option>
                  <option value="17">Java 17 (LTS)</option>
                  <option value="21">Java 21 (LTS)</option>
                  <option value="25">Java 25 (Latest)</option>
                </select>
              </FormField>
            </div>

            {/* Description */}
            <FormField
              label="Project Description"
              helperText="A brief description of what your project does"
            >
              <Textarea
                name="description"
                placeholder="Describe your project's purpose and functionality..."
                rows={3}
                className="w-full px-4 py-3 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-500 dark:placeholder:text-slate-400 focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-slate-900 transition-colors"
              />
            </FormField>

            {/* Generate Button */}
            <div className="flex justify-center pt-6">
              <Button
                type="submit"
                disabled={isGenerating}
                className="px-8 py-3 bg-blue-600 hover:bg-blue-700 dark:bg-blue-600 dark:hover:bg-blue-700 disabled:bg-slate-400 dark:disabled:bg-slate-600 text-white font-semibold rounded-lg transition-colors flex items-center gap-2 min-w-[200px] justify-center"
              >
                {isGenerating ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Generating...
                  </>
                ) : (
                  "Generate Project"
                )}
              </Button>
            </div>

            {/* Status Message */}
            {generationMessage && (
              <StatusMessage
                message={generationMessage}
                type={
                  generationMessage.includes("successfully")
                    ? "success"
                    : "error"
                }
              />
            )}
            {fallbackMessage && (
              <StatusMessage message={fallbackMessage} type="warning" />
            )}
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
