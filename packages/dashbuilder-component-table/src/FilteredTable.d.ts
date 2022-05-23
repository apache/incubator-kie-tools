export interface Alert {
  danger: string;
  good: string;
  great: string;
}
interface Props {
  columns: string[];
  rows: any[][];
  onRowSelected?: (i: number) => void;
  selectable?: boolean;
  alerts?: Map<number, Alert>;
}
export declare const FilteredTable: (props: Props) => JSX.Element;
export {};
