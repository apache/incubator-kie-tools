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
import { UniqueNameIndex } from "../../Dmn15Spec";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TypeRefSelector } from "../../dataTypes/TypeRefSelector";

export function DescriptionField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
}) {
  return (
    <FormGroup label="Description">
      <TextAreaField {...props} />
    </FormGroup>
  );
}

export function ExpressionLanguageField(props: {
  expressionLanguage: string;
  onChange: (newLabel: string) => void;
  isReadonly: boolean;
}) {
  return (
    <FormGroup label="Expression Language">
      <TextInput value={props.expressionLanguage} onChange={props.onChange}></TextInput>
    </FormGroup>
  );
}

export function KieConstraintTypeField() {
  return (
    <FormGroup label="Constraint">
      <div>TODO</div>
    </FormGroup>
  );
}

export function LabelField(props: { label: string; onChange: (newLabel: string) => void; isReadonly: boolean }) {
  return (
    <FormGroup label="Label">
      <TextInput value={props.label} onChange={props.onChange}></TextInput>
    </FormGroup>
  );
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

export function TextField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
}) {
  return (
    <FormGroup label="Content">
      <TextAreaField {...props} />
    </FormGroup>
  );
}

export function TypeRefField(props: {
  typeRef: string;
  isReadonly: boolean;
  dmnEditorRootElementRef: React.RefObject<HTMLElement>;
  onChange: (newTypeRef: string) => void;
}) {
  return (
    <FormGroup label="Type">
      <TypeRefSelector
        heightRef={props.dmnEditorRootElementRef}
        typeRef={props.typeRef}
        isDisabled={props.isReadonly}
        onChange={props.onChange}
      />
    </FormGroup>
  );
}

function TextAreaField(props: {
  initialValue: string;
  onChange: (newTextValue: string, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
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
    <>
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
        }}
        placeholder={"Enter the expression content..."}
        style={{ resize: "vertical", minHeight: "40px" }}
        rows={6}
      />
    </>
  );
}
