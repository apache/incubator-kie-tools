/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useMemo } from "react";
import { PageSection, PageSectionVariants } from "@patternfly/react-core";
import { EditorHeader } from "../../EditorCore/molecules";
import {
  MiningSchema,
  Model,
  Output,
  OutputField,
  PMML,
  RegressionModel
} from "@kogito-tooling/pmml-editor-marshaller";
import { getModelName } from "../../..";
import { Actions } from "../../../reducers";
import { useDispatch, useSelector } from "react-redux";
import "./LinearRegressionViewerPage.scss";
import { EmptyStateModelNotFound } from "../../EditorCore/organisms";
import { LinearRegressionViewAdaptor } from "../molecules";

interface LinearRegressionViewerPageProps {
  path: string;
  modelIndex: number;
}

export const LinearRegressionViewerPage = (props: LinearRegressionViewerPageProps) => {
  const { modelIndex } = props;

  const dispatch = useDispatch();

  const model: RegressionModel | undefined = useSelector<PMML, RegressionModel | undefined>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof RegressionModel) {
      const _regressionModel = _model as RegressionModel;
      if (_regressionModel.functionName === "regression" && _regressionModel.algorithmName === "linearRegression") {
        return _regressionModel;
      }
    }
    return undefined;
  });

  const miningSchema: MiningSchema | undefined = useMemo(() => model?.MiningSchema, [model]);
  const output: Output | undefined = useMemo(() => model?.Output, [model]);

  const validateOutputName = useCallback(
    (index: number | undefined, name: string): boolean => {
      if (name === undefined || name.trim() === "") {
        return false;
      }
      const existing: OutputField[] = output?.OutputField ?? [];
      const matching = existing.filter((c, _index) => _index !== index && c.name === name);
      return matching.length === 0;
    },
    [output]
  );

  return (
    <div data-testid="editor-page" className={"editor"}>
      {!model && <EmptyStateModelNotFound />}
      {model && (
        <>
          <PageSection variant={PageSectionVariants.light} isFilled={false}>
            <EditorHeader
              modelName={getModelName(model)}
              modelIndex={modelIndex}
              miningSchema={miningSchema}
              output={output}
              validateOutputFieldName={validateOutputName}
              deleteOutputField={_index => {
                if (window.confirm(`Delete Output "${_index}"?`)) {
                  dispatch({
                    type: Actions.DeleteOutput,
                    payload: {
                      modelIndex: modelIndex,
                      outputIndex: _index
                    }
                  });
                }
              }}
              commitOutputField={(_index, _outputField: OutputField) => {
                if (_index === undefined) {
                  dispatch({
                    type: Actions.AddOutput,
                    payload: {
                      modelIndex: modelIndex,
                      outputField: _outputField
                    }
                  });
                } else {
                  dispatch({
                    type: Actions.UpdateOutput,
                    payload: {
                      modelIndex: modelIndex,
                      outputIndex: _index,
                      outputField: _outputField
                    }
                  });
                }
              }}
              commitModelName={(_modelName: string) => {
                dispatch({
                  type: Actions.Scorecard_SetModelName,
                  payload: {
                    modelIndex: modelIndex,
                    modelName: _modelName
                  }
                });
              }}
            />
          </PageSection>

          <PageSection isFilled={true} style={{ paddingTop: "0px" }}>
            <LinearRegressionViewAdaptor model={model} />
          </PageSection>
        </>
      )}
    </div>
  );
};
