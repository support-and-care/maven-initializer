package com.openelements.maven.initializer.backend.domain;

public record ProjectGenerationResult(Status status, String projectPath) {
  public static ProjectGenerationResult create(boolean hasResolvedVersion, String projectPath) {
    return new ProjectGenerationResult(
        hasResolvedVersion ? Status.NO_ISSUES : Status.FALLBACK_VERSION, projectPath);
  }

  public enum Status {
    NO_ISSUES,
    FALLBACK_VERSION
  }
}
