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
import { Form, FormGroup, Split, SplitItem, Stack, StackItem, Text, TextInput, Tooltip } from "@patternfly/react-core";
import "./ModelTitle.scss";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation, useOperation } from "../../EditorScorecard";
import { HelpIcon } from "@patternfly/react-icons";

interface ModelTitleProps {
  modelName: string;
  commitModelName?: (_modelName: string) => void;
}

export const MODEL_NAME_NOT_SET = "<Model Name not set>";

export const ModelTitle = (props: ModelTitleProps) => {
  const { commitModelName } = props;

  const [isEditing, setEditing] = useState(false);
  const [modelName, setModelName] = useState("");

  const { activeOperation, setActiveOperation } = useOperation();

  const ref = useOnclickOutside(event => onCommitAndClose(), {
    disabled: activeOperation !== Operation.UPDATE_NAME,
    eventTypes: ["click"]
  });

  useEffect(() => {
    setModelName(props.modelName);
  }, [props.modelName]);

  const onEdit = () => {
    if (commitModelName !== undefined) {
      setEditing(true);
      setActiveOperation(Operation.UPDATE_NAME);
    }
  };

  const onCommitAndClose = () => {
    onCommit();
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
          setModelName(props.modelName);
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
              <SplitItem className="modelTitle__icon">
                <Tooltip content={"The Model Name will be generated at runtime if not set."}>
                  <button
                    aria-label="More info about Model Name"
                    onClick={e => e.preventDefault()}
                    className="pf-c-form__group-label-help modelTitle__icon"
                  >
                    <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
                  </button>
                </Tooltip>
              </SplitItem>
              <SplitItem isFilled={true} className={"modelTitle--hide-overflow"}>
                <FormGroup fieldId="modelName">
                  {!isEditModeEnabled && (
                    <div className={modelTitleClassNames} onClick={onEdit}>
                      {modelName.trim() !== "" && <Text className="modelTitle__truncate">{modelName}</Text>}
                      {modelName.trim() === "" && (
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
                      placeholder={MODEL_NAME_NOT_SET}
                      onChange={setModelName}
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
