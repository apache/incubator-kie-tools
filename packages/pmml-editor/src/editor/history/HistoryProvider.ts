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

interface HistoryStore {
  changes: Change[];
  index: number;
}

interface BatchEntry<M> {
  state: M;
  path: string | null;
  recipe: (draft: WritableDraft<M>) => void;
}

export class HistoryService {
  private pending: Array<BatchEntry<any>> = new Array<BatchEntry<any>>();

  private readonly history: HistoryStore = {
    changes: [],
    index: 0
  };

  public batch = <M>(state: M, path: string | null, recipe: (draft: WritableDraft<M>) => void): void => {
    this.pending.push({ state: state, path: path, recipe: recipe });
  };

  public commit = (state: PMML | undefined): PMML | undefined => {
    if (state === undefined) {
      return;
    }

    const newState = this.mutate(state, null, draft => {
      this.pending.forEach(be => {
        const segment = be.path === null ? draft : get(draft, be.path as string);
        be.recipe(segment as WritableDraft<any>);
      });
    });

    this.pending = new Array<BatchEntry<any>>();

    return newState;
  };

  public mutate = <M>(state: M, path: string | null, recipe: (draft: WritableDraft<M>) => void) => {
    if (this.history.index < this.history.changes.length) {
      this.history.changes = this.history.changes.slice(0, this.history.index);
    }

    const newState: M = produce(state, recipe, (patches, inversePatches) => {
      this.history.changes.push({ path: path, change: patches, reverse: inversePatches });
    });
    this.history.index = this.history.changes.length;
    return newState;
  };

  public undo = (state: PMML): PMML => {
    if (this.history.index > 0) {
      const change: Change = this.history.changes[--this.history.index];
      return this.apply(state, change.path, change.reverse);
    }

    return state;
  };

  public redo = (state: PMML): PMML => {
    if (this.history.index < this.history.changes.length) {
      const change: Change = this.history.changes[this.history.index++];
      return this.apply(state, change.path, change.change);
    }

    return state;
  };

  public index = (): number => {
    return this.history.index;
  };

  public changes = (): Change[] => {
    return this.history.changes;
  };

  private apply = (state: PMML, path: string | null, patch: any) => {
    if (path === null) {
      return applyPatches(state, patch);
    }
    const branch: any = get(state, path);
    const branchUndone: any = applyPatches(branch, patch);
    const newState: PMML = cloneDeep(state);
    return set(newState, path, branchUndone);
  };
}
