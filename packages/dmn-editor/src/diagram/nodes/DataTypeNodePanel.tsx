import * as React from "react";
import {
  DMN15__tInformationItem,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { TypeRefLabel } from "../../dataTypes/TypeRefLabel";

export function DataTypeNodePanel(props: {
  isVisible: boolean;
  variable: DMN15__tInformationItem | undefined;
  shape: DMNDI15__DMNShape | undefined;
}) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const { dataTypesByFeelName } = useDmnEditorDerivedStore();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const dataType = dataTypesByFeelName.get(props.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined);

  return (
    <>
      {props.isVisible && diagram.overlays.enableDataTypesToolbarOnNodes && (
        <div className={"kie-dmn-editor--data-type-node-panel"}>
          <Label
            className={"kie-dmn-editor--data-type-node-panel-label"}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                if (dataType) {
                  state.navigation.tab = DmnEditorTab.DATA_TYPES;
                  state.dataTypesEditor.activeItemDefinitionId = dataType.itemDefinition["@_id"];
                }
              });
            }}
          >
            <TypeRefLabel
              relativeToNamespace={dataType?.namespace}
              typeRef={props.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
              isCollection={dataType?.itemDefinition["@_isCollection"]}
            />
          </Label>
        </div>
      )}
    </>
  );
}
