import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { ResizerStopBehavior } from "../../src/resizing/ResizingWidthsContext";
import { DmnBuiltInDataType, BeeTableHeaderVisibility, BeeTableProps } from "../../src/api";
import { StandaloneBeeTable } from "../../src/table/BeeTable";

const defaultProps: BeeTableProps<object> = {
  columns: [
    {
      accessor: "x",
      label: "y",
      isRowIndexColumn: false,
      dataType: DmnBuiltInDataType.Any,
      minWidth: 100,
      width: 100,
    },
  ],
  rows: [{ ["x"]: { content: "", id: "y" } }],
  allowedOperations: (conditions: any) => {
    return [];
  },
  resizerStopBehavior: ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER,
  shouldShowRowsInlineControls: false,
  shouldShowColumnsInlineControls: false,
  shouldRenderRowIndexColumn: false,
  headerVisibility: BeeTableHeaderVisibility.None,
  operationConfig: [],
};

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<typeof StandaloneBeeTable> = {
  title: "Components/Table/StandaloneBeeTable",
  component: StandaloneBeeTable,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/react/configure/story-layout
    layout: "center",
  },
  // This component will have an automatically generated Autodocs entry: https://storybook.js.org/docs/react/writing-docs/autodocs
  tags: ["autodocs"],
  // More on argTypes: https://storybook.js.org/docs/react/api/argtypes
};

export default meta;
type Story = StoryObj<typeof StandaloneBeeTable>;

function StandaloneBeeTableWrapper(props: BeeTableProps<object>) {
  const ref = useRef(null);

  return (
    <div ref={ref}>
      <StandaloneBeeTable {...props} scrollableParentRef={ref} />
    </div>
  );
}

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Default: Story = {
  render: (args) => StandaloneBeeTableWrapper(args),
  args: {
    ...defaultProps,
  },
};

export const ReadOnly: Story = {
  render: (args) => StandaloneBeeTableWrapper(args),
  args: {
    ...defaultProps,
    isReadOnly: true,
  },
};
