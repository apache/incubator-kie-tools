import { joinName } from "uniforms";
import * as React from "react";
import { AutoField } from "./AutoField";
import { DataType } from "@kogito-tooling/boxed-expression-component/dist/api";
import { DmnRunnerClause } from "../boxed";
import { DecisionResult, Result } from "../dmn";
import { ColumnInstance } from "react-table";
import { DmnTableJsonSchemaBridge } from "../dmn/DmnTableJsonSchemaBridge";

export class Grid {
  private input: DmnRunnerClause[];
  private columns: ColumnInstance[];

  constructor(private bridge: DmnTableJsonSchemaBridge) {
    this.input = this.generateBoxedInputs();
  }

  public updateBridge(bridge: DmnTableJsonSchemaBridge) {
    this.bridge = bridge;
    this.input = this.generateBoxedInputs();
    this.columns?.map((column) => {
      if (column.groupType === "input") {
        const inputToUpdate = this.input.find((e) => e.name === column.label);
        if (inputToUpdate?.insideProperties && column?.columns) {
          inputToUpdate.insideProperties.forEach((insideProperty) => {
            const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insideProperty.name);
            if (columnFound) {
              insideProperty.width = columnFound.width;
            }
          });
        }
        if (inputToUpdate) {
          inputToUpdate.width = column.width;
        }
      }
    });
  }

  public getBridge() {
    return this.bridge;
  }

  public getInput() {
    return this.input;
  }

  public setPreviousColumns(columns: ColumnInstance[]) {
    this.columns = columns;
  }

  public resetColumns() {
    this.columns = [];
  }

  public updateWidth(output: any[], rules: any) {
    this.columns?.forEach((column) => {
      if (column.groupType === "input") {
        const inputToUpdate = this.input.find((i) => i.name === column.label);
        if (inputToUpdate?.insideProperties && column?.columns) {
          inputToUpdate.insideProperties.forEach((insideProperty) => {
            const columnFound = column.columns?.find((nestedColumn) => nestedColumn.label === insideProperty.name);
            if (columnFound) {
              insideProperty.width = columnFound.width;
            }
          });
        }
        if (inputToUpdate) {
          inputToUpdate.width = column.width;
        }
      }
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
  }

  public removeInputName(fullName: string) {
    return fullName.match(/\./) ? fullName.split(".").slice(1).join("-") : fullName;
  }

  public getDataTypeProps(type: string | undefined) {
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
        return { dataType: DataType.DateTime, width: 282 };
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
  }

  public deepGenerateBoxed(fieldName: any, parentName = ""): any {
    const joinedName = joinName(parentName, fieldName);
    const field = this.bridge.getField(joinedName);

    if (field.type === "object") {
      const insideProperties = this.bridge.getSubfields(joinedName).reduce((acc: any[], subField: string) => {
        const field = this.deepGenerateBoxed(subField, joinedName);
        if (field.insideProperties) {
          return [...acc, ...field.insideProperties];
        }
        return [...acc, field];
      }, []);
      return {
        ...this.getDataTypeProps(field["x-dmn-type"]),
        insideProperties,
        name: joinedName,
        width: insideProperties.reduce((acc, insideProperty) => acc + insideProperty.width, 0),
      };
    }
    return {
      ...this.getDataTypeProps(field["x-dmn-type"]),
      name: this.removeInputName(joinedName),
      cellDelegate: (formId: any) => <AutoField key={joinedName} name={joinedName} form={formId} />,
    };
  }

  public generateBoxedInputs(): DmnRunnerClause[] {
    let myGrid: DmnRunnerClause[] = [];
    const subfields = this.bridge.getSubfields();
    const inputs = subfields.reduce(
      (acc: DmnRunnerClause[], fieldName: string) => [...acc, this.deepGenerateBoxed(fieldName)],
      [] as DmnRunnerClause[]
    );
    if (inputs.length > 0) {
      myGrid = inputs;
    }
    return myGrid;
  }

  private deepFlattenOutput(acc: any, entry: string, value: object) {
    return Object.entries(value).map(([deepEntry, deepValue]) => {
      if (typeof deepValue === "object" && deepValue !== null) {
        this.deepFlattenOutput(acc, deepEntry, deepValue);
      }
      acc[`${entry}-${deepEntry}`] = deepValue;
      return acc;
    });
  }

  public generateBoxedOutputs(
    decisionResults: Array<DecisionResult[] | undefined>
  ): [Map<string, DmnRunnerClause>, Result[]] {
    const outputTypeMap = Object.entries((this.bridge as any).schema?.definitions?.OutputSet?.properties ?? []).reduce(
      (acc: Map<string, DataType>, [name, properties]: [string, any]) => {
        if (properties["x-dmn-type"]) {
          acc.set(name, this.getDataTypeProps(properties["x-dmn-type"]).dataType);
        } else {
          const path = properties.$ref.split("/").slice(1); // remove #
          const type = path.reduce((acc: any, property: string) => acc[property], (this.bridge as any).schema);
          acc.set(name, this.getDataTypeProps(type["x-dmn-type"]).dataType);
        }

        return acc;
      },
      new Map<string, DataType>()
    );

    const outputSet = decisionResults.reduce(
      (acc: Map<string, DmnRunnerClause>, decisionResult: DecisionResult[] | undefined) => {
        if (decisionResult) {
          decisionResult.forEach(({ decisionName }) => {
            acc.set(decisionName, {
              name: decisionName,
              dataType: outputTypeMap.get(decisionName) ?? DataType.Undefined,
            });
          });
        }
        return acc;
      },
      new Map<string, DmnRunnerClause>()
    );

    const outputEntries = decisionResults.reduce((acc: Result[], decisionResult: DecisionResult[] | undefined) => {
      if (decisionResult) {
        const outputResults = decisionResult.map(({ result }) => {
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
                this.deepFlattenOutput(acc, entry, value);
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

    return [outputSet, outputEntries];
  }
}
