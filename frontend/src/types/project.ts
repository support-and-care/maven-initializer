export interface ProjectConfig {
  groupId: string;
  artifactId: string;
  version: string;
  name?: string;
  description?: string;
  javaVersion: string;
  includeMavenWrapper?: boolean;
  includeSpotless?: boolean;
  includeCheckstyle?: boolean;
}

export interface ValidationErrors {
  [key: string]: string;
}

export interface ProjectGenerationResponse {
  success: boolean;
  message?: string;
  errors?: ValidationErrors;
}
