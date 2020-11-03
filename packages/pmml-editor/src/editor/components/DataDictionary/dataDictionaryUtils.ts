import {
  DataDictionary,
  DataField as PMMLDataField,
  DataType,
  FieldName
} from "@kogito-tooling/pmml-editor-marshaller";
import { DataField } from "./DataDictionaryContainer/DataDictionaryContainer";

export const convertPMML2DD = (PMMLDataDictionary: DataDictionary | undefined): DataField[] => {
  if (PMMLDataDictionary === undefined) {
    return [];
  } else {
    return PMMLDataDictionary.DataField.filter(item => item.dataType !== undefined).map(item => {
      let type: DataField["type"];

      switch (item.dataType) {
        case "integer":
        case "float":
        case "double":
          type = "number";
          break;
        case "string":
          type = "string";
          break;
        case "boolean":
          type = "boolean";
          break;
        default:
          type = "string";
          break;
      }

      return {
        name: item.name as string,
        type: type,
        list: false
      };
    });
  }
};

export const convertDD2PMML = (dataDictionary: DataField[]): PMMLDataField[] => {
  return dataDictionary.map(item => {
    const dataField: PMMLDataField = {
      name: item.name as FieldName,
      dataType: item.type === "number" ? "double" : (item.type as DataType),
      optype: item.type === "number" ? "continuous" : "categorical",
      Interval: [],
      Value: []
    };

    return dataField;
  });
};
