import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import "./ConstraintsExpression.css";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";

export function ConstraintsExpression({
  isReadonly,
  value,
  onChange,
}: {
  isReadonly: boolean;
  value?: string;
  onChange?: (newValue: string | undefined, origin: KIE__tConstraintType) => void;
}) {
  const onFeelChange = useCallback(
    (content) => {
      onChange?.(content, "expression");
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
    <div style={{ display: "flex", flexDirection: "column", height: "100%", width: "100%", gap: "10px" }}>
      <Title size={"md"} headingLevel="h5">
        {isReadonly ? "Equivalent expression:" : "Expression:"}
      </Title>
      <div style={{ flexGrow: 1 }}>
        <TextInput value={value} onChange={onFeelChange} isDisabled={isReadonly} />
      </div>
    </div>
  );
}
