"use client";

import React from "react";
import { AlertCircle } from "lucide-react";

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
    <div className="space-y-2">
      <div className="flex items-center justify-between">
        <label className="text-sm font-medium text-slate-900 dark:text-slate-200 flex items-center gap-1">
          {label}
          {required && <span className="text-red-500">*</span>}
        </label>
        {error && (
          <div className="text-red-500 dark:text-red-400 text-xs flex items-center gap-1">
            <AlertCircle className="w-3 h-3" />
            {error}
          </div>
        )}
      </div>
      {children}
      {helperText && (
        <p className="text-xs text-slate-600 dark:text-slate-400">
          {helperText}
        </p>
      )}
    </div>
  );
};
