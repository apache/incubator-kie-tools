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
import { InlineFeelNameInput } from "../../feel/InlineFeelNameInput";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { ExpressionPath } from "../../boxedExpressions/boxedExpressionIndex";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TypeRefSelector } from "../../dataTypes/TypeRefSelector";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { State } from "../../store/Store";

export function ContentField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
}) {
  return <TextAreaField {...props} title="Content" placeholder="Enter the content..." />;
}

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
  alternativeFieldName?: string;
  id: string;
  name: string;
  isReadonly: boolean;
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
        isReadonly={props.isReadonly}
        shouldCommitOnBlur={true}
        className={"pf-c-form-control"}
        onRenamed={(newName) => props.onChange?.(newName)}
        allUniqueNames={props.getAllUniqueNames}
      />
    </FormGroup>
  );
}

export function TypeRefField(props: {
  title?: string;
  typeRef: string;
  isReadonly: boolean;
  dmnEditorRootElementRef: React.RefObject<HTMLElement>;
  onChange?: (newTypeRef: string) => void;
}) {
  return (
    <FormGroup label={props.title ?? "Type"}>
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
          if (props.initialValue === textInputValue) {
            return;
          }
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
          if (props.initialValue === textAreaValue) {
            return;
          }
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
