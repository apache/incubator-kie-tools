import { useMemo } from "react";
import { DmnBuiltInDataType, DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { useDmnEditorDependencies } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditorStore } from "../store/Store";

export type DmnEditorDataTypeReference = DmnDataType & {
  namespace: string;
};

export function useDataTypes() {
  const dmn = useDmnEditorStore((s) => s.dmn);
  const { importsByNamespace } = useDmnEditorDerivedStore();
  const { dependenciesByNamespace } = useDmnEditorDependencies();

  const builtInDataTypes = useMemo<DmnEditorDataTypeReference[]>(
    () =>
      Object.values(DmnBuiltInDataType).map((feelType) => ({
        isCustom: false,
        typeRef: feelType,
        name: feelType,
        namespace: "",
      })),
    []
  );

  const customDataTypes = useMemo<DmnEditorDataTypeReference[]>(
    () =>
      (dmn.model.definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"],
        namespace: dmn.model.definitions["@_namespace"],
      })),
    [dmn.model.definitions]
  );

  // Data Types are not transitively imported, but there can be references to 2nd degree dependencies for external data types on
  // included models. 1st degree imported data types can reference them by their local namespace name, which can result in clases
  // with the local namesapce declared for `dmn.model.definitions`. In this case, we're better off referring to 2nd degree
  // dependencies by their namespace. If `dmn.model.definitions` imports such namespace, we can use the local namespace declaration
  // to refer to it.
  const externalDataTypes = useMemo<DmnEditorDataTypeReference[]>(() => {
    return (dmn.model.definitions.import ?? []).flatMap((_import) => {
      const dependency = dependenciesByNamespace[_import["@_namespace"]];
      if (!dependency) {
        console.warn(
          `DMN DIAGRAM: Can't determine External Data Types for model with namespace '${_import["@_namespace"]}' because it doesn't exist on the dependencies object'.`
        );
        return [];
      }

      return (dependency.model.definitions.itemDefinition ?? []).map((item) => ({
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
  }, [dependenciesByNamespace, dmn.model.definitions.import, importsByNamespace]);

  return useMemo(() => {
    return {
      all: [...builtInDataTypes, ...customDataTypes, ...externalDataTypes],
      builtInDataTypes,
      customDataTypes,
      externalDataTypes,
    };
  }, [builtInDataTypes, customDataTypes, externalDataTypes]);
}
