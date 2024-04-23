/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { useEffect } from "react";
import { KeyboardShortcutsService } from "../../keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";

// This component is a wrapper. It memoizes the DmnEditorRoot props beforing rendering it.
export function useDmnEditorKeyboardShortcuts(keyboardShortcutsService: KeyboardShortcutsService | undefined) {
  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress(
      "Space",
      "Diagram | Reset position to origin",
      async () => {}
    );
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("b", "Diagram | Focus on node bounds", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("Escape", "Diagram | Cancel action", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("Ctrl+X", "Diagram | Cut", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("Ctrl+C", "Diagram | Copy", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("Ctrl+V", "Diagram | Paste", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("A", "Diagram | Select/Deselect all", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress(
      "Ctrl+A",
      "Diagram | Select/Deselect all",
      async () => {}
    );
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress(
      "G",
      "Diagram | Create group wrapping selection",
      async () => {}
    );
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress(
      "H",
      "Diagram | Toggle hierarchy highlights",
      async () => {}
    );
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("I", "Diagram | Show properties panel", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress("X", "Diagram | Hide from DRD", async () => {});
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);

  useEffect(() => {
    if (keyboardShortcutsService === undefined) {
      return;
    }
    const shortcut = keyboardShortcutsService.registerKeyPress(
      "Alt",
      "Diagram | Keep it pressed to move diagram",
      async () => {}
    );
    return () => {
      keyboardShortcutsService.deregister(shortcut);
    };
  }, [keyboardShortcutsService]);
}
