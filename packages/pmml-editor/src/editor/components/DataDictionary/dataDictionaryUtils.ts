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

  convertedField.displayName = item.displayName;
  if (item.isCyclic !== undefined) {
    convertedField.isCyclic = item.isCyclic ? "1" : "0";
  }
  if (item.missingValue) {
    convertedField.Value = convertedField.Value || [];
    convertedField.Value.push({
      property: "missing",
      value: item.missingValue
    });
  }
  if (item.invalidValue) {
    convertedField.Value = convertedField.Value || [];
    convertedField.Value.push({
      property: "invalid",
      value: item.invalidValue
    });
  }

  if (item.constraints) {
    if (item.constraints.type === "Range" && item.constraints.value.length > 0) {
      convertedField.Interval = item.constraints.value.map(range => {
        const interval: Interval = {
          closure: `${range?.start?.included ? "closed" : "open"}${range?.end?.included ? "Closed" : "Open"}` as Closure
        };
        if (range.start && range.start.value) {
          interval.leftMargin = Number(range.start.value);
        }
        if (range.end && range.end.value) {
          interval.rightMargin = Number(range.end.value);
        }
        return interval;
      });
    }
    if (item.constraints.type === "Enumeration" && item.constraints.value.length > 0) {
      convertedField.Value = (convertedField.Value || []).concat(
        item.constraints.value.map(value => {
          return { value };
        })
      );
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
    convertedField.displayName = item.displayName;
  }
  if (item.isCyclic !== undefined) {
    convertedField.isCyclic = item.isCyclic === "1";
  }
  if (item.Value) {
    item.Value.forEach(value => {
      if (value.property === "missing") {
        convertedField.missingValue = value.value;
      }
      if (value.property === "invalid") {
        convertedField.invalidValue = value.value;
      }
      // valid values correspond to the enumeration constraint
      if (value.property === "valid" || value.property === undefined) {
        convertedField.constraints = convertedField.constraints || {
          type: "Enumeration",
          value: []
        };
        convertedField.constraints.value.push(value.value);
      }
    });
  }
  if (item.Interval && item.Interval.length > 0) {
    convertedField.constraints = {
      type: "Range",
      value: item.Interval.map(interval => {
        /* A note about the included value and how it's calculated.
        PMML presents a single property to handle the inclusion of both interval limits called Closure.
        Closure combines both inclusion values in camel case, i.e. "openClosed", meaning that the left margin is open
        and the right margin is closed. To convert it to DD structure where the info is stored separately for start
        and end values, I check if closure value starts with or ends with "closed" or "Closed" respectively.
        */
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
