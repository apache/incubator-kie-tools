import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import "./ConstraintsExpression.css";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import InfoIcon from "@patternfly/react-icons/dist/js/icons/info-icon";

export function ConstraintsExpression({
  isReadonly,
  value,
  onChange,
}: {
  isReadonly: boolean;
  value?: string;
  onChange?: (newValue: string | undefined) => void;
}) {
  const [preview, setPreview] = useState(value ?? "");
  const onFeelChange = useCallback(
    (content, preview) => {
      onChange?.(content);
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
          onChange={(_, value, preview) => onFeelChange(value, preview)}
          onPreviewChanged={setPreview}
          enabled={!isReadonly}
          options={monacoOptions as any}
        />
      </div>
      <HelperText>
        <HelperTextItem variant="indeterminate" icon={<InfoIcon />}>
          Check the{" "}
          <a target={"_blank"} href={"https://kiegroup.github.io/dmn-feel-handbook/#feel-values"}>
            FEEL handbook
          </a>{" "}
          to help you on creating your expressions
        </HelperTextItem>
      </HelperText>
    </div>
  );
}
