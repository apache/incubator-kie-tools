import React, { HTMLProps, useContext } from "react";
import { connectField, context } from "uniforms";
import { renderNestedInputFragmentWithContext } from "./rendering/RenderingUtils";
import { renderField } from "./utils/Utils";
import { FormInput, InputReference, InputsContainer } from "../api";
import { useAddFormElementToContext } from "./CodeGenContext";
import { union } from "lodash";

export type NestFieldProps = {
  error?: boolean;
  errorMessage?: string;
  fields?: any[];
  itemProps?: any;
  showInlineError?: boolean;
  disabled?: boolean;
  name: string;
  onChange: () => void;
} & HTMLProps<HTMLDivElement>;

const Nest: React.FunctionComponent<NestFieldProps> = ({
  id,
  children,
  error,
  errorMessage,
  fields,
  itemProps,
  label,
  name,
  showInlineError,
  disabled,
  ...props
}: NestFieldProps) => {
  const uniformsContext = useContext(context);

  const nestedRefs: InputReference[] = [];
  const nestedStates: string[] = [];
  const nestedJsx: string[] = [];

  let pfImports: string[] = ["Card", "CardBody"];
  let reactImports: string[] = [];
  let requiredCode: string[] = [];

  if (fields) {
    fields.forEach((field) => {
      const renderedInput: FormInput = renderNestedInputFragmentWithContext(
        uniformsContext,
        field,
        itemProps,
        disabled
      );

      if (renderedInput) {
        nestedStates.push(renderedInput.stateCode);
        nestedJsx.push(renderedInput.jsxCode);
        nestedRefs.push(renderedInput.ref);
        pfImports = union(pfImports, renderedInput.pfImports);
        reactImports = union(reactImports, renderedInput.reactImports);
        if (renderedInput.requiredCode) {
          requiredCode = union(requiredCode, renderedInput.requiredCode);
        }
      } else {
        console.log(`Cannnot render form field for: '${field}'`);
      }
    });
  }

  const bodyLabel = label ? `<label><b>${label}</b></label>` : "";

  const stateCode = nestedStates.join("\n");
  const jsxCode = `<Card>
          <CardBody className="pf-c-form">
          ${bodyLabel}
          ${nestedJsx.join("\n")}
          </CardBody></Card>`;

  const rendered: InputsContainer = {
    pfImports,
    reactImports,
    requiredCode: requiredCode,
    stateCode,
    jsxCode,
    ref: nestedRefs,
  };

  useAddFormElementToContext(rendered);

  return renderField(rendered);
};

export default connectField(Nest);
