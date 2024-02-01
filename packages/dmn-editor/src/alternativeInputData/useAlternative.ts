import { State, useDmnEditorStore } from "../store/Store";

export function isAlternativeInputDataShape(state: State) {
  return (
    state.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[state.diagram.drdIndex][
      "@_useAlternativeInputDataShape"
    ] ?? false
  );
}

export function useAlternativeInputDataShape() {
  return useDmnEditorStore((s) => isAlternativeInputDataShape(s)); // reativamente mude conforme o valor.
  // getState() ... geralmente dentro de useCallbacks, nao precisa de mudança "reativa";
}
