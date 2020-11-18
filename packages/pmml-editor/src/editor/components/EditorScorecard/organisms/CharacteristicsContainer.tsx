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
import { AttributesToolbar, CharacteristicsToolbar } from "../molecules";

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

type CharacteristicsViewSection = "overview" | "attributes";

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

  const [selectedCharacteristic, setSelectedCharacteristic] = useState<IndexedCharacteristic | undefined>(undefined);
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
    } else {
      return "characteristics-container__attributes";
    }
  };

  const onAddAttribute = useCallback(() => {
    setActiveOperation(Operation.CREATE_ATTRIBUTE);
  }, [characteristics]);

  const onViewOverview = () => {
    setViewSection("overview");
    setActiveOperation(Operation.NONE);
  };

  const onViewAttributes = (index: number | undefined) => {
    if (index === undefined) {
      return;
    }
    setSelectedCharacteristic(characteristics[index]);
    setViewSection("attributes");
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
                    <AttributesToolbar
                      activeOperation={activeOperation}
                      onViewOverview={onViewOverview}
                      onAddAttribute={onAddAttribute}
                    />
                  </StackItem>
                  <StackItem>
                    <AttributesTable
                      modelIndex={modelIndex}
                      characteristic={selectedCharacteristic}
                      activeOperation={activeOperation}
                      setActiveOperation={setActiveOperation}
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
