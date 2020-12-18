import { Closure, DataDictionary, DataField, FieldName, Interval } from "@kogito-tooling/pmml-editor-marshaller";
import { DDDataField, RangeConstraint } from "./DataDictionaryContainer/DataDictionaryContainer";
import convert = require("lodash/fp/convert");

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
  if (item.optionalProperties) {
    convertedField.displayName = item.optionalProperties.displayName;
    if (item.optionalProperties.isCyclic !== undefined) {
      convertedField.isCyclic = item.optionalProperties.isCyclic ? "1" : "0";
    }
    if (item.optionalProperties.missingValue) {
      convertedField.Value = convertedField.Value || [];
      convertedField.Value.push({
        property: "missing",
        value: item.optionalProperties.missingValue
      });
    }
    if (item.optionalProperties.invalidValue) {
      convertedField.Value = convertedField.Value || [];
      convertedField.Value.push({
        property: "invalid",
        value: item.optionalProperties.invalidValue
      });
    }
  }
  if (item.constraints) {
    if (item.constraints.type === "Range" && item.constraints.value.length > 0) {
      convertedField.Interval = item.constraints.value.map(range => {
        const interval: Interval = {
          closure: `${range?.start?.included ? "closed" : "open"}${range?.end?.included ? "Closed" : "Open"}` as Closure
        };
        if (range.start) {
          interval.leftMargin = Number(range.start.value);
        }
        if (range.end) {
          interval.rightMargin = Number(range.end.value);
        }
        return interval;
      });
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
  if (item.displayName) {
    convertedField.optionalProperties = convertedField.optionalProperties || {};
    convertedField.optionalProperties.displayName = item.displayName;
  }
  if (item.isCyclic !== undefined) {
    convertedField.optionalProperties = convertedField.optionalProperties || {};
    convertedField.optionalProperties.isCyclic = item.isCyclic === "1";
  }
  if (item.Value) {
    item.Value.forEach(value => {
      if (value.property === "missing") {
        convertedField.optionalProperties = convertedField.optionalProperties || {};
        convertedField.optionalProperties.missingValue = value.value;
      }
      if (value.property === "invalid") {
        convertedField.optionalProperties = convertedField.optionalProperties || {};
        convertedField.optionalProperties.invalidValue = value.value;
      }
    });
  }
  if (item.Interval && item.Interval.length > 0) {
    convertedField.constraints = {
      type: "Range",
      value: item.Interval.map(interval => {
        return {
          start: {
            value: interval.leftMargin?.toString() ?? "",
            included: interval.closure.startsWith("closed")
          },
          end: {
            value: interval.rightMargin?.toString() ?? "",
            included: interval.closure.endsWith("Closed")
          }
        };
      })
    };
  }
  return convertedField;
};
