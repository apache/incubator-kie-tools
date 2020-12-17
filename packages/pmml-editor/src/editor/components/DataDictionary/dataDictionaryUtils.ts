import { Closure, DataDictionary, DataField, FieldName, Interval } from "@kogito-tooling/pmml-editor-marshaller";
import { DDDataField } from "./DataDictionaryContainer/DataDictionaryContainer";

export const convertPMML2DD = (PMMLDataDictionary: DataDictionary | undefined): DDDataField[] => {
  if (PMMLDataDictionary === undefined) {
    return [];
  } else {
    return PMMLDataDictionary.DataField.filter(item => item.dataType !== undefined).map(item => {
      return convertFromDataField(item);
    });
  }
};

export const convertToDataField = (item: DDDataField): DataField => {
  const convertedField: DataField = {
    name: item.name as FieldName,
    dataType: item.type,
    optype: item.optype
  };
  if (item.constraints) {
    if (item.constraints.type === "Range") {
      const range = item.constraints.value;
      const interval: Interval = {
        closure: `${range?.start?.included ? "closed" : "open"}${range?.end?.included ? "Closed" : "Open"}` as Closure
      };
      if (item.constraints.value.start) {
        interval.leftMargin = Number(item.constraints.value.start.value);
      }
      if (item.constraints.value.end) {
        interval.rightMargin = Number(item.constraints.value.end.value);
      }
      convertedField.Interval = [interval];
    }
  }

  return convertedField;
};

export const convertFromDataField = (item: DataField) => {
  let type: DDDataField["type"];
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
  const convertedField: DDDataField = {
    name: item.name as string,
    type: type,
    optype: item.optype
  };
  if (item.Interval && item.Interval.length > 0) {
    convertedField.constraints = {
      type: "Range",
      value: {
        start: {
          value: item.Interval[0].leftMargin?.toString() ?? "",
          included: item.Interval[0].closure.startsWith("closed")
        },
        end: {
          value: item.Interval[0].rightMargin?.toString() ?? "",
          included: item.Interval[0].closure.endsWith("Closed")
        }
      }
    };
  }
  return convertedField;
};
