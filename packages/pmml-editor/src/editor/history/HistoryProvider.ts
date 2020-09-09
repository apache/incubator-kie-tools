/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { applyPatches, produce } from "immer";
import { WritableDraft } from "immer/dist/types/types-external";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller/dist/marshaller/model";
import { cloneDeep, get, set } from "lodash";

interface Change {
  path: string | null;
  change: any;
  reverse: any;
}

interface History {
  changes: Change[];
  index: number;
}

const history: History = {
  changes: [],
  index: 0
};

const mutate = <M>(state: M, path: string | null, recipe: (draft: WritableDraft<M>) => void) => {
  console.log(path);
  if (history.index < history.changes.length) {
    history.changes = history.changes.slice(0, history.index);
  }

  const newState: M = produce(state, recipe, (patches, inversePatches) => {
    history.changes.push({ path: path, change: patches, reverse: inversePatches });
  });
  history.index = history.changes.length;
  return newState;
};

const undo = (state: PMML): PMML => {
  if (history.index > 0) {
    const change: Change = history.changes[--history.index];
    return apply(state, change.path, change.reverse);
  }

  return state;
};

const redo = (state: PMML): PMML => {
  if (history.index < history.changes.length) {
    const change: Change = history.changes[history.index++];
    return apply(state, change.path, change.change);
  }

  return state;
};

const apply = (state: PMML, path: string | null, patch: any) => {
  if (path === null) {
    return applyPatches(state, patch);
  }
  const branch: any = get(state, path);
  const branchUndone: any = applyPatches(branch, patch);
  const newState: PMML = cloneDeep(state);
  return set(newState, path, branchUndone);
};

export { mutate, undo, redo, history };
