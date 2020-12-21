import {
  DataDictionary,
  DataField as PMMLDataField,
  DataType,
  FieldName
} from "@redhat/pmml-editor-marshaller";
import { DataField } from "./DataDictionaryContainer/DataDictionaryContainer";

export const convertPMML2DD = (PMMLDataDictionary: DataDictionary | undefined): DataField[] => {
  if (PMMLDataDictionary === undefined) {
    return [];
  } else {
    return PMMLDataDictionary.DataField.filter(item => item.dataType !== undefined).map(item => {
      let type: DataField["type"];
      // supporting a few types only for now
      if (
        item.dataType === "string" ||
        item.dataType === "integer" ||
        item.dataType === "float" ||
        item.dataType === "double" ||
        item.dataType === "boolean"
      ) {
        type = item.dataType;
      } else {
        type = "string";
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
      dataType: item.type as DataType,
      optype: item.type === "integer" || item.type === "float" || item.type === "double" ? "continuous" : "categorical",
      Interval: [],
      Value: []
    };

    return dataField;
  });
};
