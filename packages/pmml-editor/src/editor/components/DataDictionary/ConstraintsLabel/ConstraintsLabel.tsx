import * as React from "react";
import { useMemo } from "react";
import { Label } from "@patternfly/react-core";
import { every } from "lodash";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import { useValidationService } from "../../../validation";
import "./ConstraintsLabel.scss";

interface ConstraintsLabelProps {
  dataType: DDDataField;
  editingIndex?: number;
  onConstraintsDelete?: () => void;
}

const ConstraintsLabel = (props: ConstraintsLabelProps) => {
  const { dataType, editingIndex, onConstraintsDelete } = props;

  const onClose = useMemo(() => {
    if (editingIndex !== undefined && !areConstraintsRequired(dataType)) {
      return (event: React.MouseEvent) => {
        event.nativeEvent.stopImmediatePropagation();
        onConstraintsDelete?.();
      };
    }
  }, [editingIndex, dataType]);

  const missingRequiredConstraints = useMemo(() => {
    return !dataType.constraints && areConstraintsRequired(dataType);
  }, [dataType]);

  const constraintValue = useMemo(() => {
    if (dataType.constraints) {
      switch (dataType.constraints.type) {
        case "Range":
          return dataType.constraints.value
            .map(range => {
              return (
                `${range.start.included ? "[" : "("}` +
                `${range.start.value || `${String.fromCharCode(8722, 8734)}`}, ` +
                `${range.end.value || `${String.fromCharCode(43, 8734)}`}` +
                `${range.end.included ? "]" : ")"}`
              );
            })
            .join(" ");

        case "Enumeration":
          if (every(dataType.constraints.value, value => value === "")) {
            return <em>No values</em>;
          }
          return dataType.constraints.value.map(item => `"${item}"`).join(", ");
        default:
          return "";
      }
    }
    return "";
  }, [dataType.constraints]);

  const { service } = useValidationService();
  const validations = useMemo(() => service.get(`DataDictionary.DataField[${editingIndex}]`), [editingIndex, dataType]);

  return (
    <>
      {missingRequiredConstraints && (
        <ValidationIndicatorLabel
          children={<em>Missing required constraints</em>}
          validations={validations}
          cssClass="constraints-label"
        />
      )}
      {!missingRequiredConstraints && dataType.constraints && (
        <>
          {validations.length > 0 && (
            <ValidationIndicatorLabel
              validations={validations}
              children={
                <>
                  <strong>Constraints:</strong>&nbsp;
                  <span>{constraintValue}</span>
                </>
              }
              onClose={onClose}
              cssClass="constraints-label"
            />
          )}
          {validations.length === 0 && (
            <Label color="cyan" className="constraints-label" onClose={onClose}>
              <strong>Constraints:</strong>&nbsp;
              <span>{constraintValue}</span>
            </Label>
          )}
        </>
      )}
    </>
  );
};

export default ConstraintsLabel;

const areConstraintsRequired = (dataType: DDDataField) => {
  return dataType.isCyclic || (dataType.type === "string" && dataType.optype === "ordinal");
};
