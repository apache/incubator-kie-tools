import * as React from "react";
import { useEffect, useState } from "react";
import { ResizingWidth } from "./ResizingWidthsContext";

export type NestedExpressionContainerContextType = {
  minWidth: number;
  actualWidth: number;
  resizingWidth: ResizingWidth;
};

export const NestedExpressionContainerContext = React.createContext<NestedExpressionContainerContextType>({
  minWidth: -2,
  actualWidth: -2,
  resizingWidth: {
    value: -2,
    isPivoting: false,
  },
});

export function useNestedExpressionContainer() {
  return React.useContext(NestedExpressionContainerContext);
}

// If isPivoting, return the last value, if not, return normal nestedExpressionContainer
export function usePivotAwareNestedExpressionContainer(isPivoting: boolean) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  const [pivotAwareExpressionContainer, setPivotAwareNestedExpressionContainer] = useState(nestedExpressionContainer);
  useEffect(() => {
    setPivotAwareNestedExpressionContainer((prev) => {
      return isPivoting ? prev : nestedExpressionContainer;
    });
  }, [isPivoting, nestedExpressionContainer]);

  return pivotAwareExpressionContainer;
}
