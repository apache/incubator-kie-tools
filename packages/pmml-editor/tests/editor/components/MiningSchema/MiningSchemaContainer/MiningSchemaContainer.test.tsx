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
import { DataField, MiningField } from "@kie-tools/pmml-editor-marshaller";
import MiningSchemaContainer, {
  MiningSchemaContext,
} from "../../../../../src/editor/components/MiningSchema/MiningSchemaContainer/MiningSchemaContainer";

const onAddField = jest.fn((name) => {});
const onDeleteField = jest.fn((index) => {});
const onUpdateField = jest.fn((index, originalName, field) => {});

const dataFields: DataField[] = [
  {
    name: "field1",
    optype: "categorical",
    dataType: "string",
  },
  {
    name: "field2",
    optype: "categorical",
    dataType: "string",
  },
];

let miningFields: MiningField[];

beforeEach(() => {
  onAddField.mockReset();
  onDeleteField.mockReset();
  onUpdateField.mockReset();

  miningFields = [
    {
      name: "field1",
    },
    {
      name: "field2",
    },
  ];
});

describe("MiningSchemaContainer", () => {
  test("MiningField:Render", () => {
    const { getByTestId } = render(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteField}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );
    const container = getByTestId("mining-schema-container");
    expect(container).toMatchSnapshot();

    expect(getByTestId("mining-schema-field-n0")).not.toBeUndefined();
    expect(getByTestId("mining-schema-field-n1")).not.toBeUndefined();
  });

  test("MiningField:Render::WithNoDataFields", () => {
    const { getByTestId } = render(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: [] }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteField}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );
    const container = getByTestId("mining-schema-container");
    expect(container).toMatchSnapshot();

    expect(getByTestId("mining-schema-field-n0")).not.toBeUndefined();
    expect(getByTestId("mining-schema-field-n1")).not.toBeUndefined();
  });

  test("MiningField:DeleteWithIconClick", () => {
    const onDeleteFieldImpl = jest.fn((index) => {
      miningFields = miningFields.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    //Get the first MiningField and focus the row that will make the delete icon visible
    const miningField0 = getByTestId("mining-schema-field-n0");
    expect(miningField0).not.toBeUndefined();
    fireEvent.focus(miningField0, {});

    rerender(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    const miningField0deleteIcon = getByTestId("mining-schema-field-n0__delete");
    expect(miningField0deleteIcon).not.toBeUndefined();
    fireEvent.click(miningField0deleteIcon, {});

    expect(onDeleteFieldImpl).toBeCalledWith(0);

    rerender(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    expect(getByTestId("mining-schema-field-n0")).not.toBeUndefined();
    expect(queryByTestId("mining-schema-field-n1")).toBeNull();
  });

  test("MiningField:DeleteWithIconKeyDown", () => {
    const onDeleteFieldImpl = jest.fn((index) => {
      miningFields = miningFields.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    //Get the first MiningField and focus the row that will make the delete icon visible
    const miningField0 = getByTestId("mining-schema-field-n0");
    expect(miningField0).not.toBeUndefined();
    fireEvent.focus(miningField0, {});

    rerender(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    const miningField0deleteIcon = getByTestId("mining-schema-field-n0__delete");
    expect(miningField0deleteIcon).not.toBeUndefined();
    fireEvent.keyDown(miningField0deleteIcon, { key: "Enter" });

    expect(onDeleteFieldImpl).toBeCalledWith(0);

    rerender(
      <MiningSchemaContext.Provider value={-1}>
        <MiningSchemaContainer
          modelIndex={0}
          dataDictionary={{ DataField: dataFields }}
          miningSchema={{ MiningField: miningFields }}
          onAddField={onAddField}
          onDeleteField={onDeleteFieldImpl}
          onUpdateField={onUpdateField}
        />
      </MiningSchemaContext.Provider>
    );

    const miningField0Rendered = getByTestId("mining-schema-field-n0");
    expect(miningField0Rendered).not.toBeUndefined();
    expect(document.activeElement).toBe(miningField0Rendered);
    expect(queryByTestId("mining-schema-field-n1")).toBeNull();
  });
});
