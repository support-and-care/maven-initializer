"use client";

import React from "react";
import { CheckCircle, AlertCircle, AlertTriangle } from "lucide-react";

interface StatusMessageProps {
  message: string;
  type?: "success" | "error" | "warning";
}

export const StatusMessage: React.FC<StatusMessageProps> = ({
  message,
  type,
}) => {
  const resolvedType =
    type ?? (message.includes("successfully") ? "success" : "error");

  const statusStyles = {
    success: {
      container:
        "bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800 text-green-800 dark:text-green-200",
      Icon: CheckCircle,
      iconClass: "text-green-600 dark:text-green-400",
    },
    warning: {
      container:
        "bg-amber-50 dark:bg-amber-900/20 border-amber-200 dark:border-amber-800 text-amber-800 dark:text-amber-200",
      Icon: AlertTriangle,
      iconClass: "text-amber-600 dark:text-amber-400",
    },
    error: {
      container:
        "bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800 text-red-800 dark:text-red-200",
      Icon: AlertCircle,
      iconClass: "text-red-600 dark:text-red-400",
    },
  } as const;

  const { container, Icon, iconClass } =
    statusStyles[resolvedType] ?? statusStyles.error;

  return (
    <div
      className={`p-4 rounded-lg border flex items-center gap-3 ${container}`}
    >
      <Icon className={`w-5 h-5 ${iconClass}`} />
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
};
