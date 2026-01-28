import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Home from "@/app/page";

describe("Home Page", () => {
  it("renders the main heading", () => {
    render(<Home />);
    expect(screen.getByText("for Apache Maven")).toBeInTheDocument();
    expect(screen.getByText("Initializer")).toBeInTheDocument();
  });

  it("renders the hero section", () => {
    render(<Home />);
    expect(screen.getByText("Build Better Java Projects")).toBeInTheDocument();
    expect(
      screen.getByText(
        "Create production-ready projects for Apache Maven with the perfect dependencies, configuration, and structure. No more manual setup.",
      ),
    ).toBeInTheDocument();
  });

  it("renders the get started button", () => {
    render(<Home />);
    expect(screen.getByText("Get Started")).toBeInTheDocument();
  });

  it("renders hero section features", () => {
    render(<Home />);
    expect(screen.getByText("Lightning fast")).toBeInTheDocument();
    expect(screen.getByText("Enterprise-ready")).toBeInTheDocument();
    expect(screen.getByText("Best practices")).toBeInTheDocument();
  });
});
