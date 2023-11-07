import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import "./ConstraintsExpression.css";

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
      if (content !== "") {
        onChange?.(content);
        setPreview(preview);
      }
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
    <div style={{ display: "flex", flexDirection: "column", height: "60px", width: "100%", gap: "10px" }}>
      <Title size={"md"} headingLevel="h5">
        {isReadonly ? "Equivalent expression:" : "Expression:"}
      </Title>
      <div style={{ flexGrow: 1, flexShrink: 0 }}>
        {isReadonly && (
          <span className="editable-cell-value pf-u-text-break-word" dangerouslySetInnerHTML={{ __html: preview }} />
        )}
        <FeelInput
          value={value}
          onChange={(event, value, preview) => onFeelChange(value, preview)}
          onPreviewChanged={setPreview}
          enabled={!isReadonly}
          options={monacoOptions as any}
        />
      </div>
    </div>
  );
}
