import { useMemo } from "react";
import { DmnBuiltInDataType, DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { useOtherDmns } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditorStore } from "../store/Store";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type DmnEditorDataTypeReference = DmnDataType & {
  namespace: string;
  itemDefinition: DMN15__tItemDefinition | undefined;
};

export function useDataTypes() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const { importsByNamespace } = useDmnEditorDerivedStore();
  const { otherDmnsByNamespace } = useOtherDmns();

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

  const customDataTypes = useMemo<DmnEditorDataTypeReference[]>(
    () =>
      (thisDmn.model.definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"],
        itemDefinition: item,
        namespace: thisDmn.model.definitions["@_namespace"],
      })),
    [thisDmn.model.definitions]
  );

  // Data Types are not transitively imported, but there can be references to 2nd degree dependencies for external data types on
  // included models. 1st degree imported data types can reference them by their local namespace name, which can result in clases
  // with the local namesapce declared for `dmn.model.definitions`. In this case, we're better off referring to 2nd degree
  // dependencies by their namespace. If `dmn.model.definitions` imports such namespace, we can use the local namespace declaration
  // to refer to it.
  const externalDataTypes = useMemo<DmnEditorDataTypeReference[]>(() => {
    return (thisDmn.model.definitions.import ?? []).flatMap((_import) => {
      const otherDmn = otherDmnsByNamespace[_import["@_namespace"]];
      if (!otherDmn) {
        console.warn(
          `DMN DIAGRAM: Can't determine External Data Types for model with namespace '${_import["@_namespace"]}' because it doesn't exist on the dependencies object'.`
        );
        return [];
      }

      return (otherDmn.model.definitions.itemDefinition ?? []).map((item) => ({
        itemDefinition: item,
        isCustom: true,
        typeRef: item.itemComponent
          ? (undefined as any) //FIXME: Tiago --> The `DmnDataType` interface is very limited...
          : buildFeelQNameFromNamespace({
              importsByNamespace,
              namedElement: { "@_name": item.typeRef! },
              namespace: _import["@_namespace"],
            }).full,
        name: buildFeelQNameFromNamespace({
          importsByNamespace,
          namedElement: item,
          namespace: _import["@_namespace"],
        }).full,
        namespace: _import["@_namespace"],
      }));
    });
  }, [otherDmnsByNamespace, thisDmn.model.definitions.import, importsByNamespace]);

  return useMemo(() => {
    return {
      all: [...builtInDataTypes, ...customDataTypes, ...externalDataTypes],
      builtInDataTypes,
      customDataTypes,
      externalDataTypes,
    };
  }, [builtInDataTypes, customDataTypes, externalDataTypes]);
}
