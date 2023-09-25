import * as React from "react";
import { useCallback, useRef } from "react";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { EditableNodeLabel, useEditableNodeLabel } from "../diagram/nodes/EditableNodeLabel";
import { DataTypeLabel } from "./DataTypeLabel";
import { traverse } from "./DataTypeSpec";
import { EditItemDefinition } from "./DataTypes";

export function DataTypeName({
  itemDefinition,
  isActive,
  editItemDefinition,
  editMode,
}: {
  editMode: "hover" | "double-click";
  itemDefinition: DMN15__tItemDefinition;
  isActive: boolean;
  editItemDefinition: EditItemDefinition;
}) {
  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  const onRenamed = useCallback(
    (newName: string | undefined) => {
      if (!newName?.trim()) {
        return;
      }

      editItemDefinition(itemDefinition["@_id"]!, (itemComponent, items, index, all) => {
        // Only recursively rename if the itemDefinition being renamed is top-level.
        if (all === items) {
          traverse(all, (item) => {
            if (item.typeRef === itemComponent["@_name"]) {
              item.typeRef = newName?.trim();
            }
          });
        }

        itemComponent["@_name"] = newName.trim();
      });
    },
    [editItemDefinition, itemDefinition]
  );

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

  return (
    <>
      {editMode === "hover" && (
        <input
          ref={inputRef}
          key={itemDefinition["@_id"] + itemDefinition["@_name"]}
          style={{
            border: 0,
            flexGrow: 1,
            outline: "none",
            display: "inline",
            background: "transparent",
            width: "100%",
          }}
          defaultValue={itemDefinition["@_name"]}
          onFocus={(e) => {
            previouslyFocusedElement.current = document.activeElement ?? undefined; // Save potential focused element.
          }}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.stopPropagation();
              e.preventDefault();
              e.currentTarget.value = e.currentTarget.value.trimStart();
              onRenamed(e.currentTarget.value);
            } else if (e.key === "Escape") {
              e.stopPropagation();
              e.preventDefault();
              e.currentTarget.value = itemDefinition["@_name"];
              e.currentTarget.blur();
            }
          }}
          onBlur={(e) => {
            onRenamed(e.currentTarget.value);
            restoreFocus();
          }}
        />
      )}
      {editMode === "double-click" && (
        <Flex
          tabIndex={-1}
          style={isEditingLabel ? { flexGrow: 1 } : {}}
          flexWrap={{ default: "nowrap" }}
          spaceItems={{ default: "spaceItemsNone" }}
          justifyContent={{ default: "justifyContentFlexStart" }}
          alignItems={{ default: "alignItemsCenter" }}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <EditableNodeLabel
            truncate={true}
            grow={true}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            onChange={onRenamed}
            saveOnBlur={true}
            value={itemDefinition["@_name"]}
            key={itemDefinition["@_id"]}
            position={"top-left"}
            namedElement={itemDefinition}
            namedElementQName={{ type: "xml-qname", localPart: itemDefinition["@_name"] }}
          />
          {!isEditingLabel && (
            <DataTypeLabel typeRef={itemDefinition.typeRef} isCollection={itemDefinition["@_isCollection"]} />
          )}
        </Flex>
      )}
    </>
  );
}
