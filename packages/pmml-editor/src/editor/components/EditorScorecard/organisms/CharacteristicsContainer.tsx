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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Operation } from "../Operation";
import { CharacteristicsTable, IndexedCharacteristic } from "./CharacteristicsTable";
import "./CharacteristicsContainer.scss";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { Actions } from "../../../reducers";
import {
  AttributeEditor,
  AttributeToolbar,
  CharacteristicsToolbar,
  EmptyStateNoCharacteristics,
  EmptyStateNoMatchingCharacteristics,
} from "../molecules";
import { Characteristic } from "@kie-tools/pmml-editor-marshaller";
import { isEqual } from "lodash";
import { findIncrementalName } from "../../../PMMLModelHelper";
import { useBatchDispatch, useHistoryService } from "../../../history";
import { useOperation } from "../OperationContext";
import { fromText } from "./PredicateConverter";
import set = Reflect.set;
import get = Reflect.get;

interface CharacteristicsContainerProps {
  modelIndex: number;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
  characteristics: Characteristic[];
}

type CharacteristicsViewSection = "overview" | "attribute";

export const CharacteristicsContainer = (props: CharacteristicsContainerProps) => {
  const { modelIndex, areReasonCodesUsed, scorecardBaselineScore, characteristics } = props;

  const { setActiveOperation } = useOperation();
  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const [filter, setFilter] = useState("");
  const [filteredCharacteristics, setFilteredCharacteristics] = useState<IndexedCharacteristic[]>([]);
  const [selectedCharacteristicIndex, setSelectedCharacteristicIndex] = useState<number | undefined>(undefined);
  const [selectedAttributeIndex, setSelectedAttributeIndex] = useState<number | undefined>(undefined);
  const [viewSection, setViewSection] = useState<CharacteristicsViewSection>("overview");

  useEffect(() => applyFilter(), [modelIndex, characteristics]);

  const setLowercaseTrimmedFilter = (_filter: string) => {
    setFilter(_filter.toLowerCase().trim());
  };

  const applyFilter = () => {
    const _filteredCharacteristics = characteristics
      ?.map<IndexedCharacteristic>(
        (_characteristic, index) => ({ index: index, characteristic: _characteristic } as IndexedCharacteristic)
      )
      .filter((ic) => {
        const _characteristicName = ic.characteristic.name;
        return _characteristicName?.toLowerCase().includes(filter);
      });
    setFilteredCharacteristics(_filteredCharacteristics ?? []);
  };

  const getTransition = (_viewSection: CharacteristicsViewSection) => {
    if (_viewSection === "overview") {
      return "characteristics-container__overview";
    } else {
      return "characteristics-container__attribute";
    }
  };

  const onViewOverviewView = () => {
    setActiveOperation(Operation.UPDATE_CHARACTERISTIC);
    setViewSection("overview");
  };

  const onViewAttribute = (index: number | undefined) => {
    if (index === undefined) {
      return;
    }
    setSelectedAttributeIndex(index);
    setViewSection("attribute");
  };

  const validateCharacteristicName = useCallback(
    (editIndex: number | undefined, name: string): boolean => {
      if (name === undefined || name.trim() === "") {
        return false;
      }
      const matching = characteristics.filter((c, index) => editIndex !== index && c.name === name);
      return matching.length === 0;
    },
    [characteristics]
  );

  const onAddCharacteristic = useCallback(() => {
    const numberOfCharacteristics = characteristics?.length;
    if (numberOfCharacteristics !== undefined) {
      //Index of the new row is equal to the number of existing rows
      setSelectedCharacteristicIndex(numberOfCharacteristics);

      const existingNames: string[] = characteristics.map((c) => c.name ?? "");
      const newCharacteristicName = findIncrementalName("New characteristic", existingNames, 1);

      dispatch({
        type: Actions.Scorecard_AddCharacteristic,
        payload: {
          modelIndex: modelIndex,
          name: newCharacteristicName,
          baselineScore: undefined,
          reasonCode: undefined,
          Attribute: [],
        },
      });

      setActiveOperation(Operation.UPDATE_CHARACTERISTIC);

      //Clear filter to ensure new Characteristic is visible
      setLowercaseTrimmedFilter("");
    }
  }, [characteristics]);

  const deleteCharacteristic = useCallback(
    (characteristicIndex: number) => {
      //See https://issues.redhat.com/browse/FAI-443
      //if (window.confirm(`Delete Characteristic "${characteristics?.[characteristicIndex].name}"?`)) {
      dispatch({
        type: Actions.Scorecard_DeleteCharacteristic,
        payload: {
          modelIndex: modelIndex,
          characteristicIndex: characteristicIndex,
        },
      });
      // }
    },
    [characteristics]
  );

  const onAddAttribute = useCallback(() => {
    if (selectedCharacteristicIndex === undefined) {
      return;
    }
    const numberOfAttributes = characteristics[selectedCharacteristicIndex].Attribute.length;
    //Index of the new row is equal to the number of existing rows
    setSelectedAttributeIndex(numberOfAttributes);
    dispatch({
      type: Actions.Scorecard_AddAttribute,
      payload: {
        modelIndex: modelIndex,
        characteristicIndex: selectedCharacteristicIndex,
        predicate: fromText("True"),
        partialScore: undefined,
        reasonCode: undefined,
      },
    });
    setActiveOperation(Operation.UPDATE_ATTRIBUTE);
    setViewSection("attribute");
  }, [characteristics, selectedCharacteristicIndex]);

  const onCommitAndClose = () => {
    onCommit({});
    onCancel();
  };

  const onCommit = useCallback(
    (partial: Partial<Characteristic>) => {
      if (selectedCharacteristicIndex === undefined) {
        return;
      }
      const characteristic = characteristics[selectedCharacteristicIndex];
      const existingPartial: Partial<Characteristic> = {};
      Object.keys(partial).forEach((key) => set(existingPartial, key, get(characteristic, key)));

      if (!isEqual(partial, existingPartial)) {
        dispatch({
          type: Actions.Scorecard_UpdateCharacteristic,
          payload: {
            modelIndex: modelIndex,
            characteristicIndex: selectedCharacteristicIndex,
            ...characteristic,
            ...partial,
          },
        });
      }
    },
    [characteristics, selectedCharacteristicIndex]
  );

  const onCancel = () => {
    setSelectedCharacteristicIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  const onUpdateAttribute = useCallback(
    (_index, _content) => {
      if (_index === undefined) {
        dispatch({
          type: Actions.Scorecard_AddAttribute,
          payload: {
            modelIndex: modelIndex,
            characteristicIndex: selectedCharacteristicIndex,
            predicate: _content.predicate,
            partialScore: _content.partialScore,
            reasonCode: _content.reasonCode,
          },
        });
      } else {
        dispatch({
          type: Actions.Scorecard_UpdateAttribute,
          payload: {
            modelIndex: modelIndex,
            characteristicIndex: selectedCharacteristicIndex,
            attributeIndex: selectedAttributeIndex,
            predicate: _content.predicate,
            partialScore: _content.partialScore,
            reasonCode: _content.reasonCode,
          },
        });
      }
    },
    [modelIndex, selectedCharacteristicIndex, selectedAttributeIndex]
  );

  const emptyStateProvider = useMemo(() => {
    if (characteristics.length === 0) {
      return <EmptyStateNoCharacteristics addCharacteristic={onAddCharacteristic} />;
    } else {
      return (
        <Stack hasGutter={true}>
          <StackItem>
            <CharacteristicsToolbar
              filter={filter}
              setFilter={setLowercaseTrimmedFilter}
              onFilter={applyFilter}
              onAddCharacteristic={onAddCharacteristic}
            />
            <EmptyStateNoMatchingCharacteristics />
          </StackItem>
        </Stack>
      );
    }
  }, [filter, characteristics]);

  return (
    <div className="characteristics-container" data-ouia-component-id="characteristics">
      {filteredCharacteristics.length === 0 && emptyStateProvider}
      {filteredCharacteristics.length > 0 && (
        <SwitchTransition mode={"out-in"}>
          <CSSTransition
            timeout={{
              enter: 230,
              exit: 100,
            }}
            classNames={getTransition(viewSection)}
            key={viewSection}
          >
            <>
              {viewSection === "overview" && (
                <Stack>
                  <StackItem>
                    <CharacteristicsToolbar
                      filter={filter}
                      setFilter={setLowercaseTrimmedFilter}
                      onFilter={applyFilter}
                      onAddCharacteristic={onAddCharacteristic}
                    />
                  </StackItem>
                  <StackItem className="characteristics-container__overview">
                    <CharacteristicsTable
                      modelIndex={modelIndex}
                      areReasonCodesUsed={areReasonCodesUsed}
                      scorecardBaselineScore={scorecardBaselineScore}
                      characteristics={filteredCharacteristics}
                      characteristicsUnfilteredLength={characteristics.length}
                      selectedCharacteristicIndex={selectedCharacteristicIndex}
                      setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
                      validateCharacteristicName={validateCharacteristicName}
                      viewAttribute={onViewAttribute}
                      deleteCharacteristic={deleteCharacteristic}
                      onAddAttribute={onAddAttribute}
                      onCommitAndClose={onCommitAndClose}
                      onCommit={onCommit}
                      onCancel={onCancel}
                    />
                  </StackItem>
                </Stack>
              )}
              {viewSection === "attribute" && (
                <Stack hasGutter={true}>
                  <StackItem>
                    <AttributeToolbar viewOverview={onViewOverviewView} />
                  </StackItem>
                  <StackItem className="characteristics-container__attribute">
                    <AttributeEditor
                      modelIndex={modelIndex}
                      characteristicIndex={selectedCharacteristicIndex}
                      attributeIndex={selectedAttributeIndex}
                      areReasonCodesUsed={areReasonCodesUsed}
                      onCancel={onViewOverviewView}
                      onCommit={onUpdateAttribute}
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
