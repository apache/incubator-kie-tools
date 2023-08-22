import * as React from "react";
import {
  DMN14__tInformationItem,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditorStore } from "../../store/Store";

export function DataTypeNodePanel(props: {
  isVisible: boolean;
  variable: DMN14__tInformationItem | undefined;
  shape: DMNDI13__DMNShape | undefined;
}) {
  const { diagram } = useDmnEditorStore();
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
