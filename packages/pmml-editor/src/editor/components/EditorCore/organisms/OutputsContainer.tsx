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
import { Bullseye } from "@patternfly/react-core";
import "./OutputsContainer.scss";
import { OutputsTableHeader } from "./OutputsTableHeader";
import { OutputsTable } from "./OutputsTable";
import { Operation } from "../../EditorScorecard";

interface OutputsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
}

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { activeOperation, setActiveOperation, output } = props;

  const addOutput = () => {
    setActiveOperation(Operation.CREATE_OUTPUT);
  };

  return (
    <div className="outputs-container">
      {(output?.OutputField ?? []).length > 0 && (
        <div className="outputs-container__list__container">
          <OutputsTableHeader />
          <div className="outputs-container__list">
            <OutputsTable
              activeOperation={activeOperation}
              setActiveOperation={setActiveOperation}
              validateName={_name => true}
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
