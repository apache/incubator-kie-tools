import * as React from "react";

interface CellProps {
  readOnly?: boolean;
  value?: string;
  emptyCell?: boolean;
}

export function Cell(props: CellProps) {
  return (
    <div>
      <p>{props.value}</p>
    </div>
  );
}
