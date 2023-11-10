import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import "./ConstraintsExpression.css";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import InfoIcon from "@patternfly/react-icons/dist/js/icons/info-icon";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

export function ConstraintsExpression({
  isReadonly,
  value,
  type,
  onChange,
}: {
  isReadonly: boolean;
  value?: string;
  type: DmnBuiltInDataType;
  onChange?: (args: { newValue?: string; isValid: boolean }) => void;
}) {
  const [preview, setPreview] = useState(value ?? "");
  const onFeelChange = useCallback(
    (_, content, preview) => {
      onChange?.({ newValue: content, isValid: true });
      setPreview(preview);
    },
    [onChange]
  );

  const monacoOptions = useMemo(
    () => ({
      fixedOverflowWidgets: true,
      lineNumbers: "off",
      fontSize: 16,
      renderLineHighlight: "none",
      lineDecorationsWidth: 1,
      automaticLayout: true,
      "semanticHighlighting.enabled": true,
    }),
    []
  );

  const informativeText = useMemo(() => {
    switch (type) {
      case DmnBuiltInDataType.Date:
        return `Example expression for a "date" data type: [date("2000-01-01")..date("2020-01-01"))`;
      case DmnBuiltInDataType.DateTime:
        return `Example expression for a "date and time" data type: [date and time("2000-01-01T00:00")..date and time("2020-01-01T23:59"))`;
      case DmnBuiltInDataType.DateTimeDuration:
        return `Example expression for a "date and time duration" data type: [duration("PT10S")..duration("PT30M"))`;
      case DmnBuiltInDataType.Number:
        return `Example expression for a "number" data type: [0..100)`;
      case DmnBuiltInDataType.String:
        return `Example expression for a "string" data type: "apple", "orange", "pineapple"`;
      case DmnBuiltInDataType.Time:
        return `Example expression for a "time" data type: [time("00:00")..time("23:59"))`;
      case DmnBuiltInDataType.YearsMonthsDuration:
        return `Example expression for a "years and months duration" data type: [duration("P10M")..duration("P2Y"))`;
      default:
        return `Enter a valid expression`;
    }
  }, [type]);

  return (
    <div style={{ display: "flex", flexDirection: "column", width: "100%", gap: "10px" }}>
      <Title size={"md"} headingLevel="h5">
        {isReadonly ? "Equivalent expression:" : "Expression:"}
      </Title>
      <div
        style={
          !isReadonly
            ? { flexGrow: 1, flexShrink: 0, border: "solid 1px lightgray", borderRadius: "4px" }
            : { flexGrow: 1, flexShrink: 0, height: "22px" }
        }
      >
        {isReadonly &&
          (value ? (
            <span className="editable-cell-value pf-u-text-break-word" dangerouslySetInnerHTML={{ __html: preview }} />
          ) : (
            <p style={{ fontStyle: "italic" }}>{`<None>`}</p>
          ))}
        <FeelInput
          value={value}
          onChange={onFeelChange}
          onPreviewChanged={setPreview}
          enabled={!isReadonly}
          options={monacoOptions as any}
        />
      </div>
      <HelperText>
        {!isReadonly && <HelperTextItem>{informativeText}</HelperTextItem>}
        <HelperTextItem variant="indeterminate" icon={<InfoIcon />}>
          Check the{" "}
          <a target={"_blank"} href={"https://kiegroup.github.io/dmn-feel-handbook/#feel-values"}>
            FEEL handbook
          </a>{" "}
          to help you on creating your expressions.
        </HelperTextItem>
      </HelperText>
    </div>
  );
}
