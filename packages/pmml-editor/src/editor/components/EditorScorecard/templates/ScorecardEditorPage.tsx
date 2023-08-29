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
import { useCallback, useMemo } from "react";
import { PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { EditorHeader } from "../../EditorCore/molecules";
import {
  Characteristics,
  MiningSchema,
  Model,
  Output,
  OutputField,
  PMML,
  Scorecard,
} from "@kie-tools/pmml-editor-marshaller";
import { CharacteristicsContainer, CorePropertiesTable } from "../organisms";
import { getModelName } from "../../..";
import { Actions } from "../../../reducers";
import { useSelector } from "react-redux";
import "./ScorecardEditorPage.scss";
import { EmptyStateModelNotFound } from "../../EditorCore/organisms";
import { useBatchDispatch, useHistoryService } from "../../../history";

interface ScorecardEditorPageProps {
  path: string;
  modelIndex: number;
}

export const ScorecardEditorPage = (props: ScorecardEditorPageProps) => {
  const { modelIndex } = props;

  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const model: Scorecard | undefined = useSelector<PMML, Scorecard | undefined>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof Scorecard) {
      return _model as Scorecard;
    }
    return undefined;
  });

  const modelName = useMemo(() => getModelName(model as Model), [model]);

  const characteristics: Characteristics | undefined = useMemo(() => model?.Characteristics, [model]);
  const miningSchema: MiningSchema | undefined = useMemo(() => model?.MiningSchema, [model]);
  const output: Output | undefined = useMemo(() => model?.Output, [model]);

  const validateOutputName = useCallback(
    (index: number | undefined, name: string): boolean => {
      if (name.toString().trim().length === 0) {
        return false;
      }
      const existing: OutputField[] = output?.OutputField ?? [];
      const matching = existing.filter((c, _index) => _index !== index && c.name === name);
      return matching.length === 0;
    },
    [output]
  );

  const isBaselineScoreDisabled = useMemo(() => {
    return (
      characteristics?.Characteristic !== undefined &&
      characteristics.Characteristic.length > 0 &&
      characteristics.Characteristic.every((characteristic) => characteristic.baselineScore !== undefined)
    );
  }, [characteristics]);

  const onDeleteOutputField = useCallback(
    (_index) => {
      //See https://issues.redhat.com/browse/FAI-443
      //if (window.confirm(`Delete Output "${output?.OutputField[_index].name}"?`)) {
      dispatch({
        type: Actions.DeleteOutput,
        payload: {
          modelIndex: modelIndex,
          outputIndex: _index,
        },
      });
      //}
    },
    [modelIndex, output]
  );

  const onUpdateOutputField = useCallback(
    (_index, _outputField) => {
      if (_index === undefined) {
        dispatch({
          type: Actions.AddOutput,
          payload: {
            modelIndex: modelIndex,
            outputField: _outputField,
          },
        });
      } else {
        dispatch({
          type: Actions.UpdateOutput,
          payload: {
            modelIndex: modelIndex,
            outputIndex: _index,
            outputField: _outputField,
          },
        });
      }
    },
    [modelIndex]
  );

  const onUpdateModelName = useCallback(
    (_modelName: string) => {
      if (_modelName !== modelName) {
        dispatch({
          type: Actions.Scorecard_SetModelName,
          payload: {
            modelIndex: modelIndex,
            modelName: _modelName === "" ? undefined : _modelName,
          },
        });
      }
    },
    [modelIndex]
  );

  const onUpdateCoreProperty = useCallback(
    (_props) => {
      dispatch({
        type: Actions.Scorecard_SetCoreProperties,
        payload: {
          modelIndex: modelIndex,
          isScorable: _props.isScorable,
          functionName: _props.functionName,
          algorithmName: _props.algorithmName,
          baselineScore: _props.baselineScore,
          baselineMethod: _props.baselineMethod,
          initialScore: _props.initialScore,
          useReasonCodes: _props.areReasonCodesUsed,
          reasonCodeAlgorithm: _props.reasonCodeAlgorithm,
        },
      });
    },
    [modelIndex]
  );

  return (
    <div data-testid="editor-page" className={"editor"}>
      {!model && <EmptyStateModelNotFound />}
      {model && (
        <>
          <div className={"editor__header__container"}>
            <div className={"editor__header__content"}>
              <PageSection variant={PageSectionVariants.light} isFilled={false}>
                <EditorHeader
                  modelName={modelName}
                  modelIndex={modelIndex}
                  miningSchema={miningSchema}
                  output={output}
                  validateOutputFieldName={validateOutputName}
                  deleteOutputField={onDeleteOutputField}
                  commitOutputField={onUpdateOutputField}
                  commitModelName={onUpdateModelName}
                />
              </PageSection>
            </div>
          </div>

          <div className={"editor__body__container"}>
            <div className={"editor__body__content"}>
              <PageSection isFilled={false} data-ouia-component-id="model-setup">
                <CorePropertiesTable
                  modelIndex={modelIndex}
                  isScorable={model.isScorable ?? true}
                  functionName={model.functionName}
                  algorithmName={model.algorithmName}
                  baselineScore={model.baselineScore}
                  isBaselineScoreDisabled={isBaselineScoreDisabled}
                  baselineMethod={model.baselineMethod ?? "other"}
                  initialScore={model.initialScore}
                  areReasonCodesUsed={model.useReasonCodes ?? true}
                  reasonCodeAlgorithm={model.reasonCodeAlgorithm ?? "pointsBelow"}
                  commit={onUpdateCoreProperty}
                />
              </PageSection>

              <PageSection isFilled={true} style={{ paddingTop: "0px" }}>
                <PageSection variant={PageSectionVariants.light} style={{ height: "100%" }}>
                  <CharacteristicsContainer
                    modelIndex={modelIndex}
                    areReasonCodesUsed={model.useReasonCodes ?? true}
                    scorecardBaselineScore={model.baselineScore}
                    characteristics={characteristics?.Characteristic ?? []}
                  />
                </PageSection>
              </PageSection>
            </div>
          </div>
        </>
      )}
    </div>
  );
};
