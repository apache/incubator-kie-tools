import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { EmptyLabel } from "./Nodes";
import { XmlQName } from "../../xml/xmlQNames";
import { useDmnEditorStore } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { buildFeelQNameFromXmlQName } from "../../feel/buildFeelQName";
import { DMN15__tNamedElement } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { SPEC } from "../../Spec";
import "./EditableNodeLabel.css";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";

export type OnEditableNodeLabelChange = (value: string | undefined) => void;

export function EditableNodeLabel({
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
}: {
  shouldCommitOnBlur?: boolean;
  grow?: boolean;
  truncate?: boolean;
  namedElement?: DMN15__tNamedElement;
  namedElementQName?: XmlQName;
  position?: "center-center" | "top-center" | "center-left" | "top-left";
  isEditing: boolean;
  value: string | undefined;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  onChange: OnEditableNodeLabelChange;
  skipValidation?: boolean;
}) {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const { importsByNamespace } = useDmnEditorDerivedStore();

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

  const [internalValue, setInternalValue] = useState(value);
  useEffect(() => {
    // Give `value` priority over `internalValue`, if it changes externally, we take that as the new `internalValue`.
    setInternalValue(value);
  }, [value]);

  const [shouldCommit, setShouldCommit] = useState(false);

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

    const valid = true; // FIXME: Tiago --> Do additional checks here like unicity etc
    return valid && SPEC.namedElement.isValidName(internalValue);
  }, [internalValue, skipValidation]);

  const onBlur = useCallback(() => {
    setEditing(false);
    setShouldCommit(shouldCommitOnBlur ?? false);
    restoreFocus();

    if (isValid && internalValue !== value && shouldCommit) {
      onChange(internalValue);
    } else {
      console.debug(`Label change cancelled for node with label ${value}`);
      setInternalValue(value);
    }
  }, [internalValue, onChange, restoreFocus, shouldCommitOnBlur, setEditing, shouldCommit, isValid, value]);

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

  const positionClass = position ?? "center-center";

  const displayValue = useMemo(() => {
    if (!value) {
      return <EmptyLabel />;
    }

    if (!namedElement || !namedElementQName) {
      return truncate ? <Truncate content={value} tooltipPosition={"right-end"} /> : value;
    }

    const feelName = buildFeelQNameFromXmlQName({
      namedElement,
      importsByNamespace,
      model: thisDmn.model.definitions,
      namedElementQName,
      relativeToNamespace: thisDmn.model.definitions["@_namespace"],
    });

    return truncate ? <Truncate content={feelName.full} tooltipPosition={"right-end"} /> : feelName.full;
  }, [value, namedElement, namedElementQName, importsByNamespace, thisDmn.model.definitions, truncate]);

  return (
    <div className={`kie-dmn-editor--editable-node-name-input ${positionClass} ${grow ? "grow" : ""}`}>
      {(isEditing && (
        <input
          spellCheck={"false"} // Let's not confuse FEEL name validation with the browser's grammar check.
          style={{
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
          style={{
            whiteSpace: "pre-wrap",
            ...(isValid ? {} : invalidInlineFeelNameStyle),
          }}
        >
          {displayValue}
        </span>
      )}
    </div>
  );
}

export function useEditableNodeLabel() {
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

  return { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter };
}
