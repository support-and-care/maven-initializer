"use client";

import React from "react";
import { CheckCircle, AlertCircle } from "lucide-react";

interface StatusMessageProps {
  message: string;
  type?: "success" | "error";
}

export const StatusMessage: React.FC<StatusMessageProps> = ({
  message,
  type = "success",
}) => {
  const isSuccess = type === "success" || message.includes("successfully");

  return (
    <div
      className={`p-4 rounded-lg border flex items-center gap-3 ${
        isSuccess
          ? "bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800 text-green-800 dark:text-green-200"
          : "bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800 text-red-800 dark:text-red-200"
      }`}
    >
      {isSuccess ? (
        <CheckCircle className="w-5 h-5 text-green-600 dark:text-green-400" />
      ) : (
        <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400" />
      )}
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
};
