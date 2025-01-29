/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { DMN15_SPEC, UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { useFocusableElement } from "../focus/useFocusableElement";
import { State } from "../store/Store";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export type OnInlineFeelNameRenamed = (newName: string) => void;

export const invalidInlineFeelNameStyle = {
  color: "red",
  textDecoration: "red dotted underline",
  textUnderlinePosition: "under",
};

export function InlineFeelNameInput({
  id,
  onRenamed,
  isReadOnly,
  name,
  shouldCommitOnBlur,
  isPlain,
  allUniqueNames,
  validate,
  placeholder,
  onKeyDown,
  saveInvalidValue,
  enableAutoFocusing,
  ...inputProps
}: React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement> & {
  id: string;
  onRenamed: OnInlineFeelNameRenamed;
  name: string;
  isReadOnly: boolean;
  isPlain: boolean;
  shouldCommitOnBlur: boolean;
  allUniqueNames: (s: State) => UniqueNameIndex;
  placeholder?: string;
  saveInvalidValue?: boolean;
  validate?: typeof DMN15_SPEC.namedElement.isValidName;
  enableAutoFocusing?: boolean;
}) {
  const _validate = (validate ??= DMN15_SPEC.namedElement.isValidName);

  const inputRef = useRef<HTMLInputElement>(null);

  const previouslyFocusedElement = useRef<Element | undefined>();

  useFocusableElement(inputRef, enableAutoFocusing ?? true ? id : undefined);

  const restoreFocus = useCallback(() => {
    // We only restore the focus to the previously focused element if we're still holding focus. If focus has changed, we let it be.
    setTimeout(() => {
      if (document.activeElement === inputRef.current) {
        (previouslyFocusedElement.current as any)?.focus?.();
      }
    }, 0);
  }, []);

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [isValid, setValid] = useState(_validate(id, name, allUniqueNames(dmnEditorStoreApi.getState())));
  const updateIsValidFlag = useCallback(
    (name: string) => {
      const isValid = _validate(id, name, allUniqueNames(dmnEditorStoreApi.getState()));
      setValid(isValid);
      return isValid;
    },
    [_validate, allUniqueNames, dmnEditorStoreApi, id]
  );

  useEffect(() => {
    updateIsValidFlag(name);
  }, [name, updateIsValidFlag]);

  // When the `name` prop changes externally, we need to update the value of the input, as the props are the source of truth.
  useEffect(() => {
    inputRef.current!.value = name;
  }, [name]);

  const { style: _style, disabled, defaultValue, ..._inputProps } = inputProps;

  const _placeholder = placeholder ?? "Enter a name...";

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
      size={2 + Math.max(0, _placeholder?.length ?? 0, name.length)}
      onInput={(e) => {
        (e.target as any).size = 2 + Math.max(0, _placeholder?.length ?? 0, (e.target as any).value.length ?? 0);
      }}
      disabled={isReadOnly}
      placeholder={_placeholder}
      onChange={(e) => updateIsValidFlag(e.currentTarget.value)}
      defaultValue={name}
      onFocus={(e) => {
        previouslyFocusedElement.current = document.activeElement ?? undefined; // Save potential focused element.
      }}
      onKeyDown={(e) => {
        onKeyDown?.(e);
        // In macOS, we can not stopPropagation here because, otherwise, shortcuts are not handled
        // See https://github.com/apache/incubator-kie-issues/issues/1164
        if (!(getOperatingSystem() === OperatingSystem.MACOS && e.metaKey)) {
          e.stopPropagation();
        }

        if (e.key === "Enter") {
          e.preventDefault();
          const isValid = updateIsValidFlag(e.currentTarget.value);
          if (isValid || saveInvalidValue) {
            onRenamed(e.currentTarget.value);
          }
        } else if (e.key === "Escape") {
          e.preventDefault();
          e.currentTarget.value = name;
          updateIsValidFlag(e.currentTarget.value);
          e.currentTarget.blur();
        }
      }}
      onBlur={(e) => {
        if ((isValid || saveInvalidValue) && shouldCommitOnBlur) {
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
