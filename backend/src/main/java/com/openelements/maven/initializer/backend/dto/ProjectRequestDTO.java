package com.openelements.maven.initializer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProjectRequestDTO {

  @NotBlank(message = "GroupId is mandatory")
  private String groupId;

  @NotBlank(message = "ArtifactId is mandatory")
  @Pattern(
      regexp = "^[a-z][a-z0-9-]*[a-z0-9]$",
      message =
          "ArtifactId must start with a lowercase letter, contain only lowercase letters, numbers, and hyphens, and end with a lowercase letter or number")
  private String artifactId;

  private String version = "1.0.0-SNAPSHOT";

  private String description;

  private String javaVersion = "25";

  // Default constructor
  public ProjectRequestDTO() {}

  // Constructor with all fields
  public ProjectRequestDTO(
      String groupId, String artifactId, String version, String description, String javaVersion) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.description = description;
    this.javaVersion = javaVersion;
  }

  // Getters and Setters
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getJavaVersion() {
    return javaVersion;
  }

  public void setJavaVersion(String javaVersion) {
    this.javaVersion = javaVersion;
  }

  @Override
  public String toString() {
    return "ProjectRequestDTO{"
        + "groupId='"
        + groupId
        + '\''
        + ", artifactId='"
        + artifactId
        + '\''
        + ", version='"
        + version
        + '\''
        + ", description='"
        + description
        + '\''
        + ", javaVersion='"
        + javaVersion
        + '\''
        + '}';
  }
}
