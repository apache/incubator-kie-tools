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
import { useEffect, useState } from "react";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { ExpressionPath } from "../boxedExpressions/boxedExpressionIndex";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { State } from "../store/Store";

export function ContentField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadOnly: boolean;
}) {
  return <TextField {...props} type={TextFieldType.TEXT_AREA} title="Content" placeholder="Enter the content..." />;
}

export function DescriptionField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadOnly: boolean;
}) {
  return (
    <TextField {...props} type={TextFieldType.TEXT_AREA} title="Description" placeholder="Enter a description..." />
  );
}

export function ExpressionLanguageField(props: {
  initialValue: string;
  onChange?: (newTextValue: string, expressionPath?: ExpressionPath[]) => void;
  expressionPath?: ExpressionPath[];
  isReadOnly: boolean;
}) {
  return (
    <TextField
      {...props}
      type={TextFieldType.TEXT_INPUT}
      title="Expression Language"
      placeholder="Enter the expression language..."
    />
  );
}

export function NameField(props: {
  alternativeFieldName?: string;
  id: string;
  name: string;
  isReadOnly: boolean;
  getAllUniqueNames: (s: State) => UniqueNameIndex;
  onChange?: (newName: string) => void;
}) {
  return (
    <FormGroup label={props.alternativeFieldName ? props.alternativeFieldName : "Name"}>
      <InlineFeelNameInput
        enableAutoFocusing={false}
        isPlain={false}
        id={props.id}
        name={props.name}
        isReadOnly={props.isReadOnly}
        shouldCommitOnBlur={true}
        className={"pf-c-form-control"}
        onRenamed={(newName) => props.onChange?.(newName)}
        allUniqueNames={props.getAllUniqueNames}
      />
    </FormGroup>
  );
}

export function TypeRefField(props: {
  alternativeFieldName?: string;
  typeRef?: string;
  isReadOnly: boolean;
  dmnEditorRootElementRef: React.RefObject<HTMLElement>;
  onChange?: (newTypeRef: string) => void;
}) {
  return (
    <FormGroup label={props.alternativeFieldName ? props.alternativeFieldName : "Type"}>
      <TypeRefSelector
        heightRef={props.dmnEditorRootElementRef}
        typeRef={props.typeRef}
        isDisabled={props.isReadOnly}
        onChange={(newValue: string) => props.onChange?.(newValue)}
      />
    </FormGroup>
  );
}

export enum TextFieldType {
  TEXT_AREA = "text-area",
  TEXT_INPUT = "text-input",
}

export function TextField({
  onChange,
  ...props
}: {
  initialValue: string;
  onChange?: (newTextValue: string, expressionPath?: ExpressionPath[]) => void;
  expressionPath?: ExpressionPath[];
  isReadOnly: boolean;
  title: string;
  placeholder?: string;
  type: TextFieldType;
}) {
  // used to save the expression path value until the flush operation
  const [expressionPath, setExpressionPath] = useState(props.expressionPath);
  const [value, setValue] = useState(props.initialValue);

  // Uses refs to prevent `useEffect` to run multiple times
  const valueRef = React.useRef(props.initialValue);
  const isEditing = React.useRef(false);

  // Updates the value and expression path with the props value
  useEffect(() => {
    if (isEditing.current === false) {
      setValue(props.initialValue);
      setExpressionPath(props.expressionPath);
      valueRef.current = props.initialValue;
    }
  }, [props.initialValue, props.expressionPath]);

  // Handle special case where the component is umounted and the onBlur is not called
  useEffect(() => {
    return () => {
      if (isEditing.current === true) {
        if (props.initialValue === valueRef.current) {
          return;
        }
        onChange?.(valueRef.current, expressionPath);
        isEditing.current = false;
      }
    };
  }, [expressionPath, onChange, props.initialValue]);

  return (
    <FormGroup label={props.title}>
      {props.type === TextFieldType.TEXT_AREA && (
        <TextArea
          aria-label={"Content"}
          type={"text"}
          isDisabled={props.isReadOnly}
          value={value}
          onChange={(newContent) => {
            setValue(newContent);
            valueRef.current = newContent;
            isEditing.current = true;
          }}
          onBlur={() => {
            if (props.initialValue === value) {
              return;
            }
            onChange?.(value, expressionPath);
            isEditing.current = false;
          }}
          placeholder={props.placeholder ?? "Enter the expression content..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      )}
      {props.type === TextFieldType.TEXT_INPUT && (
        <TextInput
          aria-label={"Content"}
          type={"text"}
          isDisabled={props.isReadOnly}
          value={value}
          onChange={(newContent) => {
            setValue(newContent);
            valueRef.current = newContent;
            isEditing.current = true;
          }}
          onBlur={() => {
            if (props.initialValue === value) {
              return;
            }
            onChange?.(value, expressionPath);
            isEditing.current = false;
          }}
          placeholder={props.placeholder ?? "Enter the expression content..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      )}
    </FormGroup>
  );
}
