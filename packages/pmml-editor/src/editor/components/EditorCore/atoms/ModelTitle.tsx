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
import { useEffect, useMemo, useState } from "react";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import "./ModelTitle.scss";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation, useOperation } from "../../EditorScorecard";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";

interface ModelTitleProps {
  modelName: string;
  commitModelName?: (_modelName: string) => void;
}

export const MODEL_NAME_NOT_SET = "<Model Name not set>";

export const ModelTitle = (props: ModelTitleProps) => {
  const { modelName, commitModelName } = props;

  const [isEditing, setEditing] = useState(false);
  const [title, setTitle] = useState("");

  const { activeOperation, setActiveOperation } = useOperation();

  const ref = useOnclickOutside((event) => onCommitAndClose(), {
    disabled: activeOperation !== Operation.UPDATE_NAME,
    eventTypes: ["click"],
  });

  useEffect(() => {
    setTitle(modelName);
  }, [modelName]);

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
      commitModelName(title);
    }
  };

  const onCancel = () => {
    setEditing(false);
    setActiveOperation(Operation.NONE);
  };

  const isEditModeEnabled = useMemo(
    () => isEditing && activeOperation === Operation.UPDATE_NAME,
    [isEditing, activeOperation]
  );

  const modelTitleClassNames = useMemo(
    () => `${commitModelName !== undefined ? "modelTitle" : "modelTitle modelTitle--editing"} pf-v5-c-title pf-m-2xl`,
    [commitModelName]
  );

  return (
    <div
      ref={ref}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          onEdit();
        } else if (e.key === "Escape") {
          setTitle(modelName);
          onCancel();
        }
      }}
    >
      <div className={"modelTitle--full-width"}>
        <Form
          id={"modelTitle-form"}
          onSubmit={(e) => {
            e.stopPropagation();
            e.preventDefault();
          }}
        >
          <Split hasGutter={true} className={"modelTitle--hide-overflow"}>
            <SplitItem className="modelTitle__icon">
              <Tooltip content={"The Model Name will be generated at runtime if not set."}>
                <button
                  aria-label="More info about Model Name"
                  onClick={(e) => e.preventDefault()}
                  className="pf-v5-c-form__group-label-help modelTitle__icon"
                >
                  <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                </button>
              </Tooltip>
            </SplitItem>
            <SplitItem isFilled={true} className={"modelTitle--hide-overflow"}>
              <FormGroup fieldId="modelName">
                {!isEditModeEnabled && (
                  <div className={modelTitleClassNames} onClick={onEdit} data-ouia-component-id="model-name">
                    {modelName.trim() !== "" && <Text className="modelTitle__truncate">{modelName}</Text>}
                    {modelName.trim() === "" && (
                      <Text className="modelTitle__truncate modelTitle__truncate--disabled">{MODEL_NAME_NOT_SET}</Text>
                    )}
                  </div>
                )}
                {isEditModeEnabled && (
                  <TextInput
                    type="text"
                    id="modelName"
                    name="modelName"
                    aria-describedby="modelName "
                    className={`${modelTitleClassNames} modelTitle--editing`}
                    autoFocus={true}
                    value={title}
                    placeholder={MODEL_NAME_NOT_SET}
                    onChange={(_event, val) => setTitle(val)}
                    onBlur={onCommitAndClose}
                    ouiaId="set-model-name"
                  />
                )}
              </FormGroup>
            </SplitItem>
          </Split>
        </Form>
      </div>
    </div>
  );
};
