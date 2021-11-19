/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { joinName } from "uniforms";
import * as React from "react";
import { PropsWithChildren, useCallback, useEffect, useMemo, useRef } from "react";
import { AutoField } from "./AutoField";
import { DataType } from "@kogito-tooling/boxed-expression-component/dist/api";
import { DmnRunnerRule } from "../boxed";
import { DecisionResult, DmnSchemaProperties, DmnValidator, FORMS_ID, Result } from "../dmn";
import { ColumnInstance } from "react-table";
import { DmnTableJsonSchemaBridge } from "../dmn/DmnTableJsonSchemaBridge";
import { DmnAutoRow, DmnAutoRowApi } from "../dmn/DmnAutoRow";
import { diff } from "deep-object-diff";

interface InputField {
  dataType: DataType;
  width: number;
  name: string;
  cellDelegate: (formId: string) => React.ReactNode;
}

interface InputWithInsideProperties extends InputField {
  insideProperties: Array<InputField>;
}

type InputFields = InputField | InputWithInsideProperties;

export function isInputWithInsideProperties(toBeDetermined: InputFields): toBeDetermined is InputWithInsideProperties {
  return (toBeDetermined as InputWithInsideProperties).insideProperties !== undefined;
}

interface OutputField {
  dataType: DataType;
  width?: number;
  name: string;
}

interface OutputTypesField extends OutputField {
  type: string;
}

interface OutputWithInsideProperties extends OutputTypesField {
  insideProperties: Array<OutputTypesField>;
}

type OutputTypesFields = OutputTypesField | OutputWithInsideProperties;
type OutputFields = OutputField | OutputWithInsideProperties;
type OutputTypesAndNormalFields = OutputTypesFields | OutputFields;

export function isOutputWithInsideProperties(
  toBeDetermined: OutputTypesAndNormalFields
): toBeDetermined is OutputWithInsideProperties {
  return (toBeDetermined as OutputWithInsideProperties).insideProperties !== undefined;
}

