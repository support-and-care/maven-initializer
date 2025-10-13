import React from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ValidationErrors } from "@/types/project";
import { cn } from "@/lib/utils";

interface ValidatedInputProps {
  id: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  errors?: ValidationErrors;
  required?: boolean;
  type?: string;
}

export const ValidatedInput: React.FC<ValidatedInputProps> = ({
  id,
  label,
  value,
  onChange,
  placeholder,
  errors,
  required = false,
  type = "text",
}) => {
  const hasError = errors && errors[id];

  return (
    <div className="space-y-3">
      <Label
        htmlFor={id}
        className="text-sm font-semibold text-white flex items-center gap-1"
      >
        {label}
        {required && <span className="text-red-400 text-lg">*</span>}
      </Label>
      <div className="relative group">
        <Input
          id={id}
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className={cn(
            "bg-white/10 border-white/20 text-white placeholder:text-gray-400 rounded-xl px-4 py-3 text-base transition-all duration-300 focus:bg-white/15 focus:border-purple-400/50 focus:ring-2 focus:ring-purple-400/20 hover:bg-white/12",
            hasError &&
              "border-red-400/50 focus:border-red-400/50 focus:ring-red-400/20 bg-red-500/10",
          )}
        />
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-purple-500/0 to-pink-500/0 group-focus:from-purple-500/10 group-focus:to-pink-500/10 transition-all duration-300 pointer-events-none"></div>
      </div>
      {hasError && (
        <div className="flex items-center gap-2 text-red-300 text-sm">
          <div className="w-1 h-1 bg-red-400 rounded-full"></div>
          <span>{errors[id]}</span>
        </div>
      )}
    </div>
  );
};
