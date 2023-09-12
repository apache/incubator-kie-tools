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
import DataDictionaryContainer, {
  DDDataField,
} from "../../../../../src/editor/components/DataDictionary/DataDictionaryContainer/DataDictionaryContainer";
import { ValidationContext, ValidationRegistry } from "../../../../../src/editor/validation";

const onAdd = jest.fn((name, type, optype) => {});
const onEdit = jest.fn((index, originalName, field) => {});
const onDelete = jest.fn((index) => {});
const onReorder = jest.fn((oldIndex, newIndex) => {});
const onBatchAdd = jest.fn((fields) => {});
const onEditingPhaseChange = jest.fn((status) => {});

let dataFields: DDDataField[];

beforeEach(() => {
  onAdd.mockReset();
  onEdit.mockReset();
  onDelete.mockReset();
  onReorder.mockReset();
  onBatchAdd.mockReset();
  onEditingPhaseChange.mockReset();

  dataFields = [
    {
      name: "field1",
      optype: "categorical",
      type: "string",
    },
    {
      name: "field2",
      optype: "categorical",
      type: "string",
    },
  ];
});

describe("DataDictionaryContainer", () => {
  test("DataField:Render", () => {
    const { getByTestId } = render(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDelete}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );
    const container = getByTestId("data-dictionary-container");
    expect(container).toMatchSnapshot();

    expect(getByTestId("data-type-item-n0")).not.toBeUndefined();
    expect(getByTestId("data-type-item-n1")).not.toBeUndefined();
  });

  test("DataField:DeleteWithIconClick", () => {
    const onDeleteImpl = jest.fn((index) => {
      dataFields = dataFields.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    //Get the first DataField and focus the row that will make the delete icon visible
    const dataField0 = getByTestId("data-type-item-n0");
    expect(dataField0).not.toBeUndefined();
    fireEvent.focus(dataField0, {});

    rerender(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    const dataField0deleteIcon = getByTestId("data-type-item-n0__delete");
    expect(dataField0deleteIcon).not.toBeUndefined();
    fireEvent.click(dataField0deleteIcon, {});

    expect(onDeleteImpl).toBeCalledWith(0);

    rerender(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    expect(getByTestId("data-type-item-n0")).not.toBeUndefined();
    expect(queryByTestId("data-type-item-n1")).toBeNull();
  });

  test("DataField:DeleteWithIconKeyDown", () => {
    const onDeleteImpl = jest.fn((index) => {
      dataFields = dataFields.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    //Get the first DataField and focus the row that will make the delete icon visible
    const dataField0 = getByTestId("data-type-item-n0");
    expect(dataField0).not.toBeUndefined();
    fireEvent.focus(dataField0, {});

    rerender(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    const dataField0deleteIcon = getByTestId("data-type-item-n0__delete");
    expect(dataField0deleteIcon).not.toBeUndefined();
    fireEvent.keyDown(dataField0deleteIcon, { key: "Enter" });

    expect(onDeleteImpl).toBeCalledWith(0);

    rerender(
      <ValidationContext.Provider
        value={{
          validationRegistry: new ValidationRegistry(),
        }}
      >
        <DataDictionaryContainer
          dataDictionary={dataFields}
          onAdd={onAdd}
          onEdit={onEdit}
          onDelete={onDeleteImpl}
          onReorder={onReorder}
          onBatchAdd={onBatchAdd}
          onEditingPhaseChange={onEditingPhaseChange}
        />
      </ValidationContext.Provider>
    );

    const dataField0Rendered = getByTestId("data-type-item-n0");
    expect(dataField0Rendered).not.toBeUndefined();
    expect(document.activeElement).toBe(dataField0Rendered);
    expect(queryByTestId("data-type-item-n1")).toBeNull();
  });
});
