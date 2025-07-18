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
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { ResizerStopBehavior } from "../../../src/resizing/ResizingWidthsContext";
import { BeeTableHeaderVisibility, BeeTableProps, generateUuid } from "../../../src/api";
import { StandaloneBeeTable } from "../../../src/table/BeeTable";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<typeof StandaloneBeeTable> = {
  title: "Misc/Standalone Table",
  component: StandaloneBeeTable,
};

export default meta;
type Story = StoryObj<typeof StandaloneBeeTable>;

function StandaloneBeeTableWrapper(props: BeeTableProps<object>) {
  const emptyRef = useRef(null);

  return (
    <div>
      <StandaloneBeeTable {...props} scrollableParentRef={emptyRef} />
    </div>
  );
}

const baseTableProps: BeeTableProps<object> = {
  columns: [
    {
      accessor: "x",
      label: "y",
      isRowIndexColumn: false,
      minWidth: 100,
      width: 100,
    },
  ] as any,
  rows: [{ ["x"]: { content: "", id: "y" } }],
  allowedOperations: () => [],
  resizerStopBehavior: ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER,
  shouldShowRowsInlineControls: false,
  shouldShowColumnsInlineControls: false,
  shouldRenderRowIndexColumn: true,
  headerVisibility: BeeTableHeaderVisibility.AllLevels,
  operationConfig: [],
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => StandaloneBeeTableWrapper(args),
  args: {
    ...baseTableProps,
  },
};

const peopleTableProps: BeeTableProps<object> = {
  columns: [
    {
      accessor: "people",
      label: "People",
      isRowIndexColumn: false,
      columns: [
        {
          label: "Name",
          accessor: "name",
          isRowIndexColumn: false,
          width: 200,
          minWidth: 200,
        },
        {
          label: "Age",
          accessor: "age",
          isRowIndexColumn: false,
          width: 100,
          minWidth: 100,
        },
        {
          label: "Country",
          accessor: "country",
          isRowIndexColumn: false,
          width: 100,
          minWidth: 100,
        },
      ],
    },
  ] as any,
  rows: [
    {
      ["name"]: { id: generateUuid(), content: `Joao Ninguem` },
      ["age"]: { id: generateUuid(), content: "30" },
      ["country"]: { id: generateUuid(), content: `Brazil` },
    },
    {
      ["name"]: { id: generateUuid(), content: `John Doe` },
      ["age"]: { id: generateUuid(), content: "37" },
      ["country"]: { id: generateUuid(), content: `US` },
    },
    {
      ["name"]: { id: generateUuid(), content: `Jane Doe` },
      ["age"]: { id: generateUuid(), content: "32" },
      ["country"]: { id: generateUuid(), content: `Canada` },
    },
  ],
  allowedOperations: () => [],
  resizerStopBehavior: ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER,
  shouldShowRowsInlineControls: false,
  shouldShowColumnsInlineControls: false,
  shouldRenderRowIndexColumn: true,
  headerVisibility: BeeTableHeaderVisibility.AllLevels,
  operationConfig: [],
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const People: Story = {
  render: (args) => StandaloneBeeTableWrapper(args),
  args: {
    ...peopleTableProps,
  },
};
