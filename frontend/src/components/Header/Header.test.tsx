import { render, screen } from "@testing-library/react";
import { Header } from "@/components/Header";
import "@testing-library/jest-dom";

describe("Header component", () => {
  it("renders the main title correctly", () => {
    render(<Header />);
    expect(screen.getByText("Maven")).toBeInTheDocument();
    expect(screen.getByText("Initializer")).toBeInTheDocument();
  });

  it("renders all navigation elements", () => {
    render(<Header />);
    const buttons = screen.getAllByRole("button");
    const links = screen.getAllByRole("link");
    expect(buttons.length).toBeGreaterThanOrEqual(1);
    expect(links.length).toBe(1);
  });
});
