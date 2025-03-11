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
import { EmptyLabel } from "./Nodes";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { buildFeelQNameFromXmlQName } from "../../feel/buildFeelQName";
import { DMN15__tNamedElement } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { useFocusableElement } from "../../focus/useFocusableElement";
import { flushSync } from "react-dom";
import { NodeLabelPosition } from "./NodeSvgs";
import { State } from "../../store/Store";
import "./EditableNodeLabel.css";
import { useSettings } from "../../settings/DmnEditorSettingsContext";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export type OnEditableNodeLabelChange = (value: string | undefined) => void;

export function EditableNodeLabel({
  id,
  namedElement,
  namedElementQName,
  isEditing: _isEditing,
  setEditing: _setEditing,
  value,
  onChange,
  position,
  truncate,
  grow,
  shouldCommitOnBlur,
  skipValidation,
  onGetAllUniqueNames,
  fontCssProperties,
  setLabelHeight,
  enableAutoFocusing,
}: {
  id?: string;
  shouldCommitOnBlur?: boolean;
  grow?: boolean;
  truncate?: boolean;
  namedElement?: Normalized<DMN15__tNamedElement>;
  namedElementQName?: XmlQName;
  position: NodeLabelPosition;
  isEditing: boolean;
  value: string | undefined;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  onChange: OnEditableNodeLabelChange;
  skipValidation?: boolean;
  onGetAllUniqueNames: (s: State) => UniqueNameIndex;
  fontCssProperties?: React.CSSProperties;
  setLabelHeight?: React.Dispatch<React.SetStateAction<number>>;
  enableAutoFocusing?: boolean;
}) {
  const displayValue = useDmnEditorStore((s) => {
    if (!value) {
      return undefined;
    }

    if (!namedElement || !namedElementQName) {
      return value;
    }

    const feelName = buildFeelQNameFromXmlQName({
      namedElement,
      importsByNamespace: s.computed(s).importsByNamespace(),
      model: s.dmn.model.definitions,
      namedElementQName,
      relativeToNamespace: s.dmn.model.definitions["@_namespace"],
    });

    return feelName.full;
  });

  const isEditing = useMemo(() => {
    return !namedElementQName?.prefix && _isEditing; // Can't ever change the names of external nodes
  }, [_isEditing, namedElementQName?.prefix]);

  const setEditing = useCallback<React.Dispatch<React.SetStateAction<boolean>>>(
    (args) => {
      // Can't ever change the names of external nodes
      if (namedElementQName?.prefix) {
        return;
      }

      _setEditing(args);
    },
    [_setEditing, namedElementQName?.prefix]
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

  const isValid = useDmnEditorStore((s) => {
    if (skipValidation) {
      return true;
    }

    return DMN15_SPEC.namedElement.isValidName(
      namedElement?.["@_id"] ?? generateUuid(),
      internalValue,
      onGetAllUniqueNames(s)
    );
  });

  const onBlur = useCallback(() => {
    setEditing(false);
    setShouldCommit(_shouldCommitOnBlur); // Only gets propagated after this function ends. `shouldCommit` is unchanged after this line.
    restoreFocus();

    if (isValid && internalValue !== value && shouldCommit) {
      onChange(internalValue);
      setInternalValue(value); // Reset the component after the commit
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

  useFocusableElement(
    ref,
    enableAutoFocusing ?? true ? id ?? namedElement?.["@_id"] : undefined,
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
    <div
      className={`kie-dmn-editor--editable-node-name-input ${position} ${grow ? "grow" : ""} ${
        namedElementQName?.prefix ? "kie-dmn-editor--node-external" : ""
      }`}
    >
      {(isEditing && (
        <input
          spellCheck={"false"} // Let's not confuse FEEL name validation with the browser's grammar check.
          style={{
            ...fontCssProperties,
            ...(isValid ? {} : invalidInlineFeelNameStyle),
          }}
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
            ...fontCssProperties,
            ...(isValid ? {} : invalidInlineFeelNameStyle),
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
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const settings = useSettings();

  const [isEditingLabel, setEditingLabel] = useState(
    !!id && !!dmnEditorStoreApi.getState().focus.consumableId && dmnEditorStoreApi.getState().focus.consumableId === id
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
