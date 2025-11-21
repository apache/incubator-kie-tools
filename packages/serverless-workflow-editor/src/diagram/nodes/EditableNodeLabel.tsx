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
import { EmptyLabel, NodeSwfObjects } from "./SwfNodes";
import { useSwfEditorStore, useSwfEditorStoreApi } from "../../store/StoreContext";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { useFocusableElement } from "../../focus/useFocusableElement";
import { flushSync } from "react-dom";
import { NodeLabelPosition } from "./SwfNodeSvgs";
import "./EditableNodeLabel.css";
import { useSettings } from "../../settings/SwfEditorSettingsContext";

export type OnEditableNodeLabelChange = (value: string | undefined) => void;

// we have to allow only unique names for the nodes and update the references to that node in the model/diagram
export function EditableNodeLabel({
  id,
  namedElement,
  isEditing: _isEditing,
  setEditing: _setEditing,
  value,
  onChange,
  position,
  truncate,
  grow,
  shouldCommitOnBlur,
  skipValidation,
  setLabelHeight,
  enableAutoFocusing,
}: {
  id?: string;
  shouldCommitOnBlur?: boolean;
  grow?: boolean;
  truncate?: boolean;
  namedElement?: NodeSwfObjects;
  position: NodeLabelPosition;
  isEditing: boolean;
  value: string | undefined;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  onChange: OnEditableNodeLabelChange;
  skipValidation?: boolean;
  setLabelHeight?: React.Dispatch<React.SetStateAction<number>>;
  enableAutoFocusing?: boolean;
}) {
  const displayValue = useSwfEditorStore((s) => {
    if (!value) {
      return undefined;
    }

    return value;
  });

  const isEditing = useMemo(() => {
    return _isEditing;
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

  const isValid = useSwfEditorStore((s) => {
    if (skipValidation) {
      return true;
    }

    // TODO: implement node name validation, returning true for now
    return true;
  });

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
      e.stopPropagation();

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

  useFocusableElement(
    ref,
    enableAutoFocusing ?? true ? id ?? namedElement?.["name"] : undefined,
    useCallback(
      (cb) => {
        setTimeout(() => {
          flushSync(() => {
            setEditing(true);
          });
          cb();
        }, 100);
      },
      [setEditing]
    )
  );

  return (
    <div className={`kie-swf-editor--editable-node-name-input ${position} ${grow ? "grow" : ""} ${""}`}>
      {(isEditing && (
        <input
          spellCheck={"false"}
          style={
            {
              // ...fontCssProperties,
            }
          }
          onMouseDownCapture={(e) => e.stopPropagation()} // Make sure mouse events stay inside the node.
          onKeyDown={onKeyDown}
          tabIndex={-1}
          ref={ref}
          onBlur={onBlur}
          onChange={(e) => setInternalValue(e.target.value)}
          value={internalValue}
        />
      )) || (
        <span
          // clientHeight isn't affected by the zoom in/out
          ref={(ref) => setLabelHeight?.(ref?.clientHeight ?? 0)}
          style={{
            whiteSpace: "pre-wrap",
            //...fontCssProperties,
          }}
        >
          {!displayValue ? (
            <EmptyLabel />
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
  const swfEditorStoreApi = useSwfEditorStoreApi();
  const settings = useSettings();

  const [isEditingLabel, setEditingLabel] = useState(
    !!id && !!swfEditorStoreApi.getState().focus.consumableId && swfEditorStoreApi.getState().focus.consumableId === id
  );

  const triggerEditing = useCallback<React.EventHandler<React.SyntheticEvent>>(
    (e) => {
      if (settings.isReadOnly) {
        return;
      }
      e.stopPropagation();
      e.preventDefault();
      setEditingLabel(true);
    },
    [settings.isReadOnly]
  );

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
