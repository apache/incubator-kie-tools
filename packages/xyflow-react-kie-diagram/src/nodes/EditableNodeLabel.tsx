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
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { NodeLabelPosition } from "../nodes/NodeSvgs";
import "./EditableNodeLabel.css";
import { KieDiagramI18n, useKieDiagramI18n } from "../i18n";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export const INVALID_NAME_CSS_PROPS = {
  color: "red",
  textDecoration: "red dotted underline",
  textUnderlinePosition: "under",
};

export type OnEditableNodeLabelChange = (value: string | undefined) => void;

export function EditableNodeLabel({
  id,
  name,
  isEditing: _isEditing,
  setEditing: _setEditing,
  value,
  placeholder,
  onChange,
  position,
  truncate,
  grow,
  shouldCommitOnBlur,
  skipValidation,
  validate,
  fontCssProperties,
  setLabelHeight,
  enableAutoFocusing,
}: {
  id?: string;
  shouldCommitOnBlur?: boolean;
  grow?: boolean;
  truncate?: boolean;
  name: string | undefined;
  position: NodeLabelPosition;
  isEditing: boolean;
  value: string | undefined;
  placeholder?: string;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  onChange: OnEditableNodeLabelChange;
  skipValidation?: boolean;
  validate: (name: string | undefined) => boolean;
  fontCssProperties?: React.CSSProperties;
  setLabelHeight?: React.Dispatch<React.SetStateAction<number>>;
  enableAutoFocusing?: boolean;
}) {
  const { i18n } = useKieDiagramI18n();
  const displayValue = useMemo(() => {
    if (!value) {
      return undefined;
    }

    if (!name) {
      return value;
    }

    return value;
  }, [name, value]);

  const isEditing = useMemo(() => {
    return _isEditing; // Can't ever change the names of external nodes
  }, [_isEditing]);

  const setEditing = useCallback<React.Dispatch<React.SetStateAction<boolean>>>(
    (args) => {
      _setEditing(args);
    },
    [_setEditing]
  );

  const [internalValue, setInternalValue] = useState(displayValue);
  useEffect(() => {
    // Give `value` priority over `internalValue`, if it changes externally, we take that as the new `internalValue`.
    setInternalValue(displayValue);
  }, [displayValue]);

  // If `shouldCommitOnBlur` is true, pressing `Esc` will override this.
  // If `shouldCommitOnBlur` is false, pressing `Enter` will override this.
  const _shouldCommitOnBlur = shouldCommitOnBlur ?? false; // Defaults to false
  const [shouldCommit, setShouldCommit] = useState(_shouldCommitOnBlur);
  useEffect(() => {
    setShouldCommit(_shouldCommitOnBlur); // Keeps the internal state aligned with the prop.
  }, [_shouldCommitOnBlur]);

  const restoreFocus = useCallback(() => {
    // We only restore the focus to the previously focused element if we're still holding focus. If focus has changed, we let it be.
    setTimeout(() => {
      if (document.activeElement === ref.current) {
        (previouslyFocusedElement.current as any)?.focus?.();
      }
    }, 0);
  }, []);

  const isValid = useMemo(() => {
    if (skipValidation) {
      return true;
    }

    return validate(value);
  }, [skipValidation, validate, value]);

  const onBlur = useCallback(() => {
    setEditing(false);
    setShouldCommit(_shouldCommitOnBlur); // Only gets propagated after this function ends. `shouldCommit` is unchanged after this line.
    restoreFocus();

    if (isValid && internalValue !== value && shouldCommit) {
      onChange(internalValue);
    } else {
      console.debug(`Label change cancelled for node with label ${value}`);
      setInternalValue(value);
    }
  }, [internalValue, onChange, restoreFocus, _shouldCommitOnBlur, setEditing, shouldCommit, isValid, value]);

  // Finish editing on `Enter` pressed.
  const onKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      // In macOS, we can not stopPropagation here because, otherwise, shortcuts are not handled
      // See https://github.com/apache/incubator-kie-issues/issues/1164
      if (!(getOperatingSystem() === OperatingSystem.MACOS && e.metaKey)) {
        e.stopPropagation();
      }
      if (e.key === "Enter") {
        if (!isValid) {
          return; // Simply ignore and don't allow user to go outside the component using only the keyboard.
        } else {
          setShouldCommit(true);
          restoreFocus(); // This will trigger `onBlur`, which will commit the change.
        }
      } else if (e.key === "Escape") {
        setShouldCommit(false);
        restoreFocus(); // This will trigger `onBlur`, which will ignore  the change.
      }
    },
    [restoreFocus, isValid]
  );

  // Very important to restore the focus after editing is done.
  const previouslyFocusedElement = useRef<Element | undefined>();
  useLayoutEffect(() => {
    if (isEditing) {
      previouslyFocusedElement.current = document.activeElement ?? undefined; // Save potential focused element. Most likely the node itself.
      ref.current?.focus();
    }
  }, [isEditing]);

  // Make sure the component is rendered with its text already selected.
  // `useLayoutEffect` is just like `useEffect`, but runs before the DOM mutates.
  useLayoutEffect(() => {
    if (isEditing) {
      ref.current?.setSelectionRange(0, 0);
    }
  }, [isEditing]);
  // It's important to do this in two steps, so the text is selected and shows always from the start, not from the end.
  useEffect(() => {
    if (isEditing) {
      ref.current?.setSelectionRange(0, ref.current?.value.length, "forward");
    }
  }, [isEditing]);

  const ref = useRef<HTMLInputElement>(null);

  return (
    <div className={`xyflow-react-kie-diagram--editable-node-name-input ${position} ${grow ? "grow" : ""}`}>
      {(isEditing && (
        <input
          spellCheck={"false"} // Let's not confuse FEEL name validation with the browser's grammar check.
          style={{
            ...fontCssProperties,
            ...(isValid ? {} : INVALID_NAME_CSS_PROPS),
          }}
          onMouseDownCapture={(e) => e.stopPropagation()} // Make sure mouse events stay inside the node.
          onKeyDown={onKeyDown}
          tabIndex={-1}
          ref={ref}
          onBlur={onBlur}
          placeholder={placeholder}
          onChange={(e) => setInternalValue(e.target.value)}
          value={internalValue}
        />
      )) || (
        <span
          // clientHeight isn't affected by the zoom in/out
          ref={(ref) => setLabelHeight?.(ref?.clientHeight ?? 0)}
          style={{
            whiteSpace: "pre-wrap",
            ...fontCssProperties,
            ...(isValid ? {} : INVALID_NAME_CSS_PROPS),
          }}
        >
          {!displayValue ? (
            <EmptyLabel i18n={i18n} />
          ) : !truncate ? (
            displayValue
          ) : (
            <Truncate content={displayValue} tooltipPosition={"right-end"} />
          )}
        </span>
      )}
    </div>
  );
}

export function useEditableNodeLabel(id: string | undefined) {
  const [isEditingLabel, setEditingLabel] = useState(false);

  const triggerEditing = useCallback<React.EventHandler<React.SyntheticEvent>>((e) => {
    e.stopPropagation();
    e.preventDefault();
    setEditingLabel(true);
  }, []);

  // Trigger editing on `Enter` pressed.
  const triggerEditingIfEnter = useCallback<React.KeyboardEventHandler>(
    (e) => {
      if (e.key === "Enter") {
        triggerEditing(e);
      }
    },
    [triggerEditing]
  );

  return useMemo(
    () => ({ isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter }),
    [isEditingLabel, triggerEditing, triggerEditingIfEnter]
  );
}

export function EmptyLabel(props: { i18n: KieDiagramI18n }) {
  return (
    <span style={{ fontFamily: "serif", paddingTop: "8px" }}>
      <i style={{ opacity: 0.5, fontSize: "0.8em", lineHeight: "0.8em" }}>{props.i18n.diagram.doubleClickToChange}</i>
    </span>
  );
}
