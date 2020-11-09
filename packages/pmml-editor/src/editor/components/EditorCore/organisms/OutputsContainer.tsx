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
import { useState } from "react";
import { DataType, FieldName, Output, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { Button, Flex, FlexItem } from "@patternfly/react-core";
import { PlusIcon } from "@patternfly/react-icons";
import { OutputsTable } from "./OutputsTable";
import { Operation } from "../../EditorScorecard";
import "./OutputsContainer.scss";
import { CSSTransition, SwitchTransition } from "react-transition-group";

interface OutputsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
  validateOutputName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutput: (index: number) => void;
  commit: (index: number | undefined, name: FieldName | undefined, dataType: DataType | undefined) => void;
}

type OutputsViewSection = "overview" | "extended-properties";

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { activeOperation, setActiveOperation, output, validateOutputName, deleteOutput, commit } = props;

  const [viewSection, setViewSection] = useState<OutputsViewSection>("overview");

  const getTransition = (_viewSection: OutputsViewSection) => {
    if (_viewSection === "overview") {
      return "outputs-container__overview";
    } else {
      return "enter-from-right";
    }
  };

  const addOutput = () => {
    setActiveOperation(Operation.CREATE_OUTPUT);
  };

  return (
    <div className="outputs-container">
      <Flex style={{ margin: "1em 0 2em 0" }}>
        <FlexItem>
          <Button
            variant="secondary"
            onClick={addOutput}
            isDisabled={activeOperation !== Operation.NONE}
            icon={<PlusIcon />}
            iconPosition="left"
          >
            Add Output
          </Button>
        </FlexItem>
      </Flex>
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection == "overview" && (
              <div className="outputs-container__list">
                <OutputsTable
                  activeOperation={activeOperation}
                  setActiveOperation={setActiveOperation}
                  outputs={output?.OutputField as OutputField[]}
                  addOutput={addOutput}
                  validateOutputName={validateOutputName}
                  viewExtendedProperties={() => setViewSection("extended-properties")}
                  deleteOutput={deleteOutput}
                  commit={commit}
                />
              </div>
            )}
            {viewSection == "extended-properties" && (
              <>
                <p>Some where else</p>
                <Button
                  onClick={e => {
                    setActiveOperation(Operation.NONE);
                    setViewSection("overview");
                  }}
                >
                  Return
                </Button>{" "}
              </>
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </div>
  );
};
