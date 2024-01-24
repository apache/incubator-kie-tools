/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InlineFeelNameInput } from "../../feel/InlineFeelNameInput";
import { UniqueNameIndex } from "../../Dmn15Spec";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TypeRefSelector } from "../../dataTypes/TypeRefSelector";
import { FeelInput } from "@kie-tools/feel-input-component";

export function KieConstraintTypeField() {
  return (
    <FormGroup label="Constraint">
      <div>TODO</div>
    </FormGroup>
  );
}

export function ContentField(props: {
  initialValue: string;
  onChange?: (newTextValue: string, expressionPath?: ExpressionPath[]) => void;
  expressionPath?: ExpressionPath[];
  isReadonly: boolean;
}) {
  return <TextInputField {...props} title="Content" placeholder="Enter the content..." />;
}

// export function ContentField({
//   onChange,
//   ...props
// }: {
//   initialValue: string;
//   onChange: (newTextValue: string) => void;
//   expressionPath: ExpressionPath[];
//   isReadonly: boolean;
// }) {
//   const [preview, setPreview] = useState(props.initialValue ?? "");
//   const [editingValue, setEditingValue] = useState(props.initialValue);
//   const onFeelChange = useCallback((_, content, preview) => {
//     setEditingValue?.(content.trim());
//     setPreview(preview);
//   }, []);

//   const onFeelBlur = useCallback(
//     (value: string) => {
//       onChange?.(value.trim());
//     },
//     [onChange]
//   );

//   const monacoOptions = useMemo(
//     () => ({
//       fixedOverflowWidgets: true,
//       lineNumbers: "off",
//       fontSize: 16,
//       renderLineHighlight: "none",
//       lineDecorationsWidth: 1,
//       automaticLayout: true,
//       "semanticHighlighting.enabled": true,
//     }),
//     []
//   );

//   return (
//     <FormGroup label={"Content"}>
//       {props.isReadonly &&
//         (props.initialValue ? (
//           <span className="editable-cell-value pf-u-text-break-word" dangerouslySetInnerHTML={{ __html: preview }} />
//         ) : (
//           <p style={{ fontStyle: "italic" }}>{`<None>`}</p>
//         ))}
//       <FeelInput
//         value={props.isReadonly ? props.initialValue : editingValue}
//         onChange={onFeelChange}
//         onBlur={onFeelBlur}
//         onPreviewChanged={setPreview}
//         enabled={!props.isReadonly}
//         options={monacoOptions as any}
//       />
//     </FormGroup>
//   );
// }

export function DescriptionField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
}) {
  return <TextAreaField {...props} title="Description" placeholder="Enter a description..." />;
}

export function ExpressionLanguageField(props: {
  initialValue: string;
  onChange?: (newTextValue: string, expressionPath?: ExpressionPath[]) => void;
  expressionPath?: ExpressionPath[];
  isReadonly: boolean;
}) {
  return <TextInputField {...props} title="Expression Language" placeholder="Enter the expression language..." />;
}

export function NameField(props: {
  id: string;
  name: string;
  isReadonly: boolean;
  allUniqueNames: UniqueNameIndex;
  onChange: (newName: string) => void;
}) {
  return (
    <FormGroup label="Name">
      <InlineFeelNameInput
        enableAutoFocusing={false}
        isPlain={false}
        id={props.id}
        name={props.name}
        isReadonly={props.isReadonly}
        shouldCommitOnBlur={true}
        className={"pf-c-form-control"}
        onRenamed={props.onChange}
        allUniqueNames={props.allUniqueNames}
      />
    </FormGroup>
  );
}

export function TypeRefField(props: {
  typeRef: string;
  isReadonly: boolean;
  dmnEditorRootElementRef: React.RefObject<HTMLElement>;
  onChange?: (newTypeRef: string) => void;
}) {
  return (
    <FormGroup label="Type">
      <TypeRefSelector
        heightRef={props.dmnEditorRootElementRef}
        typeRef={props.typeRef}
        isDisabled={props.isReadonly}
        onChange={(newValue: string) => props.onChange?.(newValue)}
      />
    </FormGroup>
  );
}

export function TextInputField(props: {
  initialValue: string;
  onChange?: (newTextValue: string, expressionPath?: ExpressionPath[]) => void;
  expressionPath?: ExpressionPath[];
  isReadonly: boolean;
  title: string;
  placeholder?: string;
}) {
  // used to save the expression path value until the flush operation
  const [expressionPath, setExpressionPath] = useState(props.expressionPath);
  const [textInputValue, setTextInputValue] = useState("");
  const [isEditing, setEditing] = useState(false);

  useEffect(() => {
    if (!isEditing) {
      setTextInputValue(props.initialValue);
    }
  }, [props.initialValue, isEditing]);

  useEffect(() => {
    if (!isEditing) {
      setExpressionPath(props.expressionPath);
    }
  }, [props.expressionPath, isEditing]);

  return (
    <FormGroup label={props.title}>
      <TextInput
        aria-label={"Content"}
        type={"text"}
        isDisabled={props.isReadonly}
        value={textInputValue}
        onChange={(newContent) => {
          setTextInputValue(newContent);
          setEditing(true);
        }}
        onBlur={() => {
          props.onChange?.(textInputValue, expressionPath);
          setEditing(false);
        }}
        placeholder={props.placeholder ?? "Enter the expression content..."}
        style={{ resize: "vertical", minHeight: "40px" }}
        rows={6}
      />
    </FormGroup>
  );
}

export function TextAreaField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
  title: string;
  placeholder?: string;
}) {
  // used to save the expression path value until the flush operation
  const [expressionPath, setExpressionPath] = useState(props.expressionPath);
  const [textAreaValue, setTextAreaValue] = useState("");
  const [isEditing, setEditing] = useState(false);

  useEffect(() => {
    if (!isEditing) {
      setTextAreaValue(props.initialValue);
    }
  }, [props.initialValue, isEditing]);

  useEffect(() => {
    if (!isEditing) {
      setExpressionPath(props.expressionPath);
    }
  }, [props.expressionPath, isEditing]);

  return (
    <FormGroup label={props.title}>
      <TextArea
        aria-label={"Content"}
        type={"text"}
        isDisabled={props.isReadonly}
        value={textAreaValue}
        onChange={(newContent) => {
          setTextAreaValue(newContent);
          setEditing(true);
        }}
        onBlur={() => {
          props.onChange(textAreaValue, expressionPath);
          setEditing(false);
        }}
        placeholder={props.placeholder ?? "Enter the expression content..."}
        style={{ resize: "vertical", minHeight: "40px" }}
        rows={6}
      />
    </FormGroup>
  );
}
