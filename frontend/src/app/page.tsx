"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";

export default function Home() {
  const [message, setMessage] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const fetchHelloMessage = async () => {
    setLoading(true);
    try {
      const response = await fetch("/api/hello");
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setMessage(data.message);
    } catch (error) {
      console.error("Error fetching data:", error);
      setMessage(
        "Error: Could not connect to backend. Make sure both frontend and backend servers are running.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="z-10 max-w-5xl w-full items-center justify-between font-mono text-sm">
        <h1 className="text-4xl font-bold text-center mb-8">
          Maven Initializer
        </h1>

        <div className="text-center space-y-4">
          <Button
            onClick={fetchHelloMessage}
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            {loading ? "Loading..." : "Call Backend API"}
          </Button>

          {message && (
            <div className="mt-4 p-4 bg-gray-100 rounded-lg">
              <p className="text-lg font-medium">Backend Response:</p>
              <p className="text-gray-700">{message}</p>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
