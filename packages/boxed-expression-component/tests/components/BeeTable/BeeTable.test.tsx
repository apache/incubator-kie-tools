/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { fireEvent, render } from "@testing-library/react";
import * as _ from "lodash";
import * as React from "react";
import { act } from "react-dom/test-utils";
import * as ReactTable from "react-table";
import { PASTE_OPERATION } from "@kie-tools/boxed-expression-component/dist/components/BeeTable/common";
import {
  BeeGwtService,
  BeeTableColumnsUpdateArgs,
  DmnBuiltInDataType,
  BeeTableRowsUpdateArgs,
  BeeTableOperationHandlerConfig,
  BeeTableOperation,
} from "@kie-tools/boxed-expression-component/dist/api";

import {
  activateNameAndDataTypePopover,
  EDIT_EXPRESSION_DATA_TYPE,
  EDIT_EXPRESSION_NAME,
  flushPromises,
  updateElementViaPopover,
  usingTestingBoxedExpressionI18nContext,
  usingTestingBoxedExpressionProviderContext,
  wrapComponentInContext,
} from "../test-utils";
import { DEFAULT_MIN_WIDTH } from "@kie-tools/boxed-expression-component/dist/components/Resizer";
import { BeeTable } from "@kie-tools/boxed-expression-component/dist/components";

jest.useFakeTimers();

const EXPRESSION_COLUMN_HEADER = "[data-ouia-component-type='expression-column-header']";
const EXPRESSION_COLUMN_HEADER_CELL_INFO = "[data-ouia-component-type='expression-column-header-cell-info']";
const EXPRESSION_POPOVER_MENU = "[data-ouia-component-id='expression-popover-menu']";
const EXPRESSION_POPOVER_MENU_TITLE = "[data-ouia-component-id='expression-popover-menu-title']";
const EXPRESSION_TABLE_HANDLER_MENU = "[data-ouia-component-id='expression-table-handler-menu']";
const expressionRow = (index: number) => {
  return "[data-ouia-component-id='expression-row-" + index + "']";
};

const expressionCell = (rowIndex: number, columnIndex: number) => {
  return (
    "[data-ouia-component-id='expression-row-" +
    rowIndex +
    "'] [data-ouia-component-id='expression-column-" +
    columnIndex +
    "']"
  );
};

const operationHandlerMenuEntry = (menuEntry: string) => {
  return "[data-ouia-component-id='expression-table-handler-menu-" + menuEntry + "']";
};

const assertHeaderCell = (container: Element, expectedCells: number, content: string) => {
  expect(container.querySelector(".table-component table thead")).toBeTruthy();
  expect(container.querySelector(".table-component table thead tr")).toBeTruthy();
  expect(container.querySelectorAll(EXPRESSION_COLUMN_HEADER).length).toBe(expectedCells);
  expect(container.querySelectorAll(EXPRESSION_COLUMN_HEADER)[expectedCells - 1].innerHTML).toContain(content);
};

