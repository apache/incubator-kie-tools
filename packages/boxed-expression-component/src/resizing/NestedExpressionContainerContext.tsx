import * as React from "react";
import { ResizingWidth } from "./ResizingWidthsContext";

export type NestedExpressionContainerContextType = {
  minWidthLocal: number;
  minWidthGlobal: number;
  actualWidth: number;
  resizingWidth: ResizingWidth;
};

export const NestedExpressionContainerContext = React.createContext<NestedExpressionContainerContextType>({
  minWidthLocal: -2,
  minWidthGlobal: -2,
  actualWidth: -2,
  resizingWidth: {
    value: -2,
    isPivoting: false,
  },
});

export function useNestedExpressionContainer() {
  return React.useContext(NestedExpressionContainerContext);
}
