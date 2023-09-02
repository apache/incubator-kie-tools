import * as React from "react";
import {
  DMN15__tInformationItem,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditorStore } from "../../store/Store";

export function DataTypeNodePanel(props: {
  isVisible: boolean;
  variable: DMN15__tInformationItem | undefined;
  shape: DMNDI15__DMNShape | undefined;
}) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  return (
    <>
      {props.isVisible && diagram.overlays.enableDataTypesOnNodes && (
        <Label className={"kie-dmn-editor--data-type-node-panel"} isCompact={true}>{`🔹 ${
          props.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined
        }`}</Label>
      )}
    </>
  );
}
