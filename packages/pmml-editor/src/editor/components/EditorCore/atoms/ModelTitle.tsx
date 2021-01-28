/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useEffect, useState } from "react";
import { Form, FormGroup, TextContent, TextInput, Title, Tooltip } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import "./ModelTitle.scss";
import useOnclickOutside from "react-cool-onclickoutside";
import { ValidatedType } from "../../../types";
import { Operation, useOperation } from "../../EditorScorecard";

interface HeaderTitleProps {
  modelName: string;
  commitModelName?: (_modelName: string) => void;
}

export const ModelTitle = (props: HeaderTitleProps) => {
  const { commitModelName } = props;

  const [modelName, setModelName] = useState<ValidatedType<string>>({ value: "", valid: true });

  const { activeOperation, setActiveOperation } = useOperation();

  const ref = useOnclickOutside(event => onCancel(), {
    disabled: activeOperation !== Operation.UPDATE_NAME,
    eventTypes: ["click"]
  });

  useEffect(() => {
    setModelName({ value: props.modelName, valid: true });
  }, [props]);

  const onCommit = () => {
    if (modelName.value !== props.modelName) {
      if (commitModelName !== undefined) {
        commitModelName(modelName.value);
      }
    }
    onCancel();
  };

  const onCancel = () => {
    setActiveOperation(Operation.NONE);
  };

  return (
    <Form
      className="modelTitle"
      onSubmit={e => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <FormGroup
        fieldId="modelName"
        helperTextInvalid="Name must be present"
        helperTextInvalidIcon={<ExclamationCircleIcon />}
        validated={modelName.valid ? "default" : "error"}
      >
        {activeOperation !== Operation.UPDATE_NAME && (
          <Tooltip content={<div>{props.modelName}</div>}>
            <TextContent
              tabIndex={0}
              onKeyDown={e => {
                if (e.key === "Enter") {
                  if (commitModelName !== undefined) {
                    setActiveOperation(Operation.UPDATE_NAME);
                  }
                }
              }}
            >
              <Title
                size="3xl"
                headingLevel="h2"
                className="modelTitle__truncate"
                onClick={e => {
                  if (commitModelName !== undefined) {
                    setActiveOperation(Operation.UPDATE_NAME);
                  }
                }}
              >
                {modelName.value}
              </Title>
            </TextContent>
          </Tooltip>
        )}
        {activeOperation === Operation.UPDATE_NAME && (
          <TextInput
            ref={ref}
            type="text"
            id="modelName"
            name="modelName"
            aria-describedby="modelName"
            autoFocus={true}
            value={modelName.value}
            validated={modelName.valid ? "default" : "error"}
            onChange={e => setModelName({ value: e, valid: e.length > 0 })}
            onBlur={e => {
              if (modelName.valid) {
                onCommit();
              }
            }}
          />
        )}
      </FormGroup>
    </Form>
  );
};
