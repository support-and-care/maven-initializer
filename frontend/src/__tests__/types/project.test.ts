import {
  ProjectConfig,
  ValidationErrors,
  ProjectGenerationResponse,
} from "@/types/project";

describe("Project Types", () => {
  describe("ProjectConfig interface", () => {
    it("should allow valid project configuration", () => {
      const config: ProjectConfig = {
        groupId: "com.example",
        artifactId: "test-project",
        version: "1.0.0",
        name: "Test Project",
        description: "A test project",
        javaVersion: "17",
      };

      expect(config.groupId).toBe("com.example");
      expect(config.artifactId).toBe("test-project");
      expect(config.version).toBe("1.0.0");
      expect(config.name).toBe("Test Project");
      expect(config.description).toBe("A test project");
      expect(config.javaVersion).toBe("17");
    });

    it("should allow minimal project configuration", () => {
      const config: ProjectConfig = {
        groupId: "com.example",
        artifactId: "test-project",
        version: "1.0.0",
        javaVersion: "17",
      };

      expect(config.groupId).toBe("com.example");
      expect(config.artifactId).toBe("test-project");
      expect(config.version).toBe("1.0.0");
      expect(config.javaVersion).toBe("17");
      expect(config.name).toBeUndefined();
      expect(config.description).toBeUndefined();
    });

    it("should handle different Java versions", () => {
      const javaVersions = ["8", "11", "17", "21", "25"];

      javaVersions.forEach((version) => {
        const config: ProjectConfig = {
          groupId: "com.example",
          artifactId: "test-project",
          version: "1.0.0",
          javaVersion: version,
        };

        expect(config.javaVersion).toBe(version);
      });
    });

    it("should handle different version formats", () => {
      const versions = ["1.0.0", "1.0.0-SNAPSHOT", "2.1.3", "0.1.0"];

      versions.forEach((version) => {
        const config: ProjectConfig = {
          groupId: "com.example",
          artifactId: "test-project",
          version: version,
          javaVersion: "17",
        };

        expect(config.version).toBe(version);
      });
    });
  });

  describe("ValidationErrors interface", () => {
    it("should allow string key-value pairs", () => {
      const errors: ValidationErrors = {
        groupId: "Group ID is required",
        artifactId: "Artifact ID must be lowercase",
        version: "Version format is invalid",
      };

      expect(errors.groupId).toBe("Group ID is required");
      expect(errors.artifactId).toBe("Artifact ID must be lowercase");
      expect(errors.version).toBe("Version format is invalid");
    });

    it("should allow empty errors object", () => {
      const errors: ValidationErrors = {};
      expect(Object.keys(errors)).toHaveLength(0);
    });

    it("should handle dynamic keys", () => {
      const errors: ValidationErrors = {
        customField: "Custom field error",
        anotherField: "Another field error",
      };

      expect(errors.customField).toBe("Custom field error");
      expect(errors.anotherField).toBe("Another field error");
    });
  });

  describe("ProjectGenerationResponse interface", () => {
    it("should allow successful response", () => {
      const response: ProjectGenerationResponse = {
        success: true,
        message: "Project generated successfully",
      };

      expect(response.success).toBe(true);
      expect(response.message).toBe("Project generated successfully");
      expect(response.errors).toBeUndefined();
    });

    it("should allow error response with validation errors", () => {
      const response: ProjectGenerationResponse = {
        success: false,
        message: "Validation failed",
        errors: {
          groupId: "Group ID is required",
          artifactId: "Artifact ID must be lowercase",
        },
      };

      expect(response.success).toBe(false);
      expect(response.message).toBe("Validation failed");
      expect(response.errors).toEqual({
        groupId: "Group ID is required",
        artifactId: "Artifact ID must be lowercase",
      });
    });

    it("should allow minimal error response", () => {
      const response: ProjectGenerationResponse = {
        success: false,
      };

      expect(response.success).toBe(false);
      expect(response.message).toBeUndefined();
      expect(response.errors).toBeUndefined();
    });

    it("should handle different error scenarios", () => {
      const scenarios = [
        {
          success: false,
          message: "Network error",
        },
        {
          success: false,
          message: "Server error",
          errors: {},
        },
        {
          success: true,
          message: "Project created",
        },
      ];

      scenarios.forEach((scenario) => {
        const response: ProjectGenerationResponse = scenario;
        expect(response.success).toBe(scenario.success);
        expect(response.message).toBe(scenario.message);
        if (scenario.errors) {
          expect(response.errors).toEqual(scenario.errors);
        }
      });
    });
  });

  describe("Type compatibility", () => {
    it("should be compatible with form data", () => {
      const formData = new FormData();
      formData.append("groupId", "com.example");
      formData.append("artifactId", "test-project");
      formData.append("version", "1.0.0");
      formData.append("javaVersion", "17");

      const config: ProjectConfig = {
        groupId: formData.get("groupId") as string,
        artifactId: formData.get("artifactId") as string,
        version: formData.get("version") as string,
        javaVersion: formData.get("javaVersion") as string,
      };

      expect(config.groupId).toBe("com.example");
      expect(config.artifactId).toBe("test-project");
    });

    it("should be compatible with API responses", () => {
      const apiResponse = {
        success: false,
        message: "Validation failed",
        errors: {
          groupId: "Group ID is required",
        },
      };

      const response: ProjectGenerationResponse = apiResponse;
      expect(response.success).toBe(false);
      expect(response.message).toBe("Validation failed");
      expect(response.errors?.groupId).toBe("Group ID is required");
    });
  });
});
