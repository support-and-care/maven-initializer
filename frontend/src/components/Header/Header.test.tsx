import { render, screen } from "@testing-library/react";
import { Header } from "@/components/Header";
import "@testing-library/jest-dom";

describe("Header component", () => {
  it("renders the main title correctly", () => {
    render(<Header />);
    expect(screen.getByText("Apache Maven")).toBeInTheDocument();
    expect(screen.getByText("Initializer")).toBeInTheDocument();
  });
});
