export interface ProjectConfig {
  groupId: string;
  artifactId: string;
  version: string;
  description?: string;
  javaVersion: string;
}

export interface ValidationErrors {
  [key: string]: string;
}

export interface ProjectGenerationResponse {
  success: boolean;
  message?: string;
  errors?: ValidationErrors;
}
