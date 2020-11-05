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
  Characteristic,
  Characteristics,
  Model,
  Output,
  PMML,
  Scorecard
} from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicPanel, CharacteristicsTable, CorePropertiesTable, IndexedCharacteristic } from "../organisms";
import { getModelName } from "../../..";
import { Actions } from "../../../reducers";
import { useDispatch, useSelector } from "react-redux";
import { CharacteristicsToolbar } from "../molecules";
import "./ScorecardEditorPage.scss";
import { Operation } from "../Operation";
import { EmptyStateModelNotFound } from "../../EditorCore/organisms";

interface ScorecardEditorPageProps {
  path: string;
  modelIndex: number;
}

export const ScorecardEditorPage = (props: ScorecardEditorPageProps) => {
  const { modelIndex } = props;

  const dispatch = useDispatch();

  const [filter, setFilter] = useState("");
  const [showCharacteristicPanel, setShowCharacteristicPanel] = useState(false);
  const [selectedCharacteristic, setSelectedCharacteristic] = useState<IndexedCharacteristic | undefined>(undefined);
  const [activeOperation, setActiveOperation] = useState(Operation.NONE);

  const model: Scorecard | undefined = useSelector<PMML, Scorecard | undefined>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof Scorecard) {
      return _model as Scorecard;
    }
    return undefined;
  });

  const characteristics: Characteristics | undefined = useMemo(() => model?.Characteristics, [model]);
  const output: Output | undefined = useMemo(() => model?.Output, [model]);

  const onAddAttribute = useCallback(() => {
    setActiveOperation(Operation.CREATE_ATTRIBUTE);
  }, [characteristics]);

  const onAddCharacteristic = useCallback(() => {
    setActiveOperation(Operation.CREATE_CHARACTERISTIC);
  }, [characteristics]);

  const selectCharacteristic = useCallback(
    index => {
      if (activeOperation !== Operation.NONE) {
        return;
      }
      setShowCharacteristicPanel(true);
      setSelectedCharacteristic({
        index: index,
        characteristic: characteristics?.Characteristic[index] as Characteristic
      });
    },
    [characteristics, activeOperation]
  );

  const validateCharacteristicName = useCallback(
    (index: number | undefined, name: string): boolean => {
      if (name === undefined || name === "") {
        return false;
      }
      const existing: Characteristic[] = characteristics?.Characteristic ?? [];
      const matching = existing.filter((c, _index) => _index !== index && c.name === name);
      return matching.length === 0;
    },
    [characteristics]
  );

  const validateText = (text: string | undefined) => {
    return text !== undefined && text !== "";
  };

  const hideCharacteristicPanel = useCallback(() => {
    setShowCharacteristicPanel(false);
  }, [characteristics]);

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
          <CharacteristicPanel
            modelIndex={modelIndex}
            characteristic={selectedCharacteristic}
            activeOperation={activeOperation}
            setActiveOperation={setActiveOperation}
            showCharacteristicPanel={showCharacteristicPanel}
            hideCharacteristicPanel={hideCharacteristicPanel}
            validateText={validateText}
            addAttribute={onAddAttribute}
            deleteAttribute={index => {
              if (window.confirm(`Delete Attribute "${index}"?`)) {
                dispatch({
                  type: Actions.Scorecard_DeleteAttribute,
                  payload: {
                    modelIndex: modelIndex,
                    characteristicIndex: selectedCharacteristic?.index,
                    attributeIndex: index
                  }
                });
              }
            }}
            commit={(_index, _text, _partialScore, _reasonCode) => {
              if (_index === undefined) {
                dispatch({
                  type: Actions.Scorecard_AddAttribute,
                  payload: {
                    modelIndex: modelIndex,
                    characteristicIndex: selectedCharacteristic?.index,
                    text: _text,
                    partialScore: _partialScore,
                    reasonCode: _reasonCode
                  }
                });
              } else {
                dispatch({
                  type: Actions.Scorecard_UpdateAttribute,
                  payload: {
                    modelIndex: modelIndex,
                    characteristicIndex: selectedCharacteristic?.index,
                    attributeIndex: _index,
                    text: _text,
                    partialScore: _partialScore,
                    reasonCode: _reasonCode
                  }
                });
              }
            }}
          />

          <PageSection variant={PageSectionVariants.light} isFilled={false}>
            <EditorHeader
              title={getModelName(model)}
              activeOperation={activeOperation}
              setActiveOperation={setActiveOperation}
              modelIndex={modelIndex}
              output={output}
            />
          </PageSection>

          <PageSection isFilled={false}>
            <CorePropertiesTable
              activeOperation={activeOperation}
              setActiveOperation={setActiveOperation}
              isScorable={model.isScorable ?? true}
              functionName={model.functionName}
              algorithmName={model.algorithmName ?? ""}
              baselineScore={model.baselineScore ?? 0}
              baselineMethod={model.baselineMethod ?? "other"}
              initialScore={model.initialScore ?? 0}
              useReasonCodes={model.useReasonCodes ?? true}
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
                    useReasonCodes: _props.useReasonCodes,
                    reasonCodeAlgorithm: _props.reasonCodeAlgorithm
                  }
                });
              }}
            />
          </PageSection>

          <PageSection isFilled={true} style={{ paddingTop: "0px" }}>
            <PageSection variant={PageSectionVariants.light}>
              <CharacteristicsToolbar
                activeOperation={activeOperation}
                onFilter={setFilter}
                onAddCharacteristic={onAddCharacteristic}
              />
              <CharacteristicsTable
                activeOperation={activeOperation}
                setActiveOperation={setActiveOperation}
                characteristics={filteredCharacteristics}
                validateCharacteristicName={validateCharacteristicName}
                selectCharacteristic={index => selectCharacteristic(index)}
                addCharacteristic={onAddCharacteristic}
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
                commit={(_index, _name, _reasonCode, _baselineScore) => {
                  if (_index === undefined) {
                    dispatch({
                      type: Actions.Scorecard_AddCharacteristic,
                      payload: {
                        modelIndex: modelIndex,
                        name: _name,
                        reasonCode: _reasonCode,
                        baselineScore: _baselineScore
                      }
                    });
                  } else {
                    dispatch({
                      type: Actions.Scorecard_UpdateCharacteristic,
                      payload: {
                        modelIndex: modelIndex,
                        characteristicIndex: _index,
                        name: _name,
                        reasonCode: _reasonCode,
                        baselineScore: _baselineScore
                      }
                    });
                  }
                }}
              />
            </PageSection>
          </PageSection>
        </>
      )}
    </div>
  );
};
