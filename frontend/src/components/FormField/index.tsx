"use client";

import React from "react";
import { AlertCircle } from "lucide-react";
import { Label } from "@/components/ui/label";

interface FormFieldProps {
  label: string;
  required?: boolean;
  error?: string;
  helperText?: string;
  children: React.ReactNode;
}

export const FormField: React.FC<FormFieldProps> = ({
  label,
  required = false,
  error,
  helperText,
  children,
}) => {
  return (
    <div className="space-y-1.5">
      <div className="flex items-center justify-between">
        <Label className="text-sm font-medium text-foreground flex items-center gap-1">
          {label}
          {required && <span className="text-destructive">*</span>}
        </Label>
        {error && (
          <div className="text-destructive text-xs flex items-center gap-1">
            <AlertCircle className="w-3 h-3" />
            {error}
          </div>
        )}
      </div>
      {children}
      {helperText && !error && (
        <p className="text-xs text-muted-foreground">
          {helperText}
        </p>
      )}
    </div>
  );
};