describe("Table tests", () => {
  const columnName = "column-1";
  const operationHandlerConfig: BeeTableOperationHandlerConfig = [];

  describe("when rendering it", () => {
    test("should show a table element", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      expect(container.querySelector(".table-component table")).toBeTruthy();
    });

    test("should show a table head with only one default column (#)", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      assertHeaderCell(container, 1, "#");
    });

    test("should show a table head with one configured column, rendering its label", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  accessor: columnName,
                  label: columnName,
                  dataType: DmnBuiltInDataType.Undefined,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      assertHeaderCell(container, 2, columnName);
    });

    test("should show a table head with one configured column, rendering its custom element", () => {
      const customElementContent = "Custom Element";
      const headerCellElement = <div>`${customElementContent}`</div>;

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  accessor: columnName,
                  headerCellElement,
                  dataType: DmnBuiltInDataType.Undefined,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      assertHeaderCell(container, 2, customElementContent);
    });

    test("should show a table body with no rows", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      expect(container.querySelector(".table-component table tbody")).toBeTruthy();
      expect(container.querySelector(".table-component table tbody tr")).toBeFalsy();
    });

    test("should show a table body with one configured row", () => {
      const row: ROWGENERICTYPE = {};
      const cellValue = "cell value";
      row[columnName] = cellValue;
      const rows: ROWGENERICTYPE[] = [row];

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[{ accessor: columnName, dataType: DmnBuiltInDataType.Undefined } as ReactTable.ColumnInstance]}
              rows={rows}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      expect(container.querySelector(".table-component table tbody")).toBeTruthy();
      expect(container.querySelector(expressionRow(0))).toBeTruthy();
      expect(container.querySelectorAll(expressionRow(0) + " td")).toHaveLength(2);
      expect(container.querySelector(expressionCell(0, 0))!.innerHTML).toContain("1");
      expect(container.querySelector(expressionCell(0, 1))!.innerHTML).toContain(cellValue);
    });
  });

  describe("when interacting with header", () => {
    test("should render popover with column name and dataType, when clicking on header cell", async () => {
      const editRelationLabel = "Edit Relation";
      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              editColumnLabel={editRelationLabel}
              columns={[
                {
                  label: columnName,
                  accessor: columnName,
                  dataType: DmnBuiltInDataType.Boolean,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      await activateNameAndDataTypePopover(
        container.querySelectorAll(EXPRESSION_COLUMN_HEADER_CELL_INFO)[0] as HTMLTableHeaderCellElement
      );

      expect(baseElement.querySelector(EXPRESSION_POPOVER_MENU)).toBeTruthy();
      expect(baseElement.querySelector(EXPRESSION_POPOVER_MENU_TITLE)?.innerHTML).toBe(editRelationLabel);
      expect((baseElement.querySelector(EDIT_EXPRESSION_NAME)! as HTMLInputElement).value).toBe(columnName);
      expect((baseElement.querySelector(EDIT_EXPRESSION_DATA_TYPE)! as HTMLSpanElement).textContent).toBe(
        DmnBuiltInDataType.Boolean
      );
    });

    test("should trigger onColumnUpdate, when changing column name via popover", async () => {
      const columnId = "column-id";
      const newColumnName = "changed";
      const onColumnUpdate = ({ columns }: BeeTableColumnsUpdateArgs) => {
        _.identity(columns);
      };
      const mockedOnColumnUpdate = jest.fn(onColumnUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  label: columnName,
                  accessor: columnId,
                  dataType: DmnBuiltInDataType.Boolean,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[]}
              onColumnsUpdate={mockedOnColumnUpdate}
              onRowsUpdate={_.identity}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );
      await updateElementViaPopover(
        container.querySelectorAll(EXPRESSION_COLUMN_HEADER_CELL_INFO)[0] as HTMLTableHeaderCellElement,
        baseElement,
        EDIT_EXPRESSION_NAME,
        newColumnName
      );

      expect(mockedOnColumnUpdate).toHaveBeenCalled();
      expect(mockedOnColumnUpdate).toHaveBeenCalledWith({
        columns: [
          {
            label: newColumnName,
            accessor: columnId,
            dataType: DmnBuiltInDataType.Boolean,
          } as ReactTable.ColumnInstance,
        ],
        columnIndex: 0,
      });
    });

    test("should trigger beeGwtService.selectObject, when clicking on header cell", async () => {
      const { beeGwtService, mockedSelectObject } = mockBeeGwtService();

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  label: columnName,
                  accessor: columnName,
                  dataType: DmnBuiltInDataType.Undefined,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[]}
            />,
            { beeGwtService }
          ).wrapper
        ).wrapper
      );

      fireEvent.click(container.querySelectorAll(EXPRESSION_COLUMN_HEADER_CELL_INFO)[0] as HTMLTableHeaderCellElement);

      expect(mockedSelectObject).toHaveBeenLastCalledWith(columnName);
    });
  });

  describe("when interacting with body", () => {
    test("should trigger onRowsUpdate, when changing cell value", async () => {
      const row: ROWGENERICTYPE = {};
      const newRow: ROWGENERICTYPE = {};
      const rowValue = "value";
      const newRowValue = "new value";
      row[columnName] = rowValue;
      newRow[columnName] = newRowValue;
      const columns = [
        { label: columnName, accessor: columnName, dataType: DmnBuiltInDataType.Boolean } as ReactTable.ColumnInstance,
      ];

      row[columnName] = "value";
      const orRowsUpdate = ({ rows }: BeeTableRowsUpdateArgs) => {
        _.identity(rows);
      };
      const mockedOnRowsUpdate = jest.fn(orRowsUpdate);

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={columns}
              rows={[row]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={mockedOnRowsUpdate}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      const textarea = container.querySelector("table tbody tr td textarea") as HTMLTextAreaElement;

      fireEvent.change(textarea, { target: { value: `${newRowValue}` } });
      // onblur is triggered by Monaco (mock), and the new value relies on Monaco implementation

      expect(mockedOnRowsUpdate).toHaveBeenCalled();
      expect(mockedOnRowsUpdate).toHaveBeenCalledWith({
        rows: [expect.objectContaining(newRow)],
        columns,
      });
    });

    test("should trigger beeGwtService.selectObject, when clicking on body cell", async () => {
      const { beeGwtService, mockedSelectObject } = mockBeeGwtService();
      const id = "row-id";

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  label: columnName,
                  accessor: columnName,
                  dataType: DmnBuiltInDataType.Undefined,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[{ id, columnName: "" }]}
            />,
            { beeGwtService }
          ).wrapper
        ).wrapper
      );

      fireEvent.click(container.querySelector("table tbody tr td") as HTMLTableCellElement);

      expect(mockedSelectObject).toHaveBeenLastCalledWith(id);
    });
  });

  describe("when users paste a value", () => {
    test("should trigger onRowsUpdate", async () => {
      const orRowsUpdate = ({ rows }: BeeTableRowsUpdateArgs) => {
        _.identity(rows);
      };
      const mockedOnRowsUpdate = jest.fn(orRowsUpdate);

      const row: ROWGENERICTYPE = {};
      const newRow: ROWGENERICTYPE = {};
      const rowValue = "value";
      const newRowValue = "new value";
      row[columnName] = rowValue;
      newRow[columnName] = newRowValue;

      row[columnName] = "value";

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[
                {
                  label: columnName,
                  accessor: columnName,
                  dataType: DmnBuiltInDataType.Boolean,
                } as ReactTable.ColumnInstance,
              ]}
              rows={[row]}
              onColumnsUpdate={_.identity}
              onNewRow={() => ({})}
              onRowsUpdate={mockedOnRowsUpdate}
              operationHandlerConfig={operationHandlerConfig}
            />
          ).wrapper
        ).wrapper
      );

      container.querySelector(".boxed-expression-provider")?.dispatchEvent(customEvent(container));
      expect(mockedOnRowsUpdate).toHaveBeenCalled();
    });
  });

  describe("when interacting with context menu", () => {
    test("should trigger onColumnUpdate, when inserting a new column on the left", async () => {
      const firstColumn = {
        label: "column-2",
        accessor: "column-2",
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
      } as ReactTable.ColumnInstance;
      const secondColumn = {
        label: "column-3",
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
      } as ReactTable.ColumnInstance;
      const onColumnUpdate = ({ columns }: BeeTableColumnsUpdateArgs) => {
        _.identity(columns);
      };
      const mockedOnColumnUpdate = jest.fn(onColumnUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[firstColumn]}
              rows={[]}
              onColumnsUpdate={mockedOnColumnUpdate}
              onRowsUpdate={_.identity}
              operationHandlerConfig={[
                {
                  group: "COLUMNS",
                  items: [{ name: "Insert Column Left", type: BeeTableOperation.ColumnInsertLeft }],
                },
              ]}
            />
          ).wrapper
        ).wrapper
      );
      await openContextMenu(container.querySelectorAll(EXPRESSION_COLUMN_HEADER)[1]);
      await selectMenuEntryIfNotDisabled(baseElement, "Insert Column Left");

      expect(mockedOnColumnUpdate).toHaveBeenCalledWith({
        columns: [expect.objectContaining(secondColumn), firstColumn],
        operation: BeeTableOperation.ColumnInsertLeft,
        columnIndex: 0,
      });
    });

    test("should trigger onColumnUpdate, when inserting a new column on the right", async () => {
      const firstColumn = {
        label: "column-2",
        accessor: "column-2",
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
      } as ReactTable.ColumnInstance;
      const secondColumn = {
        label: "column-3",
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
      } as ReactTable.ColumnInstance;
      const onColumnUpdate = ({ columns }: BeeTableColumnsUpdateArgs) => {
        _.identity(columns);
      };
      const mockedOnColumnUpdate = jest.fn(onColumnUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={[firstColumn]}
              rows={[]}
              onColumnsUpdate={mockedOnColumnUpdate}
              onRowsUpdate={_.identity}
              operationHandlerConfig={[
                {
                  group: "COLUMNS",
                  items: [{ name: "Insert Column right", type: BeeTableOperation.ColumnInsertRight }],
                },
              ]}
            />
          ).wrapper
        ).wrapper
      );
      await openContextMenu(container.querySelectorAll(EXPRESSION_COLUMN_HEADER)[1]);
      await selectMenuEntryIfNotDisabled(baseElement, "Insert Column right");

      expect(mockedOnColumnUpdate).toHaveBeenCalledWith({
        columns: [firstColumn, expect.objectContaining(secondColumn)],
        operation: BeeTableOperation.ColumnInsertRight,
        columnIndex: 0,
      });
    });

    test("should trigger onColumnUpdate, when deleting a column", async () => {
      const firstColumn = {
        label: "column-1",
        accessor: "column-1",
        dataType: DmnBuiltInDataType.Undefined,
      } as ReactTable.ColumnInstance;
      const secondColumn = {
        label: "column-2",
        accessor: "column-2",
        dataType: DmnBuiltInDataType.Undefined,
      } as ReactTable.ColumnInstance;
      const onColumnUpdate = ({ columns }: BeeTableColumnsUpdateArgs) => {
        _.identity(columns);
      };
      const mockedOnColumnUpdate = jest.fn(onColumnUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          wrapComponentInContext(
            <BeeTable
              columns={[firstColumn, secondColumn]}
              rows={[]}
              onColumnsUpdate={mockedOnColumnUpdate}
              onRowsUpdate={_.identity}
              operationHandlerConfig={[
                {
                  group: "COLUMNS",
                  items: [{ name: "Delete", type: BeeTableOperation.ColumnDelete }],
                },
              ]}
            />
          )
        ).wrapper
      );
      await openContextMenu(container.querySelectorAll(EXPRESSION_COLUMN_HEADER)[1]);
      await selectMenuEntryIfNotDisabled(baseElement, "Delete");

      expect(mockedOnColumnUpdate).toHaveBeenCalledWith({
        columns: [secondColumn],
        operation: BeeTableOperation.ColumnDelete,
        columnIndex: 0,
      });
    });

    test("should not trigger onColumnUpdate, when deleting a row number column", async () => {
      const row: ROWGENERICTYPE = {};
      row["#"] = "1";
      row["column-1"] = "column-1 value";
      row["column-2"] = "column-2 value";
      const firstColumn = {
        label: "column-1",
        accessor: "column-1",
        dataType: DmnBuiltInDataType.Undefined,
      } as ReactTable.ColumnInstance;
      const secondColumn = {
        label: "column-2",
        accessor: "column-2",
        dataType: DmnBuiltInDataType.Undefined,
      } as ReactTable.ColumnInstance;
      const onColumnUpdate = ({ columns }: BeeTableColumnsUpdateArgs) => {
        _.identity(columns);
      };
      const mockedOnColumnUpdate = jest.fn(onColumnUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          wrapComponentInContext(
            <BeeTable
              columns={[firstColumn, secondColumn]}
              rows={[row]}
              onColumnsUpdate={mockedOnColumnUpdate}
              onRowsUpdate={_.identity}
              operationHandlerConfig={[
                {
                  group: "COLUMNS",
                  items: [{ name: "Delete", type: BeeTableOperation.ColumnDelete }],
                },
              ]}
            />
          )
        ).wrapper
      );
      await openContextMenu(container.querySelector(expressionCell(0, 0))!);
      await selectMenuEntryIfNotDisabled(baseElement, "Delete");

      expect(mockedOnColumnUpdate).toHaveBeenCalledTimes(0);
    });

    test("should trigger onRowsUpdate, when inserting a new row above", async () => {
      const row: ROWGENERICTYPE = {};
      row[columnName] = "value";
      const columns = [
        {
          label: columnName,
          accessor: columnName,
          dataType: DmnBuiltInDataType.Undefined,
        } as ReactTable.ColumnInstance,
      ];
      const onRowsUpdate = ({ rows }: BeeTableRowsUpdateArgs) => {
        _.identity(rows);
      };
      const mockedOnRowsUpdate = jest.fn(onRowsUpdate);

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={columns}
              rows={[row]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={mockedOnRowsUpdate}
              operationHandlerConfig={[
                {
                  group: "ROWS",
                  items: [{ name: "Insert row above", type: BeeTableOperation.RowInsertAbove }],
                },
              ]}
            />
          ).wrapper
        ).wrapper
      );

      await openContextMenu(container.querySelector(expressionCell(0, 1))!);
      await selectMenuEntryIfNotDisabled(baseElement, "Insert row above");

      expect(mockedOnRowsUpdate).toHaveBeenCalledWith({
        rows: [expect.objectContaining({ width: DEFAULT_MIN_WIDTH }), expect.objectContaining(row)],
        operation: BeeTableOperation.RowInsertAbove,
        rowIndex: 0,
        columns,
      });
    });

    test("should trigger onRowsUpdate, when inserting a new row below", async () => {
      const row: ROWGENERICTYPE = {};
      row[columnName] = "value";
      const onRowsUpdate = ({ rows }: BeeTableRowsUpdateArgs) => {
        _.identity(rows);
      };
      const mockedOnRowsUpdate = jest.fn(onRowsUpdate);
      const columns = [
        {
          label: columnName,
          accessor: columnName,
          dataType: DmnBuiltInDataType.Undefined,
        } as ReactTable.ColumnInstance,
      ];

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={columns}
              rows={[row]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={mockedOnRowsUpdate}
              operationHandlerConfig={[
                {
                  group: "ROWS",
                  items: [{ name: "Insert row below", type: BeeTableOperation.RowInsertBelow }],
                },
              ]}
            />
          ).wrapper
        ).wrapper
      );
      await openContextMenu(container.querySelector(expressionCell(0, 1))!);
      await selectMenuEntryIfNotDisabled(baseElement, "Insert row below");

      expect(mockedOnRowsUpdate).toHaveBeenCalledWith({
        rows: [expect.objectContaining(row), expect.objectContaining({ width: DEFAULT_MIN_WIDTH })],
        operation: BeeTableOperation.RowInsertBelow,
        rowIndex: 0,
        columns,
      });
    });

    test("should trigger onRowsUpdate, when deleting a row", async () => {
      const firstRow: ROWGENERICTYPE = {};
      const secondRow: ROWGENERICTYPE = {};
      firstRow[columnName] = "value";
      secondRow[columnName] = "another value";
      const onRowsUpdate = ({ rows }: BeeTableRowsUpdateArgs) => {
        _.identity(rows);
      };
      const mockedOnRowsUpdate = jest.fn(onRowsUpdate);
      const columns = [
        {
          label: columnName,
          accessor: columnName,
          dataType: DmnBuiltInDataType.Undefined,
        } as ReactTable.ColumnInstance,
      ];

      const { container, baseElement } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(
            <BeeTable
              columns={columns}
              rows={[firstRow, secondRow]}
              onColumnsUpdate={_.identity}
              onRowsUpdate={mockedOnRowsUpdate}
              operationHandlerConfig={[
                {
                  group: "ROWS",
                  items: [{ name: "Delete", type: BeeTableOperation.RowDelete }],
                },
              ]}
            />
          ).wrapper
        ).wrapper
      );
      await openContextMenu(container.querySelector(expressionCell(0, 1))!);
      await selectMenuEntryIfNotDisabled(baseElement, "Delete");

      expect(mockedOnRowsUpdate).toHaveBeenCalledWith({
        rows: [secondRow],
        operation: BeeTableOperation.RowDelete,
        rowIndex: 0,
        columns,
      });
    });
  });

  describe("when interacting with column name", () => {
    test("should trigger beeGwtService.notifyUserAction, when column name changed", async () => {
      const { beeGwtService, mockedNotifyUserAction } = mockBeeGwtService();

      const nameColumn = {
        label: columnName,
        accessor: columnName,
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
        inlineEditable: true,
      } as ReactTable.ColumnInstance;

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(<BeeTable columns={[nameColumn]} rows={[]} />, {
            beeGwtService,
          }).wrapper
        ).wrapper
      );

      const element = container.querySelectorAll("p.pf-u-text-truncate")[0] as HTMLElement;
      element.click();

      const input = changeValue(container, "new value");
      input.blur();

      expect(mockedNotifyUserAction).toHaveBeenCalled();
    });

    test("should not trigger beeGwtService.notifyUserAction, when column name is not changed", async () => {
      const { beeGwtService, mockedNotifyUserAction } = mockBeeGwtService();

      const nameColumn = {
        label: columnName,
        accessor: columnName,
        dataType: DmnBuiltInDataType.Undefined,
        width: DEFAULT_MIN_WIDTH,
        inlineEditable: true,
      } as ReactTable.ColumnInstance;

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          usingTestingBoxedExpressionProviderContext(<BeeTable columns={[nameColumn]} rows={[]} />, {
            beeGwtService,
          }).wrapper
        ).wrapper
      );

      const element = container.querySelectorAll("p.pf-u-text-truncate")[0] as HTMLElement;
      element.click();

      const input = changeValue(container, columnName);
      input.blur();

      expect(mockedNotifyUserAction).toHaveBeenCalledTimes(0);
    });
  });
});

