import * as React from "react";
import { useContext, useMemo } from "react";

export interface DiagramContainerContextType {
  container: React.RefObject<HTMLElement>;
}

const DiagramContainerContext = React.createContext<DiagramContainerContextType>({
  container: {
    current: null,
  },
});

export function useDmnEditorDiagramContainer() {
  return useContext(DiagramContainerContext);
}

export function DiagramContainerContextProvider({
  container,
  children,
}: React.PropsWithChildren<{
  container: React.RefObject<HTMLElement>;
}>) {
  const value = useMemo(
    () => ({
      container,
    }),
    [container]
  );

  return <DiagramContainerContext.Provider value={value}>{children}</DiagramContainerContext.Provider>;
}
