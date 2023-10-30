import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";

export function ConstraintsEnum(props: {
  inputType: "text" | "number";
  value?: string;
  onChange: (newValue: string | undefined, origin: KIE__tConstraintType) => void;
  isDisabled: boolean;
}) {
  const onChange = useCallback(() => {}, []);

  const enumValues = useMemo(() => props.value?.split(","), [props.value]);

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
      {enumValues?.map((value, i) => (
        <TextInput
          key={i}
          type={props.inputType}
          value={value.trim()}
          onChange={onChange}
          isDisabled={props.isDisabled}
        />
      ))}
    </div>
  );
}