async function selectMenuEntryIfNotDisabled(baseElement: Element, menuEntry: string) {
  await act(async () => {
    expect(baseElement.querySelector(EXPRESSION_TABLE_HANDLER_MENU)).toBeTruthy();
    const button: HTMLButtonElement = baseElement.querySelector(
      operationHandlerMenuEntry(menuEntry) + " button:not([disabled])"
    ) as HTMLButtonElement;
    if (button != null) {
      button.click();
    }
    await flushPromises();
    jest.runAllTimers();
  });
}

export async function openContextMenu(element: Element): Promise<void> {
  await act(async () => {
    fireEvent.contextMenu(element);
    await flushPromises();
    jest.runAllTimers();
  });
}

function customEvent(container: HTMLElement) {
  const eventId: string =
    _.first(
      [].slice
        .call(container.querySelector(".table-component")?.classList)
        .filter((c: string) => c.match(/table-event-/g))
    ) || "";

  return new CustomEvent(eventId, {
    detail: {
      x: 0,
      y: 0,
      pasteValue: "A\tA\tA\n",
      type: PASTE_OPERATION,
    },
  });
}

function changeValue(container: Element, newValue: string) {
  const inputElement = container.querySelector("input")!;
  fireEvent.change(inputElement, {
    target: { value: newValue },
  });
  return inputElement;
}

function mockBeeGwtService() {
  const mockedSelectObject: (uuid?: string | undefined) => void = jest.fn();
  const mockedNotifyUserAction: () => void = jest.fn();
  const beeGwtService = {
    broadcastFunctionExpressionDefinition: () => {},
    notifyUserAction: mockedNotifyUserAction,
    selectObject: mockedSelectObject,
  } as unknown as BeeGwtService;
  return { mockedSelectObject, mockedNotifyUserAction, beeGwtService };
}
