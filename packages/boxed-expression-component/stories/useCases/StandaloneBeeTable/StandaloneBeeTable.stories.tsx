import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { ResizerStopBehavior } from "../../../src/resizing/ResizingWidthsContext";
import { DmnBuiltInDataType, BeeTableHeaderVisibility, BeeTableProps } from "../../../src/api";
import { StandaloneBeeTable } from "../../../src/table/BeeTable";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<typeof StandaloneBeeTable> = {
  title: "Use cases/Standalone Bee Table",
  component: StandaloneBeeTable,
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
    ...defaultProps,
  },
};