export function usePrevious<T>(value: T) {
  const ref = useRef<T>();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

export function useGrid(
  jsonSchema: object,
  results: Array<DecisionResult[] | undefined> | undefined,
  inputRows: Array<object>,
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>,
  rowCount: number,
  formsDivRendered: boolean,
  rowsRef: Map<number, React.RefObject<DmnAutoRowApi> | null>,
  inputColumnsCache: React.MutableRefObject<ColumnInstance[]>,
  outputColumnsCache: React.MutableRefObject<ColumnInstance[]>,
  defaultModel: React.MutableRefObject<Array<object>>,
  defaultValues: object
) {
  const removeInputName = useCallback((fullName: string) => {
    return fullName.match(/\./) ? fullName.split(".").slice(1).join("-") : fullName;
  }, []);

  const getDataTypeProps = useCallback((type: string | undefined) => {
    let extractedType = (type ?? "").split("FEEL:").pop();
    if ((extractedType?.length ?? 0) > 1) {
      extractedType = (type ?? "").split(":").pop()?.split("}").join("").trim();
    }
    switch (extractedType) {
      case "<Undefined>":
        return { dataType: DataType.Undefined, width: 150 };
      case "Any":
        return { dataType: DataType.Any, width: 150 };
      case "boolean":
        return { dataType: DataType.Boolean, width: 150 };
      case "context":
        return { dataType: DataType.Context, width: 150 };
      case "date":
        return { dataType: DataType.Date, width: 180 };
      case "date and time":
        return { dataType: DataType.DateTime, width: 296 };
      case "days and time duration":
        return { dataType: DataType.DateTimeDuration, width: 150 };
      case "number":
        return { dataType: DataType.Number, width: 150 };
      case "string":
        return { dataType: DataType.String, width: 150 };
      case "time":
        return { dataType: DataType.Time, width: 150 };
      case "years and months duration":
        return { dataType: DataType.YearsMonthsDuration, width: 150 };
      default:
        return { dataType: (extractedType as DataType) ?? DataType.Undefined, width: 150 };
    }
  }, []);

  const deepGenerateInputFields = useCallback(
    (jsonSchemaBridge: DmnTableJsonSchemaBridge, fieldName: any, parentName = ""): InputFields | undefined => {
      const joinedName = joinName(parentName, fieldName);
      if (jsonSchemaBridge) {
        const field = jsonSchemaBridge.getField(joinedName);

        if (field.type === "object") {
          const insideProperties: Array<InputField> = jsonSchemaBridge
            .getSubfields(joinedName)
            .reduce((acc: Array<InputField>, subField: string) => {
              const field = deepGenerateInputFields(
                jsonSchemaBridge,
                subField,
                joinedName
              ) as InputWithInsideProperties;
              if (field && field.insideProperties) {
                return [...acc, ...field.insideProperties];
              }
              return [...acc, field];
            }, []);
          return {
            ...getDataTypeProps(field["x-dmn-type"]),
            insideProperties,
            name: joinedName,
            width: insideProperties.reduce((acc, insideProperty) => acc + insideProperty.width, 0),
          } as InputWithInsideProperties;
        }
        return {
          ...getDataTypeProps(field["x-dmn-type"]),
          name: removeInputName(joinedName),
          cellDelegate: (formId: string) => <AutoField key={joinedName} name={joinedName} form={formId} />,
        } as InputField;
      }
    },
    [getDataTypeProps, removeInputName]
  );

  const generateInputFields = useCallback(
    (jsonSchemaBridge: DmnTableJsonSchemaBridge) => {
      const subfields = jsonSchemaBridge?.getSubfields();
      return (
        subfields?.reduce((acc: Array<InputFields>, fieldName: string) => {
          const generateInputFields = deepGenerateInputFields(jsonSchemaBridge, fieldName);
          if (generateInputFields) {
            return [...acc, generateInputFields];
          }
        }, [] as Array<InputFields>) ?? []
      );
    },
    [deepGenerateInputFields]
  );

  // when update schema, re-create the json schema and generate the new inputs using the saved columns properties
  const jsonSchemaBridge = useMemo(() => {
    return new DmnValidator().getBridge(jsonSchema ?? {});
  }, [jsonSchema]);
  const previousBridge = usePrevious(jsonSchemaBridge);

  const inputs = useMemo(() => {
    const newInputs = generateInputFields(jsonSchemaBridge);
    inputColumnsCache.current?.map((column) => {
      if (column.groupType === "input") {
        const inputToUpdate = newInputs.find((e) => e.name === column.label);
        if (inputToUpdate && isInputWithInsideProperties(inputToUpdate) && column?.columns) {
          inputToUpdate.insideProperties.forEach((insideProperty) => {
            const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insideProperty.name);
            if (columnFound && columnFound.width) {
              insideProperty.width = columnFound.width as number;
            }
          });
        } else if (inputToUpdate && isInputWithInsideProperties(inputToUpdate) && column.width) {
          inputToUpdate.insideProperties.forEach((insideProperty) => {
            const width = (column.width as number) / inputToUpdate.insideProperties.length;
            if (width < 150) {
              insideProperty.width = 150;
            } else {
              insideProperty.width = width;
            }
          });
        }
        if (inputToUpdate && column.width && column.width > inputToUpdate.width) {
          inputToUpdate.width = column.width as number;
        }
      }
    });
    return newInputs;
  }, [inputColumnsCache, generateInputFields, jsonSchemaBridge]);

  useEffect(() => {
    if (previousBridge === undefined) {
      return;
    }
    setInputRows((previousData) => {
      const newData = [...previousData];
      const propertiesDifference = diff(
        (previousBridge.schema ?? {}).definitions?.InputSet?.properties ?? {},
        jsonSchemaBridge.schema?.definitions?.InputSet?.properties ?? {}
      );

      const updatedData = newData.map((data) => {
        return Object.entries(propertiesDifference).reduce(
          (row, [property, value]) => {
            if (Object.keys(row).length === 0) {
              return row;
            }
            if (!value || value.type || value.$ref) {
              delete (row as any)[property];
            }
            if (value?.["x-dmn-type"]) {
              (row as any)[property] = undefined;
            }
            return row;
          },
          { ...defaultValues, ...data }
        );
      });

      defaultModel.current = updatedData;
      return updatedData;
    });
  }, [defaultModel, defaultValues, jsonSchemaBridge, previousBridge, setInputRows]);

  const updateWidth = useCallback(
    (output: any[]) => {
      inputColumnsCache.current?.forEach((column) => {
        if (column.groupType === "input") {
          const inputToUpdate = inputs.find((i) => i.name === column.label);
          if (inputToUpdate && isInputWithInsideProperties(inputToUpdate) && column?.columns) {
            inputToUpdate.insideProperties.forEach((insideProperty) => {
              const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insideProperty.name);
              if (columnFound && columnFound.width) {
                insideProperty.width = columnFound.width as number;
              }
            });
          }
          if (inputToUpdate && column.width) {
            inputToUpdate.width = column.width as number;
          }
        }
      });

      outputColumnsCache.current?.forEach((column) => {
        if (column.groupType === "output") {
          const outputToUpdate = output.find((e) => e.name === column.label);
          if (outputToUpdate?.insideProperties && column?.columns) {
            (outputToUpdate.insideProperties as any[]).forEach((insideProperty) => {
              if (insideProperty !== null && typeof insideProperty === "object") {
                Object.keys(insideProperty).map((insidePropertyKey) => {
                  const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insidePropertyKey);
                  if (columnFound) {
                    insideProperty[insidePropertyKey] = {
                      value: insideProperty[insidePropertyKey],
                      width: columnFound.width,
                    };
                  }
                });
              } else if (insideProperty) {
                const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insideProperty.name);
                if (columnFound) {
                  insideProperty.width = columnFound.width;
                }
              }
            });
          }
          if (outputToUpdate) {
            outputToUpdate.width = column.width;
          }
        }
      });
    },
    [inputColumnsCache, outputColumnsCache, inputs]
  );

  const onModelUpdate = useCallback(
    (model: object, index) => {
      setInputRows?.((previousData) => {
        const newData = [...previousData];
        newData[index] = model;
        return newData;
      });
    },
    [setInputRows]
  );

  const inputRules: Partial<DmnRunnerRule>[] = useMemo(() => {
    if (jsonSchemaBridge === undefined || !formsDivRendered) {
      return [] as Partial<DmnRunnerRule>[];
    }
    const inputEntriesLength = inputs.reduce(
      (length, input) => (isInputWithInsideProperties(input) ? length + input.insideProperties.length : length + 1),
      0
    );
    const inputEntries = Array.from(Array(inputEntriesLength));
    return Array.from(Array(rowCount)).map((e, rowIndex) => {
      return {
        inputEntries,
        rowDelegate: ({ children }: PropsWithChildren<any>) => {
          const dmnAutoRowRef = React.createRef<DmnAutoRowApi>();
          rowsRef.set(rowIndex, dmnAutoRowRef);
          return (
            <DmnAutoRow
              ref={dmnAutoRowRef}
              formId={FORMS_ID}
              rowIndex={rowIndex}
              model={defaultModel.current[rowIndex]}
              jsonSchemaBridge={jsonSchemaBridge}
              onModelUpdate={(model) => onModelUpdate(model, rowIndex)}
            >
              {children}
            </DmnAutoRow>
          );
        },
      } as Partial<DmnRunnerRule>;
    });
  }, [jsonSchemaBridge, formsDivRendered, inputs, rowCount, rowsRef, defaultModel, onModelUpdate]);

  const deepFlattenOutput = useCallback((acc: any, entry: string, value: object) => {
    return Object.entries(value).map(([deepEntry, deepValue]) => {
      if (typeof deepValue === "object" && deepValue !== null) {
        deepFlattenOutput(acc, deepEntry, deepValue);
      }
      acc[`${entry}-${deepEntry}`] = deepValue;
      return acc;
    });
  }, []);

  const deepGenerateOutputTypesMapFields = useCallback(
    (
      outputTypeMap: Map<string, OutputTypesFields>,
      properties: DmnSchemaProperties[],
      jsonSchemaBridge: DmnTableJsonSchemaBridge
    ) => {
      return Object.entries(properties).map(([name, property]: [string, DmnSchemaProperties]) => {
        if (property["x-dmn-type"]) {
          const dataType = getDataTypeProps(property["x-dmn-type"]).dataType;
          outputTypeMap.set(name, { type: property.type, dataType, name });
          return { name, type: property.type, width: 150, dataType };
        }
        const path: string[] = property.$ref.split("/").slice(1); // remove #
        const data = path.reduce(
          (acc: { [x: string]: object }, property: string) => acc[property],
          jsonSchemaBridge.schema
        );
        const dataType = getDataTypeProps(data["x-dmn-type"]).dataType;
        if (data.properties) {
          const insideProperties = deepGenerateOutputTypesMapFields(outputTypeMap, data.properties, jsonSchemaBridge);
          outputTypeMap.set(name, { type: data.type, insideProperties, dataType, name });
        } else {
          outputTypeMap.set(name, { type: data.type, dataType, name });
        }
        return { name, dataType: data.type, width: 150 } as OutputTypesField;
      });
    },
    [getDataTypeProps]
  );

  const { outputs, outputRules } = useMemo(() => {
    const decisionResults = results?.filter((result) => result !== undefined);
    if (jsonSchemaBridge === undefined || decisionResults === undefined) {
      return { outputs: [] as OutputFields[], outputRules: [] as Partial<DmnRunnerRule>[] };
    }

    // generate a map that contains output types
    const outputTypeMap = Object.entries(
      (jsonSchemaBridge as any).schema?.definitions?.OutputSet?.properties ?? []
    ).reduce((outputTypeMap: Map<string, OutputTypesFields>, [name, properties]: [string, DmnSchemaProperties]) => {
      if (properties["x-dmn-type"]) {
        const dataType = getDataTypeProps(properties["x-dmn-type"]).dataType;
        outputTypeMap.set(name, { type: properties.type, dataType, name });
      } else {
        const path = properties.$ref.split("/").slice(1); // remove #
        const data = path.reduce((acc: any, property: string) => acc[property], (jsonSchemaBridge as any).schema);
        const dataType = getDataTypeProps(data["x-dmn-type"]).dataType;
        if (data.properties) {
          const insideProperties = deepGenerateOutputTypesMapFields(outputTypeMap, data.properties, jsonSchemaBridge);
          outputTypeMap.set(name, { type: data.type, insideProperties, dataType, name });
        } else {
          outputTypeMap.set(name, { type: data.type, dataType, name });
        }
      }

      return outputTypeMap;
    }, new Map<string, OutputFields>());

    // generate outputs
    const outputMap = decisionResults.reduce(
      (acc: Map<string, OutputFields>, decisionResult: DecisionResult[] | undefined) => {
        if (decisionResult) {
          decisionResult.forEach(({ decisionName }) => {
            const data = outputTypeMap.get(decisionName);
            const dataType = data?.dataType ?? DataType.Undefined;
            if (data && isOutputWithInsideProperties(data)) {
              acc.set(decisionName, {
                name: decisionName,
                dataType,
                insideProperties: data.insideProperties,
                width: data.insideProperties.reduce((acc: number, column: any) => acc + column.width, 0),
              });
            } else {
              acc.set(decisionName, {
                name: decisionName,
                dataType,
                width: 150,
              });
            }
          });
        }
        return acc;
      },
      new Map<string, OutputFields>()
    );

    const outputEntries = decisionResults.reduce((acc: Result[], decisionResult: DecisionResult[] | undefined) => {
      if (decisionResult) {
        const outputResults = decisionResult.map(({ result, decisionName }) => {
          if (result === null || typeof result !== "object") {
            const dmnRunnerClause = outputMap.get(decisionName);
            if (dmnRunnerClause && isOutputWithInsideProperties(dmnRunnerClause)) {
              return dmnRunnerClause.insideProperties.reduce((acc, insideProperty) => {
                acc[insideProperty.name] = "null";
                return acc;
              }, {} as { [x: string]: any });
            }
          }
          if (result === null) {
            return "null";
          }
          if (result === true) {
            return "true";
          }
          if (result === false) {
            return "false";
          }
          if (typeof result === "object") {
            return Object.entries(result).reduce((acc: any, [entry, value]) => {
              if (typeof value === "object" && value !== null) {
                deepFlattenOutput(acc, entry, value);
              } else {
                acc[entry] = value;
              }
              return acc;
            }, {});
          }
          return result;
        });
        return [...acc, outputResults];
      }
      return acc;
    }, []);

    const outputRules: Partial<DmnRunnerRule>[] = Array.from(Array(rowCount)).map((e, i) => ({
      outputEntries: (outputEntries?.[i] as string[]) ?? [],
    }));

    const outputs = Array.from(outputMap.values());
    updateWidth(outputs);
    return { outputs, outputRules };
  }, [
    deepFlattenOutput,
    deepGenerateOutputTypesMapFields,
    getDataTypeProps,
    jsonSchemaBridge,
    results,
    rowCount,
    updateWidth,
  ]);

  return { jsonSchemaBridge, inputs, inputRules, outputs, outputRules, updateWidth };
}
