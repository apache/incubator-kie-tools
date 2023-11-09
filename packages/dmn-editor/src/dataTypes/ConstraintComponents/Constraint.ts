import React from "react";

export interface ConstraintProps {
  id: string;
  placeholder?: string;
  type: "text" | "number";
  value: string;
  onChange: (newValue: string) => void;
  onBlur?: () => void;
  isDisabled?: boolean;
  autoFocus?: boolean;
  focusOwner?: string;
  setFocusOwner?: (id: string) => void;
  style?: React.CSSProperties;
  isValid: boolean;
}
