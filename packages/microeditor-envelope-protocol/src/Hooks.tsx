import { useEffect } from "react";
import { getChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBusOuterMessageHandler } from "./EnvelopeBusOuterMessageHandler";

export function useSyncedKeyboardEvents(envelopeBusOuterMessageHandler: EnvelopeBusOuterMessageHandler) {
  useEffect(() => {
    const listener = (keyboardEvent: KeyboardEvent) => {
      const keyboardShortcut = getChannelKeyboardEvent(keyboardEvent);
      console.debug(`New keyboard event (${JSON.stringify(keyboardShortcut)})!`);
      envelopeBusOuterMessageHandler.notify_channelKeyboardEvent(keyboardShortcut);
    };

    window.addEventListener("keydown", listener);
    window.addEventListener("keyup", listener);
    window.addEventListener("keypress", listener);
    return () => {
      window.removeEventListener("keydown", listener);
      window.removeEventListener("keyup", listener);
      window.removeEventListener("keypress", listener);
    };
  }, [envelopeBusOuterMessageHandler]);
}
