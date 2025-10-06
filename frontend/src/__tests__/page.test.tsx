import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import Home from "@/app/page";

// Mock fetch
global.fetch = jest.fn();

describe("Home Page", () => {
  beforeEach(() => {
    (fetch as jest.Mock).mockClear();
  });

  it("renders the main heading", () => {
    render(<Home />);
    expect(screen.getByText("Maven Initializer")).toBeInTheDocument();
  });

  it("renders the call backend button", () => {
    render(<Home />);
    expect(screen.getByText("Call Backend API")).toBeInTheDocument();
  });

  it("calls backend API when button is clicked", async () => {
    const mockResponse = { message: "Hello from Maven Initializer Backend!" };
    (fetch as jest.Mock).mockResolvedValueOnce({
      json: async () => mockResponse,
    });

    render(<Home />);

    const button = screen.getByText("Call Backend API");
    fireEvent.click(button);

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith("http://localhost:8080/api/hello");
      expect(
        screen.getByText("Hello from Maven Initializer Backend!"),
      ).toBeInTheDocument();
    });
  });

  it("shows error message when API call fails", async () => {
    (fetch as jest.Mock).mockRejectedValueOnce(new Error("Network error"));

    render(<Home />);

    const button = screen.getByText("Call Backend API");
    fireEvent.click(button);

    await waitFor(() => {
      expect(
        screen.getByText("Error: Could not connect to backend"),
      ).toBeInTheDocument();
    });
  });
});
