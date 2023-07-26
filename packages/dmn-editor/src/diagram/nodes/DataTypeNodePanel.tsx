import * as React from "react";
import * as RF from "reactflow";
import {
  DMN14__tInformationItem,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";

export function DataTypeNodePanel(props: {
  isVisible: boolean;
  variable: DMN14__tInformationItem | undefined;
  shape: DMNDI13__DMNShape | undefined;
}) {
  return (
    <>
      {props.isVisible && (
        <Label className={"kie-dmn-editor--data-type-node-panel"} isCompact={true}>{`🔹 ${
          props.variable?.["@_typeRef"] ?? "<Undefined>"
        }`}</Label>
      )}
    </>
  );
}
