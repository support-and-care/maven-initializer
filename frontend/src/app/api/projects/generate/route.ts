import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const backendUrl = process.env.BACKEND_URL || "http://localhost:8080";
    const body = await request.json();

    const response = await fetch(`${backendUrl}/api/projects/generate`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      // If it's a validation error, return the error details
      if (response.status === 400) {
        const errorData = await response.json();
        return NextResponse.json(errorData, { status: 400 });
      }
      throw new Error(`Backend responded with status: ${response.status}`);
    }

    // For successful responses, return the zip file
    const zipBuffer = await response.arrayBuffer();
    const filename =
      response.headers
        .get("content-disposition")
        ?.split("filename=")[1]
        ?.replace(/"/g, "") || "project.zip";

    return new NextResponse(zipBuffer, {
      status: 200,
      headers: {
        "Content-Type": "application/octet-stream",
        "Content-Disposition": `attachment; filename="${filename}"`,
      },
    });
  } catch (error) {
    console.error("Error proxying to backend:", error);
    return NextResponse.json(
      {
        success: false,
        message:
          "Could not connect to backend. Make sure the backend server is running.",
        errors: {},
      },
      { status: 500 },
    );
  }
}
