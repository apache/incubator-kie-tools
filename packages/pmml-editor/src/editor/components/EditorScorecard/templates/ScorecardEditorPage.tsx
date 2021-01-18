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
import { useCallback, useMemo, useState } from "react";
import { PageSection, PageSectionVariants } from "@patternfly/react-core";
import { EditorHeader } from "../../EditorCore/molecules";
import {
  Characteristics,
  MiningSchema,
  Model,
  Output,
  OutputField,
  PMML,
  Scorecard
} from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicsContainer, CorePropertiesTable, IndexedCharacteristic } from "../organisms";
import { getModelName, HistoryContext } from "../../..";
import { Actions } from "../../../reducers";
import { useSelector } from "react-redux";
import "./ScorecardEditorPage.scss";
import { EmptyStateModelNotFound } from "../../EditorCore/organisms";
import { useBatchDispatch } from "../../../history";

interface ScorecardEditorPageProps {
  path: string;
  modelIndex: number;
}

export const ScorecardEditorPage = (props: ScorecardEditorPageProps) => {
  const { modelIndex } = props;

  const { service, getCurrentState } = React.useContext(HistoryContext);
  const dispatch = useBatchDispatch(service, getCurrentState);

  const [filter, setFilter] = useState("");

  const model: Scorecard | undefined = useSelector<PMML, Scorecard | undefined>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof Scorecard) {
      return _model as Scorecard;
    }
    return undefined;
  });

  const characteristics: Characteristics | undefined = useMemo(() => model?.Characteristics, [model]);
  const output: Output | undefined = useMemo(() => model?.Output, [model]);
  const miningSchema: MiningSchema | undefined = useMemo(() => model?.MiningSchema, [model]);

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

  const filterCharacteristics = useCallback((): IndexedCharacteristic[] => {
    const _lowerCaseFilter = filter.toLowerCase();
    const _filteredCharacteristics = characteristics?.Characteristic.map<IndexedCharacteristic>(
      (_characteristic, index) => ({ index: index, characteristic: _characteristic } as IndexedCharacteristic)
    ).filter(ic => {
      const _characteristicName = ic.characteristic.name;
      return _characteristicName?.toLowerCase().includes(_lowerCaseFilter);
    });
    return _filteredCharacteristics ?? [];
  }, [filter, characteristics]);

  const filteredCharacteristics: IndexedCharacteristic[] = useMemo(() => filterCharacteristics(), [
    filter,
    characteristics
  ]);

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

          <PageSection isFilled={false}>
            <CorePropertiesTable
              isScorable={model.isScorable ?? true}
              functionName={model.functionName}
              algorithmName={model.algorithmName ?? ""}
              baselineScore={model.baselineScore}
              baselineMethod={model.baselineMethod ?? "other"}
              initialScore={model.initialScore}
              areReasonCodesUsed={model.useReasonCodes ?? true}
              reasonCodeAlgorithm={model.reasonCodeAlgorithm ?? "pointsBelow"}
              commit={_props => {
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
                    reasonCodeAlgorithm: _props.reasonCodeAlgorithm
                  }
                });
              }}
            />
          </PageSection>

          <PageSection isFilled={true} style={{ paddingTop: "0px" }}>
            <PageSection variant={PageSectionVariants.light}>
              <div>
                <CharacteristicsContainer
                  modelIndex={modelIndex}
                  areReasonCodesUsed={model.useReasonCodes ?? true}
                  isBaselineScoreRequired={(model.useReasonCodes ?? true) && model.baselineScore === undefined}
                  characteristics={characteristics?.Characteristic ?? []}
                  filteredCharacteristics={filteredCharacteristics}
                  filter={filter}
                  onFilter={setFilter}
                  deleteCharacteristic={index => {
                    if (window.confirm(`Delete Characteristic "${index}"?`)) {
                      dispatch({
                        type: Actions.Scorecard_DeleteCharacteristic,
                        payload: {
                          modelIndex: modelIndex,
                          characteristicIndex: index
                        }
                      });
                    }
                  }}
                  commit={(_index, _characteristic) => {
                    if (_index === undefined) {
                      dispatch({
                        type: Actions.Scorecard_AddCharacteristic,
                        payload: {
                          modelIndex: modelIndex,
                          name: _characteristic.name,
                          reasonCode: _characteristic.reasonCode,
                          baselineScore: _characteristic.baselineScore
                        }
                      });
                    } else {
                      dispatch({
                        type: Actions.Scorecard_UpdateCharacteristic,
                        payload: {
                          modelIndex: modelIndex,
                          characteristicIndex: _index,
                          name: _characteristic.name,
                          reasonCode: _characteristic.reasonCode,
                          baselineScore: _characteristic.baselineScore
                        }
                      });
                    }
                  }}
                />
              </div>
            </PageSection>
          </PageSection>
        </>
      )}
    </div>
  );
};
