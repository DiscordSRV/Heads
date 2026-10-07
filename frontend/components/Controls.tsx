"use client";

import type { CSSProperties, ReactNode } from "react";

export function Field({ label, hint, action, children }: { label: string; hint?: ReactNode; action?: ReactNode; children: ReactNode }) {
  return (
    <div className="field">
      <div className="field-header">
        <span className="field-label">{label}</span>
        {action}
      </div>
      {children}
      {hint && <p className="field-hint">{hint}</p>}
    </div>
  );
}

export function Segmented<T extends string>({
  value,
  options,
  onChange,
  label,
}: {
  value: T;
  options: { value: T; label: string }[];
  onChange: (value: T) => void;
  label: string;
}) {
  return (
    <div className="segmented" role="radiogroup" aria-label={label}>
      {options.map((option) => (
        <button
          key={option.value}
          type="button"
          role="radio"
          aria-checked={option.value === value}
          className={option.value === value ? "active" : undefined}
          onClick={() => onChange(option.value)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}

export function Toggle({ checked, onChange, label }: { checked: boolean; onChange: (checked: boolean) => void; label: string }) {
  return (
    <label className="toggle">
      <input type="checkbox" checked={checked} onChange={(e) => onChange(e.target.checked)} />
      <span className="toggle-track" aria-hidden />
      {label}
    </label>
  );
}

/**
 * A range slider with a number box. `isDefault` dims the value to show it isn't being sent,
 * and the reset button returns to that state.
 */
export function Slider({
  label,
  value,
  min,
  max,
  step = 1,
  unit = "",
  isDefault,
  onChange,
  onReset,
  hint,
}: {
  label: string;
  value: number;
  min: number;
  max: number;
  step?: number;
  unit?: string;
  isDefault: boolean;
  onChange: (value: number) => void;
  onReset: () => void;
  hint?: ReactNode;
}) {
  const fill = ((value - min) / (max - min)) * 100;
  const clamp = (v: number) => Math.min(Math.max(v, min), max);
  return (
    <Field
      label={label}
      hint={hint}
      action={
        <span className="slider-value">
          <input
            type="number"
            className={isDefault ? "is-default" : undefined}
            aria-label={`${label} value`}
            value={Math.round(value * 100) / 100}
            min={min}
            max={max}
            step="any"
            onChange={(e) => {
              const v = e.target.valueAsNumber;
              if (Number.isFinite(v)) onChange(clamp(v));
            }}
          />
          {unit}
          <button type="button" className="button button-small" onClick={onReset} disabled={isDefault}>
            {isDefault ? "Default" : "Reset"}
          </button>
        </span>
      }
    >
      <input
        type="range"
        className={isDefault ? "slider is-default" : "slider"}
        aria-label={label}
        min={min}
        max={max}
        step={step}
        value={value}
        style={{ "--fill": `${fill}%` } as CSSProperties}
        onChange={(e) => onChange(e.target.valueAsNumber)}
      />
    </Field>
  );
}
