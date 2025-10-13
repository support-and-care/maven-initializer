import React from "react";
import { Select } from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { ValidationErrors } from "@/types/project";
import { cn } from "@/lib/utils";

interface SelectOption {
  value: string;
  label: string;
}

interface ValidatedSelectProps {
  id: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: SelectOption[];
  errors?: ValidationErrors;
  required?: boolean;
}

export const ValidatedSelect: React.FC<ValidatedSelectProps> = ({
  id,
  label,
  value,
  onChange,
  options,
  errors,
  required = false,
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
        <Select
          id={id}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className={cn(
            "bg-white/10 border-white/20 text-white rounded-xl px-4 py-3 text-base transition-all duration-300 focus:bg-white/15 focus:border-purple-400/50 focus:ring-2 focus:ring-purple-400/20 hover:bg-white/12 appearance-none cursor-pointer",
            hasError &&
              "border-red-400/50 focus:border-red-400/50 focus:ring-red-400/20 bg-red-500/10",
          )}
        >
          <option value="" className="bg-gray-800 text-gray-300">
            Select {label}
          </option>
          {options.map((option) => (
            <option
              key={option.value}
              value={option.value}
              className="bg-gray-800 text-white"
            >
              {option.label}
            </option>
          ))}
        </Select>
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-purple-500/0 to-pink-500/0 group-focus:from-purple-500/10 group-focus:to-pink-500/10 transition-all duration-300 pointer-events-none"></div>
        <div className="absolute right-3 top-1/2 transform -translate-y-1/2 pointer-events-none">
          <svg
            className="w-5 h-5 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 9l-7 7-7-7"
            />
          </svg>
        </div>
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
