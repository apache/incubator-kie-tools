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
import { Output } from "@kogito-tooling/pmml-editor-marshaller";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";
import { Bullseye } from "@patternfly/react-core";
import "./OutputsContainer.scss";

interface OutputsContainerProps {
  output?: Output;
}

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { output } = props;

  const addOutput = () => {
    window.alert("Add Output");
  };

  return (
    <div className="outputs-container">
      {(output?.OutputField ?? []).length > 0 && <p>OutputsContainer output=${output?.OutputField.length}</p>}
      {(output?.OutputField ?? []).length === 0 && (
        <Bullseye>
          <EmptyStateNoOutput addOutput={addOutput} />
        </Bullseye>
      )}
    </div>
  );
};
