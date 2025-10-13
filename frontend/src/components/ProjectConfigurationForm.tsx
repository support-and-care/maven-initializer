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
    <div
      style={{
        height: "100vh",
        background:
          "linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%)",
        position: "relative",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Background Elements */}
      <div
        style={{
          position: "absolute",
          inset: 0,
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%236b7280' fill-opacity='0.1'%3E%3Ccircle cx='30' cy='30' r='2'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
          opacity: 0.2,
        }}
      ></div>

      {/* Floating Elements */}
      <div
        style={{
          position: "absolute",
          top: "80px",
          left: "40px",
          width: "288px",
          height: "288px",
          backgroundColor: "#374151",
          borderRadius: "50%",
          mixBlendMode: "multiply",
          filter: "blur(40px)",
          opacity: 0.2,
          animation: "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          top: "160px",
          right: "40px",
          width: "288px",
          height: "288px",
          backgroundColor: "#4b5563",
          borderRadius: "50%",
          mixBlendMode: "multiply",
          filter: "blur(40px)",
          opacity: 0.2,
          animation: "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite",
          animationDelay: "2s",
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          bottom: "-32px",
          left: "80px",
          width: "288px",
          height: "288px",
          backgroundColor: "#6b7280",
          borderRadius: "50%",
          mixBlendMode: "multiply",
          filter: "blur(40px)",
          opacity: 0.2,
          animation: "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite",
          animationDelay: "4s",
        }}
      ></div>

      <div
        style={{
          position: "relative",
          zIndex: 10,
          maxWidth: "1152px",
          margin: "0 auto",
          padding: "16px 24px",
          flex: 1,
          display: "flex",
          flexDirection: "column",
        }}
      >
        {/* Header Section */}
        <div
          style={{
            textAlign: "center",
            marginBottom: "24px",
            flex: "0 0 auto",
          }}
        >
          <div
            style={{
              marginBottom: "8px",
            }}
          >
            <h1
              style={{
                fontSize: "36px",
                fontWeight: "bold",
                background:
                  "linear-gradient(135deg, #ffffff, #e5e7eb, #d1d5db)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
                backgroundClip: "text",
              }}
            >
              Maven Project Generator
            </h1>
          </div>
          <p
            style={{
              fontSize: "14px",
              color: "#d1d5db",
              maxWidth: "512px",
              margin: "0 auto",
              lineHeight: "1.4",
            }}
          >
            Create professional Maven projects in seconds with our configuration
            tool
          </p>
        </div>

        {/* Main Form Card */}
        <div
          style={{
            border: "none",
            boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
            background: "rgba(255, 255, 255, 0.1)",
            backdropFilter: "blur(16px)",
            borderRadius: "24px",
            overflow: "hidden",
            flex: 1,
            display: "flex",
            flexDirection: "column",
          }}
        >
          <div
            style={{
              background:
                "linear-gradient(135deg, rgba(55, 65, 81, 0.2), rgba(107, 114, 128, 0.2))",
              borderBottom: "1px solid rgba(255, 255, 255, 0.1)",
              padding: "16px",
            }}
          >
            <h3
              style={{
                fontSize: "20px",
                fontWeight: "600",
                color: "white",
              }}
            >
              Project Configuration
            </h3>
            <p
              style={{
                color: "#d1d5db",
                fontSize: "14px",
                marginTop: "4px",
              }}
            >
              Configure your Maven project settings and generate your project
              instantly
            </p>
          </div>

          <div
            style={{
              padding: "16px",
              flex: 1,
              display: "flex",
              flexDirection: "column",
            }}
          >
            <Form.Root
              onSubmit={handleSubmit}
              onClearServerErrors={clearServerErrors}
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "16px",
                flex: 1,
              }}
            >
              {/* Form Grid */}
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
                  gap: "16px",
                  flex: 1,
                }}
              >
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "12px",
                  }}
                >
                  {/* Group ID Field */}
                  <Form.Field
                    name="groupId"
                    serverInvalid={!!serverErrors.groupId}
                    style={{
                      display: "flex",
                      flexDirection: "column",
                      gap: "6px",
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                      }}
                    >
                      <Form.Label
                        style={{
                          fontSize: "14px",
                          fontWeight: "600",
                          color: "white",
                          display: "flex",
                          alignItems: "center",
                          gap: "4px",
                        }}
                      >
                        Group ID
                        <span style={{ color: "#f87171", fontSize: "18px" }}>
                          *
                        </span>
                      </Form.Label>
                      <Form.Message
                        style={{
                          color: "#fca5a5",
                          fontSize: "14px",
                          display: "flex",
                          alignItems: "center",
                          gap: "4px",
                        }}
                        match="valueMissing"
                      >
                        <AlertCircle size={16} />
                        Please enter a group ID
                      </Form.Message>
                      {serverErrors.groupId && (
                        <Form.Message
                          style={{
                            color: "#fca5a5",
                            fontSize: "14px",
                            display: "flex",
                            alignItems: "center",
                            gap: "4px",
                          }}
                          forceMatch
                        >
                          <AlertCircle size={16} />
                          {serverErrors.groupId}
                        </Form.Message>
                      )}
                    </div>
                    <div style={{ position: "relative" }}>
                      <Form.Control asChild>
                        <input
                          type="text"
                          placeholder="com.example"
                          required
                          style={{
                            width: "100%",
                            height: "48px",
                            padding: "8px 12px",
                            background: "rgba(255, 255, 255, 0.1)",
                            border: "1px solid rgba(255, 255, 255, 0.2)",
                            color: "white",
                            borderRadius: "8px",
                            fontSize: "14px",
                            transition: "all 0.3s ease",
                            outline: "none",
                          }}
                          onFocus={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.15)";
                            e.target.style.borderColor =
                              "rgba(107, 114, 128, 0.5)";
                            e.target.style.boxShadow =
                              "0 0 0 2px rgba(107, 114, 128, 0.2)";
                          }}
                          onBlur={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.1)";
                            e.target.style.borderColor =
                              "rgba(255, 255, 255, 0.2)";
                            e.target.style.boxShadow = "none";
                          }}
                        />
                      </Form.Control>
                    </div>
                  </Form.Field>

                  {/* Artifact ID Field */}
                  <Form.Field
                    name="artifactId"
                    serverInvalid={!!serverErrors.artifactId}
                    style={{
                      display: "flex",
                      flexDirection: "column",
                      gap: "12px",
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                      }}
                    >
                      <Form.Label
                        style={{
                          fontSize: "14px",
                          fontWeight: "600",
                          color: "white",
                          display: "flex",
                          alignItems: "center",
                          gap: "4px",
                        }}
                      >
                        Artifact ID
                        <span style={{ color: "#f87171", fontSize: "18px" }}>
                          *
                        </span>
                      </Form.Label>
                      <Form.Message
                        style={{
                          color: "#fca5a5",
                          fontSize: "14px",
                          display: "flex",
                          alignItems: "center",
                          gap: "4px",
                        }}
                        match="valueMissing"
                      >
                        <AlertCircle size={16} />
                        Please enter an artifact ID
                      </Form.Message>
                      <Form.Message
                        style={{
                          color: "#fca5a5",
                          fontSize: "14px",
                          display: "flex",
                          alignItems: "center",
                          gap: "4px",
                        }}
                        match="patternMismatch"
                      >
                        <AlertCircle size={16} />
                        Artifact ID must start with lowercase letter and contain
                        only lowercase letters, numbers, and hyphens
                      </Form.Message>
                      {serverErrors.artifactId && (
                        <Form.Message
                          style={{
                            color: "#fca5a5",
                            fontSize: "14px",
                            display: "flex",
                            alignItems: "center",
                            gap: "4px",
                          }}
                          forceMatch
                        >
                          <AlertCircle size={16} />
                          {serverErrors.artifactId}
                        </Form.Message>
                      )}
                    </div>
                    <div style={{ position: "relative" }}>
                      <Form.Control asChild>
                        <input
                          type="text"
                          placeholder="my-awesome-project"
                          required
                          pattern="^[a-z][a-z0-9-]*[a-z0-9]$"
                          style={{
                            width: "100%",
                            height: "48px",
                            padding: "8px 12px",
                            background: "rgba(255, 255, 255, 0.1)",
                            border: "1px solid rgba(255, 255, 255, 0.2)",
                            color: "white",
                            borderRadius: "8px",
                            fontSize: "14px",
                            transition: "all 0.3s ease",
                            outline: "none",
                          }}
                          onFocus={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.15)";
                            e.target.style.borderColor =
                              "rgba(107, 114, 128, 0.5)";
                            e.target.style.boxShadow =
                              "0 0 0 2px rgba(107, 114, 128, 0.2)";
                          }}
                          onBlur={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.1)";
                            e.target.style.borderColor =
                              "rgba(255, 255, 255, 0.2)";
                            e.target.style.boxShadow = "none";
                          }}
                        />
                      </Form.Control>
                    </div>
                  </Form.Field>

                  {/* Version Field */}
                  <Form.Field
                    name="version"
                    style={{
                      display: "flex",
                      flexDirection: "column",
                      gap: "12px",
                    }}
                  >
                    <Form.Label
                      style={{
                        fontSize: "14px",
                        fontWeight: "600",
                        color: "white",
                      }}
                    >
                      Version
                    </Form.Label>
                    <div style={{ position: "relative" }}>
                      <Form.Control asChild>
                        <input
                          type="text"
                          placeholder="1.0.0-SNAPSHOT"
                          defaultValue="1.0.0-SNAPSHOT"
                          style={{
                            width: "100%",
                            height: "48px",
                            padding: "8px 12px",
                            background: "rgba(255, 255, 255, 0.1)",
                            border: "1px solid rgba(255, 255, 255, 0.2)",
                            color: "white",
                            borderRadius: "8px",
                            fontSize: "14px",
                            transition: "all 0.3s ease",
                            outline: "none",
                          }}
                          onFocus={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.15)";
                            e.target.style.borderColor =
                              "rgba(107, 114, 128, 0.5)";
                            e.target.style.boxShadow =
                              "0 0 0 2px rgba(107, 114, 128, 0.2)";
                          }}
                          onBlur={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.1)";
                            e.target.style.borderColor =
                              "rgba(255, 255, 255, 0.2)";
                            e.target.style.boxShadow = "none";
                          }}
                        />
                      </Form.Control>
                    </div>
                  </Form.Field>
                </div>

                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "12px",
                  }}
                >
                  {/* Java Version Field */}
                  <Form.Field
                    name="javaVersion"
                    style={{
                      display: "flex",
                      flexDirection: "column",
                      gap: "12px",
                    }}
                  >
                    <Form.Label
                      style={{
                        fontSize: "14px",
                        fontWeight: "600",
                        color: "white",
                      }}
                    >
                      Java Version
                    </Form.Label>
                    <div style={{ position: "relative" }}>
                      <Form.Control asChild>
                        <select
                          defaultValue="25"
                          style={{
                            width: "100%",
                            height: "48px",
                            padding: "8px 12px",
                            background: "rgba(255, 255, 255, 0.1)",
                            border: "1px solid rgba(255, 255, 255, 0.2)",
                            color: "white",
                            borderRadius: "8px",
                            fontSize: "14px",
                            transition: "all 0.3s ease",
                            outline: "none",
                            appearance: "none",
                            cursor: "pointer",
                          }}
                          onFocus={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.15)";
                            e.target.style.borderColor =
                              "rgba(107, 114, 128, 0.5)";
                            e.target.style.boxShadow =
                              "0 0 0 2px rgba(107, 114, 128, 0.2)";
                          }}
                          onBlur={(e) => {
                            e.target.style.background =
                              "rgba(255, 255, 255, 0.1)";
                            e.target.style.borderColor =
                              "rgba(255, 255, 255, 0.2)";
                            e.target.style.boxShadow = "none";
                          }}
                        >
                          <option
                            value="8"
                            style={{ background: "#1f2937", color: "white" }}
                          >
                            Java 8 (LTS)
                          </option>
                          <option
                            value="11"
                            style={{ background: "#1f2937", color: "white" }}
                          >
                            Java 11 (LTS)
                          </option>
                          <option
                            value="17"
                            style={{ background: "#1f2937", color: "white" }}
                          >
                            Java 17 (LTS)
                          </option>
                          <option
                            value="21"
                            style={{ background: "#1f2937", color: "white" }}
                          >
                            Java 21 (LTS)
                          </option>
                          <option
                            value="25"
                            style={{ background: "#1f2937", color: "white" }}
                          >
                            Java 25 (Latest)
                          </option>
                        </select>
                      </Form.Control>
                    </div>
                  </Form.Field>

                  {/* Quick Tips */}
                  <div
                    style={{
                      background:
                        "linear-gradient(135deg, rgba(75, 85, 99, 0.1), rgba(107, 114, 128, 0.1))",
                      borderRadius: "8px",
                      padding: "12px",
                      border: "1px solid rgba(75, 85, 99, 0.2)",
                    }}
                  >
                    <h3
                      style={{
                        fontSize: "14px",
                        fontWeight: "600",
                        color: "white",
                        marginBottom: "6px",
                      }}
                    >
                      Quick Tips
                    </h3>
                    <ul
                      style={{
                        fontSize: "12px",
                        color: "#d1d5db",
                        listStyle: "none",
                        padding: 0,
                        margin: 0,
                        display: "flex",
                        flexDirection: "column",
                        gap: "4px",
                      }}
                    >
                      <li>
                        • Use lowercase letters and hyphens for artifact ID
                      </li>
                      <li>• Group ID shows who owns the project</li>
                      <li>• Java 25+ recommended for modern applications</li>
                    </ul>
                  </div>
                </div>
              </div>

              {/* Description Field */}
              <Form.Field
                name="description"
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "12px",
                }}
              >
                <Form.Label
                  style={{
                    fontSize: "14px",
                    fontWeight: "600",
                    color: "white",
                  }}
                >
                  Project Description
                </Form.Label>
                <div style={{ position: "relative" }}>
                  <Form.Control asChild>
                    <textarea
                      placeholder="Describe your project's purpose and functionality..."
                      rows={2}
                      style={{
                        width: "100%",
                        padding: "12px 16px",
                        background: "rgba(255, 255, 255, 0.1)",
                        border: "1px solid rgba(255, 255, 255, 0.2)",
                        color: "white",
                        borderRadius: "8px",
                        fontSize: "14px",
                        transition: "all 0.3s ease",
                        outline: "none",
                        resize: "none",
                      }}
                      onFocus={(e) => {
                        e.target.style.background = "rgba(255, 255, 255, 0.15)";
                        e.target.style.borderColor = "rgba(107, 114, 128, 0.5)";
                        e.target.style.boxShadow =
                          "0 0 0 2px rgba(107, 114, 128, 0.2)";
                      }}
                      onBlur={(e) => {
                        e.target.style.background = "rgba(255, 255, 255, 0.1)";
                        e.target.style.borderColor = "rgba(255, 255, 255, 0.2)";
                        e.target.style.boxShadow = "none";
                      }}
                    />
                  </Form.Control>
                </div>
              </Form.Field>

              {/* Generate Button */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  paddingTop: "12px",
                  flex: "0 0 auto",
                }}
              >
                <Form.Submit asChild>
                  <button
                    disabled={isGenerating}
                    style={{
                      position: "relative",
                      padding: "12px 32px",
                      fontSize: "16px",
                      fontWeight: "600",
                      background: "linear-gradient(135deg, #374151, #6b7280)",
                      color: "white",
                      borderRadius: "16px",
                      boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
                      transition: "all 0.3s ease",
                      transform: "scale(1)",
                      border: "none",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      gap: "12px",
                      opacity: isGenerating ? 0.5 : 1,
                      pointerEvents: isGenerating ? "none" : "auto",
                    }}
                    onMouseEnter={(e) => {
                      if (!isGenerating) {
                        e.currentTarget.style.transform = "scale(1.05)";
                        e.currentTarget.style.boxShadow =
                          "0 25px 50px -12px rgba(55, 65, 81, 0.25)";
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (!isGenerating) {
                        e.currentTarget.style.transform = "scale(1)";
                        e.currentTarget.style.boxShadow =
                          "0 25px 50px -12px rgba(0, 0, 0, 0.25)";
                      }
                    }}
                  >
                    {isGenerating ? (
                      <>
                        <div
                          style={{
                            width: "20px",
                            height: "20px",
                            border: "2px solid white",
                            borderTop: "2px solid transparent",
                            borderRadius: "50%",
                            animation: "spin 1s linear infinite",
                          }}
                        ></div>
                        Generating Project...
                      </>
                    ) : (
                      <>Generate Project</>
                    )}
                  </button>
                </Form.Submit>
              </div>

              {/* Status Message */}
              {generationMessage && (
                <div
                  style={{
                    textAlign: "center",
                    padding: "24px",
                    borderRadius: "16px",
                    border: "2px solid",
                    transition: "all 0.3s ease",
                    ...(generationMessage.includes("successfully")
                      ? {
                          background:
                            "linear-gradient(135deg, rgba(34, 197, 94, 0.2), rgba(16, 185, 129, 0.2))",
                          borderColor: "rgba(34, 197, 94, 0.3)",
                          color: "#86efac",
                        }
                      : {
                          background:
                            "linear-gradient(135deg, rgba(239, 68, 68, 0.2), rgba(220, 38, 38, 0.2))",
                          borderColor: "rgba(239, 68, 68, 0.3)",
                          color: "#fca5a5",
                        }),
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: "8px",
                    }}
                  >
                    {generationMessage.includes("successfully") ? (
                      <CheckCircle size={24} color="#4ade80" />
                    ) : (
                      <AlertCircle size={24} color="#f87171" />
                    )}
                    <span style={{ fontWeight: "500" }}>
                      {generationMessage}
                    </span>
                  </div>
                </div>
              )}
            </Form.Root>
          </div>
        </div>
      </div>

      <style jsx>{`
        @keyframes pulse {
          0%,
          100% {
            opacity: 0.2;
          }
          50% {
            opacity: 0.3;
          }
        }
        @keyframes spin {
          from {
            transform: rotate(0deg);
          }
          to {
            transform: rotate(360deg);
          }
        }
        input::placeholder,
        textarea::placeholder {
          color: #9ca3af;
        }
      `}</style>
    </div>
  );
};
