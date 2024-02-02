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

import { createContext, useContext } from "react";
import { StoreApi, UseBoundStore } from "zustand";
import { WithImmer } from "zustand/middleware/immer";
import { useStoreWithEqualityFn } from "zustand/traditional";
import { State } from "./Store";

type ExtractState = StoreApi<State> extends { getState: () => infer T } ? T : never;

export type StoreApiType = UseBoundStore<WithImmer<StoreApi<State>>>;

export const DmnEditorStoreApiContext = createContext<StoreApiType>({} as any);

export function useDmnEditorStore<StateSlice = ExtractState>(
  selector: (state: State) => StateSlice,
  equalityFn?: (a: StateSlice, b: StateSlice) => boolean
) {
  const store = useContext(DmnEditorStoreApiContext);

  if (store === null) {
    throw new Error("Can't use DMN Editor Store outside of the DmnEditor component.");
  }

  return useStoreWithEqualityFn(store, selector, equalityFn);
}

export function useDmnEditorStoreApi() {
  return useContext(DmnEditorStoreApiContext);
}
