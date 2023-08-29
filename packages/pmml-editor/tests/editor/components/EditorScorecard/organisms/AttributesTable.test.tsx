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
import { Attribute, Characteristic, PMML, Scorecard } from "@kie-tools/pmml-editor-marshaller";
import { Operation, OperationContext } from "../../../../../dist/editor/components/EditorScorecard";
import { AttributesTable } from "../../../../../src/editor/components/EditorScorecard/organisms";
import { Provider } from "react-redux";
import { createStore, Store } from "redux";
import { AllActions } from "../../../../../src/editor/reducers";

const viewAttribute = jest.fn((index) => {});
const deleteAttribute = jest.fn((index) => {});
const onCommit = jest.fn((index, partial) => {});

let attributes: Attribute[];
let characteristics: Characteristic[];
let pmml: PMML;
let scorecard: Scorecard;
let store: Store<PMML, AllActions>;

beforeEach(() => {
  viewAttribute.mockReset();
  deleteAttribute.mockReset();
  onCommit.mockReset();

  attributes = [{ reasonCode: "A1" }, { reasonCode: "A2" }];

  characteristics = [
    {
      name: "characteristic1",
      Attribute: attributes,
    },
  ];

  scorecard = {
    MiningSchema: { MiningField: [] },
    Characteristics: { Characteristic: characteristics },
    functionName: "regression",
  };
  pmml = { Header: {}, DataDictionary: { DataField: [] }, models: [scorecard], version: "1" };
  store = createStore((state) => state, pmml);
});

describe("AttributesTable", () => {
  test("Attribute:Render", () => {
    const { getByTestId } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttribute}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );
    const container = getByTestId("attributes-table");
    expect(container).toMatchSnapshot();

    expect(getByTestId("attribute-n0")).not.toBeUndefined();
    expect(getByTestId("attribute-n1")).not.toBeUndefined();
  });

  test("Attribute:DeleteWithIconClick", () => {
    const deleteAttributeImpl = jest.fn((index) => {
      scorecard.Characteristics.Characteristic[0].Attribute =
        scorecard.Characteristics.Characteristic[0].Attribute.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    //Get the first Attribute and focus the row that will make the delete icon visible
    const characteristic0 = getByTestId("attribute-n0");
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
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0deleteIcon = getByTestId("attribute-n0__delete");
    expect(characteristic0deleteIcon).not.toBeUndefined();
    fireEvent.click(characteristic0deleteIcon, {});

    expect(deleteAttributeImpl).toBeCalledWith(0);

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    expect(getByTestId("attribute-n0")).not.toBeUndefined();
    expect(queryByTestId("attribute-n1")).toBeNull();
  });

  test("Attribute:DeleteWithIconKeyDown", () => {
    const deleteAttributeImpl = jest.fn((index) => {
      scorecard.Characteristics.Characteristic[0].Attribute =
        scorecard.Characteristics.Characteristic[0].Attribute.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    //Get the first Attribute and focus the row that will make the delete icon visible
    const characteristic0 = getByTestId("attribute-n0");
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
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0deleteIcon = getByTestId("attribute-n0__delete");
    expect(characteristic0deleteIcon).not.toBeUndefined();
    fireEvent.keyDown(characteristic0deleteIcon, { key: "Enter" });

    expect(deleteAttributeImpl).toBeCalledWith(0);

    rerender(
      <Provider store={store}>
        <OperationContext.Provider
          value={{
            activeOperation: Operation.UPDATE_CHARACTERISTIC,
            setActiveOperation: (operation) => {},
          }}
        >
          <AttributesTable
            modelIndex={0}
            characteristicIndex={0}
            characteristic={characteristics[0]}
            areReasonCodesUsed={true}
            viewAttribute={viewAttribute}
            deleteAttribute={deleteAttributeImpl}
            onCommit={onCommit}
          />
        </OperationContext.Provider>
      </Provider>
    );

    const characteristic0Rendered = getByTestId("attribute-n0");
    expect(characteristic0Rendered).not.toBeUndefined();
    expect(document.activeElement).toBe(characteristic0Rendered);
    expect(queryByTestId("attribute-n1")).toBeNull();
  });
});
