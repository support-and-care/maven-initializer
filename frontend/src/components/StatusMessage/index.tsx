"use client";

import React from "react";
import { CheckCircle, AlertCircle, AlertTriangle } from "lucide-react";
import { cn } from "@/lib/utils";

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
        "bg-primary/10 border-primary/30 text-primary dark:bg-primary/20 dark:border-primary/40 dark:text-primary",
      Icon: CheckCircle,
      iconClass: "text-primary",
    },
    warning: {
      container:
        "bg-amber-50 dark:bg-amber-900/20 border-amber-200 dark:border-amber-800 text-amber-800 dark:text-amber-200",
      Icon: AlertTriangle,
      iconClass: "text-amber-600 dark:text-amber-400",
    },
    error: {
      container:
        "bg-destructive/10 border-destructive/30 text-destructive dark:bg-destructive/20 dark:border-destructive/40 dark:text-destructive",
      Icon: AlertCircle,
      iconClass: "text-destructive",
    },
  } as const;

  const { container, Icon, iconClass } =
    statusStyles[resolvedType] ?? statusStyles.error;

  return (
    <div
      className={cn(
        "p-3 rounded-xl border flex items-center gap-2 transition-smooth",
        container
      )}
    >
      <Icon className={cn("w-4 h-4 flex-shrink-0", iconClass)} />
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
};
