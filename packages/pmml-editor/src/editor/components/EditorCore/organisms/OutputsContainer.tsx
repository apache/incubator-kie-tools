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
import { Output, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";
import { Bullseye, Button, Flex, FlexItem } from "@patternfly/react-core";
import { PlusIcon } from "@patternfly/react-icons";
import { OutputsTable } from "./OutputsTable";
import { Operation } from "../../EditorScorecard";
import "./OutputsContainer.scss";

interface OutputsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
  validateOutputName: (index: number | undefined, name: string | undefined) => boolean;
}

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { activeOperation, setActiveOperation, output, validateOutputName } = props;

  const addOutput = () => {
    setActiveOperation(Operation.CREATE_OUTPUT);
  };

  return (
    <div className="outputs-container">
      <Flex style={{ margin: "1em 0 2em 0" }}>
        <FlexItem>
          <Button
            variant="secondary"
            onClick={e => addOutput()}
            isDisabled={activeOperation !== Operation.NONE}
            icon={<PlusIcon />}
            iconPosition="left"
          >
            Add Output
          </Button>
        </FlexItem>
      </Flex>
      {(output?.OutputField ?? []).length > 0 && (
        <div className="outputs-container__list">
          <div className="outputs-container__list--container">
            <OutputsTable
              activeOperation={activeOperation}
              setActiveOperation={setActiveOperation}
              validateOutputName={validateOutputName}
              deleteOutput={_index => null}
              commit={(index, text) => null}
              outputs={output?.OutputField as OutputField[]}
            />
          </div>
        </div>
      )}
      {(output?.OutputField ?? []).length === 0 && (
        <Bullseye>
          <EmptyStateNoOutput addOutput={addOutput} />
        </Bullseye>
      )}
    </div>
  );
};
