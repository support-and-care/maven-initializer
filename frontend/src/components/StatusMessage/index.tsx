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
      container: "bg-green-900/20 border-green-800 text-green-200",
      Icon: CheckCircle,
      iconClass: "text-green-400",
    },
    warning: {
      container: "bg-amber-900/20 border-amber-800 text-amber-200",
      Icon: AlertTriangle,
      iconClass: "text-amber-400",
    },
    error: {
      container: "bg-red-900/20 border-red-800 text-red-200",
      Icon: AlertCircle,
      iconClass: "text-red-400",
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
