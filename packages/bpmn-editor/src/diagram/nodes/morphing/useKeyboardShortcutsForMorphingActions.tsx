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

import * as React from "react";
import { useEffect } from "react";
import { MorphingAction } from "./MorphingAction";
import { useBpmnEditorStore } from "../../../store/StoreContext";

export function useKeyboardShortcutsForMorphingActions<A extends MorphingAction>(
  ref: React.RefObject<HTMLDivElement | SVGRectElement>,
  actions: A[],
  disabledActionIds: Set<A["id"]>
) {
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  useEffect(() => {
    if (isReadOnly) {
      return;
    }

    const currentRef = ref.current;
    const listeners = new Set<(e: KeyboardEvent) => void>();

    for (const action of actions) {
      const onKeyDown = (e: KeyboardEvent) => {
        if (
          e.target === currentRef &&
          !disabledActionIds.has(action.id) &&
          e.key === action.key &&
          e.metaKey === false &&
          e.altKey === false &&
          e.shiftKey === false
        ) {
          action.action();
          e.stopPropagation();
          e.preventDefault();
        }
      };
      listeners.add(onKeyDown);
      currentRef?.addEventListener("keydown", onKeyDown);
    }

    return () => {
      for (const listener of listeners) {
        currentRef?.removeEventListener("keydown", listener);
      }
    };
  }, [actions, disabledActionIds, isReadOnly, ref]);
}
