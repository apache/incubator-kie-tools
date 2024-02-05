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
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";

export function useFocusableElement(
  ref: React.RefObject<HTMLInputElement>,
  id: string | undefined,
  before?: (cb: () => void) => void
) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const shoudFocus = useDmnEditorStore((s) => s.focus.consumableId === id);

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

    if (shoudFocus && ref.current) {
      before?.(cb) ?? cb();
    }
  }, [before, dmnEditorStoreApi, id, ref, shoudFocus]);
}
