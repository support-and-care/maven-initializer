import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { MavenInitializerPage } from "@/components/MavenInitializerPage";

// Helpers
const fillBasicForm = () => {
  fireEvent.change(screen.getByPlaceholderText("com.example"), {
    target: { value: "com.example" },
  });
  fireEvent.change(screen.getByPlaceholderText("my-awesome-project"), {
    target: { value: "demo-app" },
  });
  fireEvent.change(screen.getByPlaceholderText("My Awesome Project"), {
    target: { value: "Demo App" },
  });
  fireEvent.change(screen.getByPlaceholderText("1.0.0-SNAPSHOT"), {
    target: { value: "1.0.0-SNAPSHOT" },
  });
  fireEvent.change(
    screen.getByPlaceholderText(
      "Describe your project's purpose and functionality...",
    ),
    { target: { value: "A demo project" } },
  );
};

describe("MavenInitializerPage", () => {
  const originalFetch = global.fetch;
  const originalCreateObjectURL = URL.createObjectURL;
  const originalRevokeObjectURL = URL.revokeObjectURL;
  const originalCreateElement = document.createElement;

  beforeEach(() => {
    // Reset DOM APIs
    URL.createObjectURL = jest.fn(() => "blob:fake");
    URL.revokeObjectURL = jest.fn();
    // Mock anchor click for download
    document.createElement = jest.fn().mockImplementation((tag: string) => {
      const el = originalCreateElement.call(document, tag);
      if (tag === "a") {
        Object.defineProperty(el, "click", { value: jest.fn() });
      }
      return el;
    });
  });

  afterEach(() => {
    jest.resetAllMocks();
    global.fetch = originalFetch as any;
    URL.createObjectURL = originalCreateObjectURL;
    URL.revokeObjectURL = originalRevokeObjectURL;
    document.createElement = originalCreateElement;
  });

  it("renders header, hero and form", () => {
    render(<MavenInitializerPage />);
    expect(screen.getByText("Maven Initializer")).toBeInTheDocument();
    expect(screen.getByText("Build Better Java Projects")).toBeInTheDocument();
    expect(screen.getByText("Project Metadata")).toBeInTheDocument();
    expect(screen.getByText("Generate Project")).toBeInTheDocument();
  });

  it("submits and handles success by starting download and showing message", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      blob: async () => new Blob(["zip-content"], { type: "application/zip" }),
    });

    render(<MavenInitializerPage />);
    fillBasicForm();

    fireEvent.submit(
      screen
        .getByRole("button", { name: /generate project/i })
        .closest("form") as HTMLFormElement,
    );

    await waitFor(() => expect(URL.createObjectURL).toHaveBeenCalled());

    expect(
      screen.getByText(/Project generated successfully!/i),
    ).toBeInTheDocument();
  });

  it("shows server validation errors and error message when response has errors", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
      json: async () => ({
        message: "Validation failed",
        errors: { groupId: "Group ID is required", artifactId: "Invalid" },
      }),
    });

    render(<MavenInitializerPage />);
    fillBasicForm();
    fireEvent.submit(
      screen
        .getByRole("button", { name: /generate project/i })
        .closest("form") as HTMLFormElement,
    );

    await screen.findByText(/Validation failed/i);
    expect(screen.getByText("Group ID is required")).toBeInTheDocument();
    expect(screen.getByText("Invalid")).toBeInTheDocument();
  });

  it("shows a network error message when fetch throws", async () => {
    global.fetch = jest.fn().mockRejectedValue(new Error("network"));
    const consoleSpy = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});

    render(<MavenInitializerPage />);
    fillBasicForm();
    fireEvent.submit(
      screen
        .getByRole("button", { name: /generate project/i })
        .closest("form") as HTMLFormElement,
    );

    await screen.findByText(/Could not connect to backend/i);
    consoleSpy.mockRestore();
  });
});
