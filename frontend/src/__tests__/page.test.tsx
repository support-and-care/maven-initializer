import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Home from "@/app/page";

describe("Home Page", () => {
  it("renders the main heading", () => {
    render(<Home />);
    expect(screen.getByText("Maven Initializer")).toBeInTheDocument();
  });

  it("renders the project configuration form", () => {
    render(<Home />);
    expect(screen.getByText("Project Metadata")).toBeInTheDocument();
    expect(
      screen.getByText("Configure your Maven project settings"),
    ).toBeInTheDocument();
  });

  it("renders the hero section", () => {
    render(<Home />);
    expect(screen.getByText("Build Better Java Projects")).toBeInTheDocument();
    expect(
      screen.getByText(
        "Create production-ready Maven projects with the perfect dependencies, configuration, and structure. No more manual setup.",
      ),
    ).toBeInTheDocument();
  });

  it("renders the generate button", () => {
    render(<Home />);
    expect(screen.getByText("Generate Project")).toBeInTheDocument();
  });

  it("renders form fields", () => {
    render(<Home />);
    expect(screen.getByPlaceholderText("com.example")).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText("my-awesome-project"),
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText("1.0.0-SNAPSHOT")).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText("My Awesome Project"),
    ).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText(
        "Describe your project's purpose and functionality...",
      ),
    ).toBeInTheDocument();
  });
});
