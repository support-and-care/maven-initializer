import { render, screen } from "@testing-library/react";
import { HeroSection } from "@/components/HeroSection";
import "@testing-library/jest-dom";

describe("HeroSection component", () => {
  it("renders the tagline text", () => {
    render(<HeroSection />);
    expect(
      screen.getByText("Generate Apache Maven™ projects in seconds"),
    ).toBeInTheDocument();
  });

  it("renders the main heading correctly", () => {
    render(<HeroSection />);
    const heading = screen.getByRole("heading", {
      name: "Build Better Java Projects",
    });
    expect(heading).toBeInTheDocument();
  });

  it("renders the description paragraph", () => {
    render(<HeroSection />);
    expect(
      screen.getByText(
        /Create production-ready Apache Maven™ projects with the perfect dependencies/i,
      ),
    ).toBeInTheDocument();
  });

  it("renders all feature items", () => {
    render(<HeroSection />);
    const features = ["Enterprise-ready", "Lightning fast", "Best practices"];

    for (const feature of features) {
      expect(screen.getByText(feature)).toBeInTheDocument();
    }
  });

  it("renders the hero container structure", () => {
    render(<HeroSection />);
    const heroContainer = screen
      .getByText("Build Better Java Projects")
      .closest("div");
    expect(heroContainer).toBeInTheDocument();
  });

  it("renders at least three SVG icons", () => {
    render(<HeroSection />);
    const svgs = document.querySelectorAll("svg");
    expect(svgs.length).toBeGreaterThanOrEqual(3);
  });
});
