import { render, screen } from "@testing-library/react";
import { Header } from "@/components/Header";
import "@testing-library/jest-dom";

describe("Header component", () => {
  it("renders the main title correctly", () => {
    render(<Header />);
    expect(screen.getByText("Maven Initializer")).toBeInTheDocument();
  });

  it("renders the subtitle correctly", () => {
    render(<Header />);
    expect(
      screen.getByText("Bootstrap your Maven project with ease"),
    ).toBeInTheDocument();
  });

  it("renders the Documentation link", () => {
    render(<Header />);
    const docLink = screen.getByText("Documentation");
    expect(docLink).toBeInTheDocument();
    expect(docLink.closest("a")).toHaveAttribute("href", "#");
  });

  it("renders the GitHub link", () => {
    render(<Header />);
    const githubLink = screen.getByText("GitHub");
    expect(githubLink).toBeInTheDocument();
    expect(githubLink.closest("a")).toHaveAttribute(
      "href",
      "https://github.com/support-and-care/maven-initializer",
    );
  });

  it("renders all navigation elements", () => {
    render(<Header />);
    const buttons = screen.getAllByRole("button");
    const links = screen.getAllByRole("link");
    expect(buttons.length).toBe(1); // One button (the icon button)
    expect(links.length).toBe(2); // Two links: Documentation + GitHub
  });
});
