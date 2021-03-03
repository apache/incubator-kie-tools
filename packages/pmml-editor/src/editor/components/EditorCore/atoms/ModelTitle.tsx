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
import { useEffect, useMemo, useState } from "react";
import { Form, FormGroup, Split, SplitItem, Stack, StackItem, Text, TextInput } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import "./ModelTitle.scss";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation, useOperation } from "../../EditorScorecard";
import { useValidationRegistry } from "../../../validation";
import { ValidationIndicator } from "./ValidationIndicator";
import { Builder } from "../../../paths";
import { validateModelName } from "../../../validation/Model";

interface ModelTitleProps {
  modelIndex: number;
  modelName: string;
  commitModelName?: (_modelName: string) => void;
}

export const MODEL_NAME_NOT_SET = "<Model Name not set>";

export const ModelTitle = (props: ModelTitleProps) => {
  const { modelIndex, commitModelName } = props;

  const [isEditing, setEditing] = useState(false);
  const [modelName, setModelName] = useState("");

  const { activeOperation, setActiveOperation } = useOperation();

  const ref = useOnclickOutside(event => onCommitAndClose(), {
    disabled: activeOperation !== Operation.UPDATE_NAME,
    eventTypes: ["click"]
  });

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forModelName()
          .build()
      ),
    [modelIndex, modelName]
  );

  useEffect(() => {
    setModelName(props.modelName);
  }, [props.modelIndex, props.modelName]);

  const onEdit = () => {
    if (commitModelName !== undefined) {
      setEditing(true);
      setActiveOperation(Operation.UPDATE_NAME);
    }
  };

  const onCommitAndClose = () => {
    if (validations.length === 0) {
      onCommit();
    } else {
      validateAndSetModelName(props.modelName);
    }
    onCancel();
  };

  const onCommit = () => {
    if (commitModelName !== undefined) {
      commitModelName(modelName);
    }
  };

  const onCancel = () => {
    setEditing(false);
    setActiveOperation(Operation.NONE);
  };

  const validateAndSetModelName = (_modelName: string) => {
    validationRegistry.clear(
      Builder()
        .forModel(modelIndex)
        .forModelName()
        .build()
    );
    validateModelName(modelIndex, _modelName, validationRegistry);
    setModelName(_modelName);
  };

  const isEditModeEnabled = useMemo(() => isEditing && activeOperation === Operation.UPDATE_NAME, [
    isEditing,
    activeOperation
  ]);

  const modelTitleClassNames = useMemo(
    () => `${commitModelName !== undefined ? "modelTitle" : "modelTitle modelTitle--editing"} pf-c-form-control`,
    [commitModelName]
  );

  return (
    <div
      ref={ref}
      onKeyDown={e => {
        if (e.key === "Enter") {
          onEdit();
        } else if (e.key === "Escape") {
          validateAndSetModelName(props.modelName);
          onCancel();
        }
      }}
    >
      <Stack hasGutter={true} className={"modelTitle--full-width"}>
        <StackItem>
          <Form
            id={"modelTitle-form"}
            onSubmit={e => {
              e.stopPropagation();
              e.preventDefault();
            }}
          >
            <Split hasGutter={true} className={"modelTitle--hide-overflow"}>
              {validations.length !== 0 && (
                <SplitItem className="modelTitle__icon">
                  <ValidationIndicator validations={validations} />
                </SplitItem>
              )}
              <SplitItem isFilled={true} className={"modelTitle--hide-overflow"}>
                <FormGroup
                  fieldId="modelName"
                  helperTextInvalidIcon={<ExclamationCircleIcon />}
                  helperText={validations[0] ? validations[0].message : ""}
                  validated={validations.length === 0 ? "default" : "warning"}
                >
                  {!isEditModeEnabled && (
                    <div className={modelTitleClassNames} onClick={onEdit}>
                      {validations.length === 0 && <Text className="modelTitle__truncate">{modelName}</Text>}
                      {validations.length !== 0 && (
                        <Text className="modelTitle__truncate modelTitle__truncate--disabled">
                          {MODEL_NAME_NOT_SET}
                        </Text>
                      )}
                    </div>
                  )}
                  {isEditModeEnabled && (
                    <TextInput
                      type="text"
                      id="modelName"
                      name="modelName"
                      aria-describedby="modelName"
                      className="modelTitle--editing"
                      autoFocus={true}
                      value={modelName}
                      validated={validations.length === 0 ? "default" : "warning"}
                      placeholder={MODEL_NAME_NOT_SET}
                      onChange={e => validateAndSetModelName(e)}
                      onBlur={onCommitAndClose}
                    />
                  )}
                </FormGroup>
              </SplitItem>
            </Split>
          </Form>
        </StackItem>
      </Stack>
    </div>
  );
};
