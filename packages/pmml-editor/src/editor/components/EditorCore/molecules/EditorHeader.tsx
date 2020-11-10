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
import { HeaderTitle } from "../../Header/atoms";
import * as React from "react";
import { Split, SplitItem } from "@patternfly/react-core";
import DataDictionaryHandler from "../../DataDictionary/DataDictionaryHandler/DataDictionaryHandler";
import { OutputsHandler } from "../organisms";
import { Operation } from "../../EditorScorecard";
import {
  DataType,
  FieldName,
  MiningSchema,
  OpType,
  Output,
  RankOrder,
  ResultFeature
} from "@kogito-tooling/pmml-editor-marshaller";
import MiningSchemaHandler from "../../MiningSchema/MiningSchemaHandler/MiningSchemaHandler";

interface EditorHeaderProps {
  title: string;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  modelIndex: number;
  output?: Output;
  miningSchema?: MiningSchema;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutputField: (index: number) => void;
  commit: (
    index: number | undefined,
    name: FieldName | undefined,
    dataType: DataType | undefined,
    optype: OpType | undefined,
    targetField: FieldName | undefined,
    feature: ResultFeature | undefined,
    value: any | undefined,
    rank: number | undefined,
    rankOrder: RankOrder | undefined,
    segmentId: string | undefined,
    isFinalResult: boolean | undefined
  ) => void;
}

export const EditorHeader = (props: EditorHeaderProps) => {
  const {
    activeOperation,
    setActiveOperation,
    miningSchema,
    modelIndex,
    output,
    validateOutputFieldName,
    deleteOutputField,
    commit
  } = props;

  return (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <HeaderTitle title={props.title} />
      </SplitItem>
      <SplitItem>
        <DataDictionaryHandler activeOperation={activeOperation} />
      </SplitItem>
      <SplitItem>
        <MiningSchemaHandler activeOperation={activeOperation} miningSchema={miningSchema} modelIndex={modelIndex} />
      </SplitItem>
      <SplitItem>
        <OutputsHandler
          activeOperation={activeOperation}
          setActiveOperation={setActiveOperation}
          modelIndex={modelIndex}
          output={output}
          validateOutputFieldName={validateOutputFieldName}
          deleteOutputField={deleteOutputField}
          commit={commit}
        />
      </SplitItem>
    </Split>
  );
};
