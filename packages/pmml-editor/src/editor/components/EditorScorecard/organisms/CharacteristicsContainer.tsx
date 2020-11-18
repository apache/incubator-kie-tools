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
import { useCallback, useEffect, useRef, useState } from "react";
import { Stack, StackItem } from "@patternfly/react-core";
import { Operation } from "../Operation";
import { CharacteristicsTable, IndexedCharacteristic } from "./CharacteristicsTable";
import "./CharacteristicsContainer.scss";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { AttributesTable } from "./AttributesTable";
import { Actions } from "../../../reducers";
import { useDispatch } from "react-redux";
import { AttributeEditor, AttributesToolbar, AttributeToolbar, CharacteristicsToolbar } from "../molecules";

interface CharacteristicsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  characteristics: IndexedCharacteristic[];
  onFilter: (filter: string) => void;
  emptyStateProvider: () => JSX.Element;
  addCharacteristic: () => void;
  deleteCharacteristic: (index: number) => void;
  commit: (
    index: number | undefined,
    name: string | undefined,
    reasonCode: string | undefined,
    baselineScore: number | undefined
  ) => void;
}

type CharacteristicsViewSection = "overview" | "attributes" | "attribute";

export const CharacteristicsContainer = (props: CharacteristicsContainerProps) => {
  const {
    modelIndex,
    activeOperation,
    setActiveOperation,
    characteristics,
    onFilter,
    emptyStateProvider,
    addCharacteristic,
    deleteCharacteristic,
    commit
  } = props;

  const dispatch = useDispatch();

  const [selectedCharacteristicIndex, setSelectedCharacteristicIndex] = useState<number | undefined>(undefined);
  const [selectedAttributeIndex, setSelectedAttributeIndex] = useState<number | undefined>(undefined);
  const addCharacteristicRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE_CHARACTERISTIC && addCharacteristicRowRef.current) {
      addCharacteristicRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const [viewSection, setViewSection] = useState<CharacteristicsViewSection>("overview");
  const getTransition = (_viewSection: CharacteristicsViewSection) => {
    if (_viewSection === "overview") {
      return "characteristics-container__overview";
    } else if (_viewSection === "attributes") {
      return "characteristics-container__attributes";
    } else {
      return "characteristics-container__attribute";
    }
  };

  const onAddAttribute = useCallback(() => {
    setActiveOperation(Operation.CREATE_ATTRIBUTE);
    setSelectedAttributeIndex(undefined);
    setViewSection("attribute");
  }, [characteristics]);

  const onViewOverviewView = () => {
    setActiveOperation(Operation.NONE);
    setViewSection("overview");
  };

  const onViewAttributesView = () => {
    setActiveOperation(Operation.NONE);
    setViewSection("attributes");
  };

  const onViewAttributes = (index: number | undefined) => {
    if (index === undefined) {
      return;
    }
    setSelectedCharacteristicIndex(index);
    setViewSection("attributes");
  };

  const onViewAttribute = (index: number | undefined) => {
    if (index === undefined) {
      return;
    }
    setSelectedAttributeIndex(index);
    setViewSection("attribute");
  };

  const validateCharacteristicName = useCallback(
    (index: number | undefined, name: string): boolean => {
      if (name === undefined || name.trim() === "") {
        return false;
      }
      const matching = characteristics.filter(ic => index !== ic.index && ic.characteristic.name === name);
      return matching.length === 0;
    },
    [characteristics]
  );

  return (
    <div className="characteristics-container">
      {characteristics.length === 0 && activeOperation === Operation.NONE && emptyStateProvider()}
      {(characteristics.length > 0 || activeOperation === Operation.CREATE_CHARACTERISTIC) && (
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
              {viewSection === "overview" && (
                <Stack hasGutter={true}>
                  <StackItem>
                    <CharacteristicsToolbar onFilter={onFilter} onAddCharacteristic={addCharacteristic} />
                  </StackItem>
                  <StackItem className="characteristics-container__overview">
                    <CharacteristicsTable
                      activeOperation={activeOperation}
                      setActiveOperation={setActiveOperation}
                      characteristics={characteristics}
                      validateCharacteristicName={validateCharacteristicName}
                      viewAttributes={onViewAttributes}
                      deleteCharacteristic={deleteCharacteristic}
                      commit={commit}
                    />
                  </StackItem>
                </Stack>
              )}
              {viewSection === "attributes" && (
                <Stack hasGutter={true}>
                  <StackItem>
                    <AttributesToolbar viewOverview={onViewOverviewView} onAddAttribute={onAddAttribute} />
                  </StackItem>
                  <StackItem className="characteristics-container__attributes">
                    <AttributesTable
                      modelIndex={modelIndex}
                      characteristicIndex={selectedCharacteristicIndex}
                      setActiveOperation={setActiveOperation}
                      viewAttribute={onViewAttribute}
                      deleteAttribute={index => {
                        if (window.confirm(`Delete Attribute "${index}"?`)) {
                          dispatch({
                            type: Actions.Scorecard_DeleteAttribute,
                            payload: {
                              modelIndex: modelIndex,
                              characteristicIndex: selectedCharacteristicIndex,
                              attributeIndex: index
                            }
                          });
                        }
                      }}
                    />
                  </StackItem>
                </Stack>
              )}
              {viewSection === "attribute" && (
                <Stack hasGutter={true}>
                  <StackItem>
                    <AttributeToolbar viewOverview={onViewOverviewView} viewAttributes={onViewAttributesView} />
                  </StackItem>
                  <StackItem className="characteristics-container__attribute">
                    <AttributeEditor
                      activeOperation={activeOperation}
                      setActiveOperation={setActiveOperation}
                      modelIndex={modelIndex}
                      characteristicIndex={selectedCharacteristicIndex}
                      attributeIndex={selectedAttributeIndex}
                      onCommit={(_index, _text, _partialScore, _reasonCode) => {
                        if (_index === undefined) {
                          dispatch({
                            type: Actions.Scorecard_AddAttribute,
                            payload: {
                              modelIndex: modelIndex,
                              characteristicIndex: selectedCharacteristicIndex,
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
                              characteristicIndex: selectedCharacteristicIndex,
                              attributeIndex: selectedAttributeIndex,
                              text: _text,
                              partialScore: _partialScore,
                              reasonCode: _reasonCode
                            }
                          });
                        }
                      }}
                      onCancel={() => null}
                    />
                  </StackItem>
                </Stack>
              )}
            </>
          </CSSTransition>
        </SwitchTransition>
      )}
    </div>
  );
};
