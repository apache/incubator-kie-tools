import { DmnDataType, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useMemo } from "react";

export function useDataTypes(definitions: DMN15__tDefinitions) {
  const builtInDataTypes = useMemo<DmnDataType[]>(
    () =>
      Object.keys(DmnBuiltInDataType).map((k) => ({
        isCustom: false,
        typeRef: (DmnBuiltInDataType as any)[k],
        name: (DmnBuiltInDataType as any)[k],
      })),
    []
  );

  const customDataTypes = useMemo<DmnDataType[]>(
    () =>
      (definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"]!,
      })),
    [definitions.itemDefinition]
  );

  const importedDataTypes = useMemo<DmnDataType[]>(() => {
    return [];
  }, []);

  return useMemo(() => {
    return {
      all: [...builtInDataTypes, ...customDataTypes, ...importedDataTypes],
      builtInDataTypes,
      customDataTypes,
      importedDataTypes,
    };
  }, [builtInDataTypes, customDataTypes, importedDataTypes]);
}
