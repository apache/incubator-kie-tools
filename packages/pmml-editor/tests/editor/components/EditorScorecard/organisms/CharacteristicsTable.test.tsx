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
import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { Characteristic, PMML } from "@kie-tools/pmml-editor-marshaller";
import { Operation, OperationContext } from "../../../../../dist/editor/components/EditorScorecard";
import {
  CharacteristicsTable,
  IndexedCharacteristic,
} from "../../../../../src/editor/components/EditorScorecard/organisms";
import { Provider } from "react-redux";
import { createStore, Store } from "redux";
import { AllActions } from "../../../../../src/editor/reducers";

const setSelectedCharacteristicIndex = jest.fn((index) => {});
const validateCharacteristicName = jest.fn((index, name) => true);
const viewAttribute = jest.fn((index) => {});
const deleteCharacteristic = jest.fn((index) => {});
const onAddAttribute = jest.fn(() => {});
const onCommitAndClose = jest.fn(() => {});
const onCommit = jest.fn((partial: Partial<Characteristic>) => {});
const onCancel = jest.fn(() => {});

let characteristics: IndexedCharacteristic[];
let pmml: PMML;
let store: Store<PMML, AllActions>;

beforeEach(() => {
  setSelectedCharacteristicIndex.mockReset();
  validateCharacteristicName.mockReset();
  viewAttribute.mockReset();
  deleteCharacteristic.mockReset();
  onAddAttribute.mockReset();
  onCommitAndClose.mockReset();
  onCommit.mockReset();
  onCancel.mockReset();

  pmml = { Header: {}, DataDictionary: { DataField: [] }, version: "1" };
  store = createStore((state) => state, pmml);

  characteristics = [
    {
      index: 0,
      characteristic: {
        name: "characteristic1",
        Attribute: [],
      },
    },
    {
      index: 1,
      characteristic: {
        name: "characteristic2",
        Attribute: [],
      },
    },
  ];
});

describe("CharacteristicsTable", () => {
  test("Characteristic:Render", () => {
    const { getByTestId } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristic}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );
    const container = getByTestId("characteristics-table");
    expect(container).toMatchSnapshot();

    expect(getByTestId("characteristic-n0")).not.toBeUndefined();
    expect(getByTestId("characteristic-n1")).not.toBeUndefined();
  });

  test("Characteristic:DeleteWithIconClick", () => {
    const deleteCharacteristicImpl = jest.fn((index) => {
      characteristics = characteristics.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    //Get the first Characteristic and focus the row that will make the delete icon visible
    const characteristic0 = getByTestId("characteristic-n0");
    expect(characteristic0).not.toBeUndefined();
    fireEvent.focus(characteristic0, {});

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0deleteIcon = getByTestId("characteristic-n0__delete");
    expect(characteristic0deleteIcon).not.toBeUndefined();
    fireEvent.click(characteristic0deleteIcon, {});

    expect(deleteCharacteristicImpl).toBeCalledWith(0);

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    expect(getByTestId("characteristic-n0")).not.toBeUndefined();
    expect(queryByTestId("characteristic-n1")).toBeNull();
  });

  test("Characteristic:DeleteWithIconKeyDown", () => {
    const deleteCharacteristicImpl = jest.fn((index) => {
      characteristics = characteristics.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    //Get the first Characteristic and focus the row that will make the delete icon visible
    const characteristic0 = getByTestId("characteristic-n0");
    expect(characteristic0).not.toBeUndefined();
    fireEvent.focus(characteristic0, {});

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0deleteIcon = getByTestId("characteristic-n0__delete");
    expect(characteristic0deleteIcon).not.toBeUndefined();
    fireEvent.keyDown(characteristic0deleteIcon, { key: "Enter" });

    expect(deleteCharacteristicImpl).toBeCalledWith(0);

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <CharacteristicsTable
            modelIndex={0}
            areReasonCodesUsed={false}
            scorecardBaselineScore={undefined}
            characteristics={characteristics}
            characteristicsUnfilteredLength={characteristics.length}
            selectedCharacteristicIndex={undefined}
            setSelectedCharacteristicIndex={setSelectedCharacteristicIndex}
            validateCharacteristicName={validateCharacteristicName}
            viewAttribute={viewAttribute}
            deleteCharacteristic={deleteCharacteristicImpl}
            onAddAttribute={onAddAttribute}
            onCommitAndClose={onCommitAndClose}
            onCommit={onCommit}
            onCancel={onCancel}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0Rendered = getByTestId("characteristic-n0");
    expect(characteristic0Rendered).not.toBeUndefined();
    expect(document.activeElement).toBe(characteristic0Rendered);
    expect(queryByTestId("characteristic-n1")).toBeNull();
  });
});
