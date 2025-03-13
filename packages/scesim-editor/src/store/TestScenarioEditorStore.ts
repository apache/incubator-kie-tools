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

import { SceSimModel } from "@kie-tools/scesim-marshaller";
import { enableMapSet } from "immer";
import { create } from "zustand";
import { immer } from "zustand/middleware/immer";

import { SceSim__FactMappingType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { ExternalDmnsIndex } from "../TestScenarioEditor";
import { ComputedStateCache } from "./ComputedStateCache";
import { computeDmnDataObjects } from "./computed/computeDmnDataObjects";
import { computeTestScenarioDataObjects } from "./computed/computeTestScenarioDataObjects";
import { computeDataObjects } from "./computed/computeDataObjects";

enableMapSet(); // Necessary because `Computed` has a lot of Maps and Sets.

export enum TestScenarioEditorDock {
  CHEATSHEET,
  DATA_OBJECT,
  SETTINGS,
}

export enum TestScenarioEditorTab {
  BACKGROUND,
  SIMULATION,
}

export type TestScenarioAlert = {
  enabled: boolean;
  message?: string;
  variant: "success" | "danger" | "warning" | "info" | "default";
};

export type TestScenarioDataObject = {
  id: string;
  children?: TestScenarioDataObject[];
  className?: string;
  collectionGenericType?: string[];
  customBadgeContent?: string;
  expressionElements: string[];
  hasBadge?: boolean;
  name: string;
};

export type TestScenarioSelectedColumnMetaData = {
  factMapping: SceSim__FactMappingType;
  index: number;
  isBackground: boolean;
};

export interface State {
  computed: (s: State) => Computed;
  dispatch: (s: State) => Dispatch;
  navigation: {
    dock: {
      isOpen: boolean;
      selected: TestScenarioEditorDock;
    };
    tab: TestScenarioEditorTab;
  };
  scesim: { model: SceSimModel };
  table: {
    background: {
      selectedColumn: TestScenarioSelectedColumnMetaData | null;
    };
    simulation: {
      selectedColumn: TestScenarioSelectedColumnMetaData | null;
    };
  };
}

// Read this to understand why we need computed as part of the store.
// https://github.com/pmndrs/zustand/issues/132#issuecomment-1120467721
export type Computed = {
  getDataObjects(externalModelsByNamespace: ExternalDmnsIndex | undefined): TestScenarioDataObject[];
  getDmnDataObjects(externalModelsByNamespace: ExternalDmnsIndex | undefined): TestScenarioDataObject[];
  getTestScenarioDataObjects(): TestScenarioDataObject[];
};

export type Dispatch = {
  navigation: {
    reset: () => void;
  };
  table: {
    updateSelectedColumn: (columnMetadata: TestScenarioSelectedColumnMetaData | null) => void;
  };
};

export const defaultStaticState = (): Omit<State, "computed" | "dispatch" | "scesim"> => ({
  navigation: {
    dock: {
      isOpen: true,
      selected: TestScenarioEditorDock.DATA_OBJECT,
    },
    tab: TestScenarioEditorTab.SIMULATION,
  },
  table: {
    background: {
      selectedColumn: null,
    },
    simulation: {
      selectedColumn: null,
    },
  },
});

export function createTestScenarioEditorStore(model: SceSimModel, computedCache: ComputedStateCache<Computed>) {
  console.debug("[TestScenarioEditorStore] Creating store with above model and empty cache ", model);

  const { ...defaultState } = defaultStaticState();
  return create(
    immer<State>(() => ({
      ...defaultState,
      scesim: {
        model: model,
      },
      dispatch(state: State) {
        return {
          navigation: {
            reset: () => {
              state.navigation.tab = TestScenarioEditorTab.SIMULATION;
              state.navigation.dock.isOpen = true;
              state.navigation.dock.selected = TestScenarioEditorDock.DATA_OBJECT;
              state.table.background.selectedColumn = null;
              state.table.simulation.selectedColumn = null;
            },
          },
          table: {
            updateSelectedColumn: (columnMetadata: TestScenarioSelectedColumnMetaData) => {
              if (state.navigation.tab === TestScenarioEditorTab.BACKGROUND) {
                state.table.background.selectedColumn = columnMetadata;
              } else {
                state.table.simulation.selectedColumn = columnMetadata;
              }
            },
          },
        };
      },
      computed(state: State) {
        return {
          getDataObjects: (externalModelsByNamespace: ExternalDmnsIndex | undefined) =>
            computedCache.cached("getDataObjects", computeDataObjects, [
              state.computed(state).getTestScenarioDataObjects(),
              state.computed(state).getDmnDataObjects(externalModelsByNamespace),
              state.scesim.model.ScenarioSimulationModel.settings.type,
            ]),

          getDmnDataObjects: (externalModelsByNamespace: ExternalDmnsIndex | undefined) =>
            computedCache.cached("getDmnDataObjects", computeDmnDataObjects, [
              externalModelsByNamespace,
              state.scesim.model.ScenarioSimulationModel.settings,
            ]),

          getTestScenarioDataObjects: () =>
            computedCache.cachedData(
              "getTestScenarioDataObjects",
              computeTestScenarioDataObjects,
              [state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping],
              [state.scesim.model.ScenarioSimulationModel.settings.type]
            ),
        };
      },
    }))
  );
}
