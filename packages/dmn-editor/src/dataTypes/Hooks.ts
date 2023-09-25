import { DmnBuiltInDataType, DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useMemo } from "react";

export type DmnEditorDataTypeReference = DmnDataType & {
  namespace: string;
  itemDefinition: DMN15__tItemDefinition | undefined;
};

export function useDataTypes() {
  const builtInDataTypes = useMemo<DmnEditorDataTypeReference[]>(
    () =>
      Object.values(DmnBuiltInDataType).map((feelType) => ({
        isCustom: false,
        typeRef: feelType,
        name: feelType,
        itemDefinition: undefined,
        namespace: "",
      })),
    []
  );

  return useMemo(() => ({ builtInDataTypes }), [builtInDataTypes]);
}
