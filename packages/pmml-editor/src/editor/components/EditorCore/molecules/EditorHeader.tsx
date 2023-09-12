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
import { ModelTitle } from "../atoms";
import * as React from "react";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import DataDictionaryHandler from "../../DataDictionary/DataDictionaryHandler/DataDictionaryHandler";
import { OutputsHandler } from "../../Outputs/organisms";
import { MiningSchema, Output, OutputField } from "@kie-tools/pmml-editor-marshaller";
import MiningSchemaHandler from "../../MiningSchema/MiningSchemaHandler/MiningSchemaHandler";
import "./EditorHeader.scss";

interface EditorHeaderViewerProps {
  modelName: string;
  modelIndex: number;
}

interface EditorHeaderEditorProps extends EditorHeaderViewerProps {
  output?: Output;
  miningSchema?: MiningSchema;
  validateOutputFieldName: (index: number | undefined, name: string) => boolean;
  deleteOutputField: (index: number) => void;
  commitOutputField: (index: number | undefined, outputField: OutputField) => void;
  commitModelName: (modelName: string) => void;
}

type EditorHeaderProps = EditorHeaderViewerProps | EditorHeaderEditorProps;

const isEditor = (props: EditorHeaderProps): props is EditorHeaderEditorProps => {
  return (props as EditorHeaderEditorProps).modelIndex !== undefined;
};

export const EditorHeader = (props: EditorHeaderProps) => {
  const { modelName, modelIndex } = props;

  if (isEditor(props)) {
    const { miningSchema, output, validateOutputFieldName, deleteOutputField, commitOutputField, commitModelName } =
      props;

    return (
      <Split hasGutter={true} className={"editorHeader"}>
        <SplitItem className={"editorHeader__modelName"}>
          <ModelTitle modelName={modelName} commitModelName={commitModelName} />
        </SplitItem>
        <SplitItem isFilled={true} data-ouia-component-type="filler" />
        <SplitItem>
          <DataDictionaryHandler />
        </SplitItem>
        <SplitItem>
          <MiningSchemaHandler miningSchema={miningSchema} modelIndex={modelIndex} />
        </SplitItem>
        <SplitItem>
          <OutputsHandler
            modelIndex={modelIndex}
            output={output}
            miningSchema={miningSchema}
            validateOutputFieldName={validateOutputFieldName}
            deleteOutputField={deleteOutputField}
            commitOutputField={commitOutputField}
          />
        </SplitItem>
      </Split>
    );
  } else {
    return (
      <Split hasGutter={true} className={"editorHeader"}>
        <SplitItem isFilled={true} className={"editorHeader--modelName"}>
          <ModelTitle modelName={modelName} />
        </SplitItem>
      </Split>
    );
  }
};
