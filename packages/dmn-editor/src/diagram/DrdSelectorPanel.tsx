import * as React from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { addOrGetDrd, getDefaultDrdName } from "../mutations/addOrGetDrd";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export function DrdSelectorPanel() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const diagram = useDmnEditorStore((s) => s.diagram);

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
        <TextContent>
          <Text component="h3">DRDs</Text>
        </TextContent>
        <Button
          variant={ButtonVariant.link}
          onClick={() => {
            dmnEditorStoreApi.setState((state) => {
              const newIndex = (state.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.length ?? -1) + 1;
              addOrGetDrd({
                definitions: state.dmn.model.definitions,
                drdIndex: newIndex,
              });
              state.diagram.drdIndex = newIndex;
            });
          }}
        >
          <PlusCircleIcon />
        </Button>
      </Flex>
      <Divider style={{ marginBottom: "8px" }} />
      <div className={"kie-dmn-editor--drd-list"}>
        {thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.map((drd, i) => (
          <button
            key={drd["@_id"] ?? i}
            className={i === diagram.drdIndex ? "active" : undefined}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.drdIndex = i;
                state.diagram.drdSelector.isOpen = false;
              });
            }}
          >
            {drd["@_name"] || getDefaultDrdName({ drdIndex: i })}
          </button>
        ))}
      </div>
    </>
  );
}
