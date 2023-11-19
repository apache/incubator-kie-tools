import { useEffect } from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";

export function useFocusableElement(
  ref: React.RefObject<HTMLInputElement>,
  id: string | undefined,
  before?: (cb: () => void) => void
) {
  const focus = useDmnEditorStore((s) => s.focus);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  useEffect(() => {
    if (!id) {
      return;
    }

    const cb = () => {
      ref.current?.select();

      dmnEditorStoreApi.setState((state) => {
        state.focus.consumableId = undefined;
      });
    };

    if (focus.consumableId === id && ref.current) {
      before?.(cb) ?? cb();
    }
  }, [before, dmnEditorStoreApi, focus.consumableId, id, ref]);
}
