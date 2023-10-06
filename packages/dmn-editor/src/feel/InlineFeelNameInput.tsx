import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { SPEC } from "../Spec";
import { UniqueNameIndex } from "../Spec";

export type OnInlineFeelNameRenamed = (newName: string) => void;

export const invalidInlineFeelNameStyle = {
  color: "red",
  textDecoration: "red dotted underline",
  textUnderlinePosition: "under",
};

export function InlineFeelNameInput({
  id,
  onRenamed,
  isReadonly,
  name,
  shouldCommitOnBlur,
  isPlain,
  allUniqueNames,
  validate,
  placeholder,
  onKeyDown,
  ...inputProps
}: React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement> & {
  id: string;
  onRenamed: OnInlineFeelNameRenamed;
  name: string;
  isReadonly: boolean;
  isPlain: boolean;
  shouldCommitOnBlur: boolean;
  allUniqueNames: UniqueNameIndex;
  placeholder?: string;
  validate?: typeof SPEC.namedElement.isValidName;
}) {
  const _validate = (validate ??= SPEC.namedElement.isValidName);

  const inputRef = useRef<HTMLInputElement>(null);

  const previouslyFocusedElement = useRef<Element | undefined>();

  const restoreFocus = useCallback(() => {
    // We only restore the focus to the previously focused element if we're still holding focus. If focus has changed, we let it be.
    setTimeout(() => {
      if (document.activeElement === inputRef.current) {
        (previouslyFocusedElement.current as any)?.focus?.();
      }
    }, 0);
  }, []);

  const [isValid, setValid] = useState(_validate(id, name, allUniqueNames));
  const updateIsValidFlag = useCallback(
    (name: string) => {
      const isValid = _validate(id, name, allUniqueNames);
      setValid(isValid);
      return isValid;
    },
    [_validate, allUniqueNames, id]
  );

  useEffect(() => {
    updateIsValidFlag(name);
  }, [name, updateIsValidFlag]);

  const { style: _style, disabled, defaultValue, ..._inputProps } = inputProps;

  return (
    <input
      spellCheck={"false"} // Let's not confuse FEEL name validation with the browser's grammar check.
      ref={inputRef}
      key={id}
      style={{
        ...(isPlain ? { border: 0, outline: "none", background: "transparent" } : {}),
        flexGrow: 1,
        display: "inline",
        width: "100%",
        ...(isValid ? {} : invalidInlineFeelNameStyle),
        ..._style,
      }}
      disabled={isReadonly}
      placeholder={placeholder ?? "Enter a name..."}
      onChange={(e) => updateIsValidFlag(e.currentTarget.value)}
      defaultValue={name}
      onFocus={(e) => {
        previouslyFocusedElement.current = document.activeElement ?? undefined; // Save potential focused element.
      }}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          e.stopPropagation();
          e.preventDefault();
          const isValid = updateIsValidFlag(e.currentTarget.value);
          if (isValid) {
            onRenamed(e.currentTarget.value);
          }
        } else if (e.key === "Escape") {
          e.stopPropagation();
          e.preventDefault();
          e.currentTarget.value = name;
          updateIsValidFlag(e.currentTarget.value);
          e.currentTarget.blur();
        }
        onKeyDown?.(e);
      }}
      onBlur={(e) => {
        if (isValid && shouldCommitOnBlur) {
          onRenamed(e.currentTarget.value);
        } else {
          e.currentTarget.value = name;
          updateIsValidFlag(e.currentTarget.value);
        }
        restoreFocus();
      }}
      {..._inputProps}
    />
  );
}
