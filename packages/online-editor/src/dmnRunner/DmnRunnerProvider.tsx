/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { PropsWithChildren, useCallback, useEffect, useMemo, useState, useReducer } from "react";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerDispatchContext, DmnRunnerStateContext } from "./DmnRunnerContext";
import { KieSandboxExtendedServicesModelPayload } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { DecisionResult, DmnSchema, InputRow, extractDifferences, DecisionResultMessage } from "@kie-tools/form-dmn";
import { useDmnRunnerPersistence } from "../dmnRunnerPersistence/DmnRunnerPersistenceHook";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { generateUuid } from "../dmnRunnerPersistence/DmnRunnerPersistenceService";
import {
  useDmnRunnerPersistenceDispatch,
  DmnRunnerPersistenceReducerActionType,
} from "../dmnRunnerPersistence/DmnRunnerPersistenceDispatchContext";
import cloneDeep from "lodash/cloneDeep";
import { UnitablesInputsConfigs } from "@kie-tools/unitables/dist/UnitablesTypes";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useOnlineI18n } from "../i18n";
import { EditorPageDockDrawerRef } from "../editor/EditorPageDockDrawer";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { diff } from "deep-object-diff";
import getObjectValueByPath from "lodash/get";

interface Props {
  isEditorReady?: boolean;
  workspaceFile: WorkspaceFile;
  dmnLanguageService?: DmnLanguageService;
  editorPageDock: EditorPageDockDrawerRef | undefined;
}

export interface DmnRunnerProviderState {
  error: boolean;
  isExpanded: boolean;
  currentInputIndex: number;
}

const initialState: DmnRunnerProviderState = {
  error: false,
  isExpanded: false,
  currentInputIndex: 0,
};

export enum DmnRunnerProviderActionType {
  DEFAULT,
  ADD_ROW,
  TOGGLE_EXPANDED,
}

interface DmnRunnerProviderActionToggleExpanded {
  type: DmnRunnerProviderActionType.TOGGLE_EXPANDED;
}

interface DmnRunnerProviderActionAddRow {
  type: DmnRunnerProviderActionType.ADD_ROW;
  newState: (previous: DmnRunnerProviderState) => void;
}

interface DmnRunnerProviderActionDefault {
  type: DmnRunnerProviderActionType.DEFAULT;
  newState: Partial<DmnRunnerProviderState>;
}

export type DmnRunnerProviderAction =
  | DmnRunnerProviderActionDefault
  | DmnRunnerProviderActionAddRow
  | DmnRunnerProviderActionToggleExpanded;

function dmnRunnerProviderReducer(dmnRunnerProvider: DmnRunnerProviderState, action: DmnRunnerProviderAction) {
  switch (action.type) {
    case DmnRunnerProviderActionType.ADD_ROW:
      action.newState(dmnRunnerProvider);
      return { ...dmnRunnerProvider, currentInputIndex: dmnRunnerProvider.currentInputIndex + 1 };
    case DmnRunnerProviderActionType.TOGGLE_EXPANDED:
      return { ...dmnRunnerProvider, isExpanded: !dmnRunnerProvider.isExpanded };
    default:
      return { ...dmnRunnerProvider, ...action.newState };
  }
}

interface DmnRunnerResults {
  results: Array<DecisionResult[] | undefined>;
  resultsDifference: Array<Array<object>>;
}

interface DmnRunnerResultsAction {
  newResults: Array<DecisionResult[] | undefined>;
}

const initialDmnRunnerResults: DmnRunnerResults = {
  results: [],
  resultsDifference: [[{}]],
};
function dmnRunnerResultsReducer(dmnRunnerResults: DmnRunnerResults, action: DmnRunnerResultsAction) {
  const differences = extractDifferences(action.newResults, dmnRunnerResults.results);
  return {
    results: [...action.newResults],
    resultsDifference: [...differences],
  };
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();

  // States that can be changed down in the tree with dmnRunnerDispatcher;
  const [{ currentInputIndex, error, isExpanded }, dmnRunnerDispatcher] = useReducer(
    dmnRunnerProviderReducer,
    initialState
  );

  // States that are in controll of the DmnRunnerProvider;
  const [canBeVisualized, setCanBeVisualized] = useState<boolean>(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [{ results, resultsDifference }, dmnRunnerResultsDispatcher] = useReducer(
    dmnRunnerResultsReducer,
    initialDmnRunnerResults
  );

  // CUSTOM HOOKs
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();
  const { updatePersistenceJsonDebouce, dmnRunnerPersistenceJson, dmnRunnerPersistenceJsonDispatcher } =
    useDmnRunnerPersistenceDispatch();
  useDmnRunnerPersistence(props.workspaceFile.workspaceId, props.workspaceFile.relativePath);
  const prevKieSandboxExtendedServicesStatus = usePrevious(extendedServices.status);

  const dmnRunnerInputs = useMemo(() => dmnRunnerPersistenceJson.inputs, [dmnRunnerPersistenceJson.inputs]);
  const dmnRunnerMode = useMemo(() => dmnRunnerPersistenceJson.configs.mode, [dmnRunnerPersistenceJson.configs.mode]);
  const dmnRunnerConfigInputs = useMemo(
    () => dmnRunnerPersistenceJson?.configs?.inputs,
    [dmnRunnerPersistenceJson?.configs?.inputs]
  );
  const status = useMemo(() => (isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE), [isExpanded]);

  const previousSchema = usePrevious(jsonSchema);

  useEffect(() => {
    if (props.isEditorReady) {
      setCanBeVisualized(true);
    } else {
      setCanBeVisualized(false);
    }
  }, [props.isEditorReady]);

  const preparePayload = useCallback(
    async (formData?: InputRow) => {
      const fileContent = await workspaces.getFileContent({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: props.workspaceFile.relativePath,
      });

      const decodedFileContent = decoder.decode(fileContent);
      const importedModelsResources =
        (await props.dmnLanguageService?.getAllImportedModelsResources([decodedFileContent])) ?? [];
      const dmnResources = [
        { content: decodedFileContent, relativePath: props.workspaceFile.relativePath },
        ...importedModelsResources,
      ].map((resource) => ({
        URI: resource.relativePath,
        content: resource.content ?? "",
      }));

      return {
        mainURI: props.workspaceFile.relativePath,
        resources: dmnResources,
        context: formData,
      } as KieSandboxExtendedServicesModelPayload;
    },
    [props.workspaceFile, workspaces, props.dmnLanguageService]
  );

  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      extendedServices.status === KieSandboxExtendedServicesStatus.STOPPED ||
      extendedServices.status === KieSandboxExtendedServicesStatus.NOT_RUNNING
    ) {
      dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { isExpanded: false } });
    }
  }, [prevKieSandboxExtendedServicesStatus, extendedServices.status, props.workspaceFile.extension]);

  // RESULTS
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        Promise.all(dmnRunnerInputs.map((inputs) => preparePayload(inputs)))
          .then((payloads) =>
            Promise.all(
              payloads.map((payload) => {
                if (canceled.get() || payload === undefined) {
                  return;
                }
                return extendedServices.client.result(payload);
              })
            )
          )
          .then((results) => {
            if (canceled.get()) {
              return;
            }

            const runnerResults: Array<DecisionResult[] | undefined> = [];
            for (const result of results) {
              if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
                dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { error: true } });
                break;
              }
              if (result) {
                runnerResults.push(result.decisionResults);
              }
            }
            dmnRunnerResultsDispatcher({ newResults: runnerResults });
          })
          .catch((err) => {
            dmnRunnerResultsDispatcher({ newResults: [] });
          });
      },
      [preparePayload, dmnRunnerDispatcher, dmnRunnerInputs, extendedServices.client]
    )
  );

  // Set execution tab on Problems panel;
  useEffect(() => {
    if (!props.editorPageDock) {
      return;
    }

    const decisionNameByDecisionId = results[currentInputIndex]?.reduce(
      (acc: Map<string, string>, decisionResult) => acc.set(decisionResult.decisionId, decisionResult.decisionName),
      new Map<string, string>()
    );

    const messagesBySourceId =
      results[currentInputIndex]?.reduce((acc, decisionResult) => {
        decisionResult.messages?.forEach((message) => {
          const messageEntry = acc.get(message.sourceId);
          if (!messageEntry) {
            acc.set(message.sourceId, [message]);
          } else {
            acc.set(message.sourceId, [...messageEntry, message]);
          }
        });
        return acc;
      }, new Map<string, DecisionResultMessage[]>()) ?? new Map<string, DecisionResultMessage[]>();

    const notifications: Notification[] = [...messagesBySourceId.entries()].flatMap(([sourceId, messages]) => {
      const path = decisionNameByDecisionId?.get(sourceId) ?? "";
      return messages.map((message: any) => ({
        type: "PROBLEM",
        path,
        severity: message.severity,
        message: `${message.messageType}: ${message.message}`,
      }));
    });

    props.editorPageDock?.setNotifications(i18n.terms.execution, "", notifications as any);
  }, [props.editorPageDock, i18n.terms.execution, results, currentInputIndex]);

  const setDmnRunnerPersistenceJson = useCallback(
    (args: {
      newInputsRow?: ((previousInputs: Array<InputRow>) => Array<InputRow>) | Array<InputRow>;
      newMode?: DmnRunnerMode;
      newConfigInputs?: (
        previousConfigInputs: UnitablesInputsConfigs
      ) => UnitablesInputsConfigs | UnitablesInputsConfigs;
    }) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = cloneDeep(previousDmnRunnerPersistenceJson);
          if (typeof args.newInputsRow === "function") {
            newDmnRunnerPersistenceJson.inputs = args.newInputsRow(previousDmnRunnerPersistenceJson.inputs);
          } else if (args.newInputsRow) {
            newDmnRunnerPersistenceJson.inputs = args.newInputsRow;
          }

          if (args.newMode) {
            newDmnRunnerPersistenceJson.configs.mode = args.newMode;
          }

          if (typeof args.newConfigInputs === "function") {
            newDmnRunnerPersistenceJson.configs.inputs = args.newConfigInputs(
              previousDmnRunnerPersistenceJson.configs.inputs
            );
          } else if (args.newConfigInputs) {
            newDmnRunnerPersistenceJson.configs.inputs = args.newConfigInputs;
          }
          return newDmnRunnerPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerInputs = useCallback(
    (newInputsRow: ((previousInputs: Array<InputRow>) => Array<InputRow>) | Array<InputRow>) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = cloneDeep(previousDmnRunnerPersistenceJson);
          if (typeof newInputsRow === "function") {
            newDmnRunnerPersistenceJson.inputs = newInputsRow(previousDmnRunnerPersistenceJson.inputs);
          } else {
            newDmnRunnerPersistenceJson.inputs = newInputsRow;
          }
          return newDmnRunnerPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerMode = useCallback(
    (newMode: DmnRunnerMode) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = cloneDeep(previousDmnRunnerPersistenceJson);
          newDmnRunnerPersistenceJson.configs.mode = newMode;
          return newDmnRunnerPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerConfigInputs = useCallback(
    (
      newConfigInputs: (previousConfigInputs: UnitablesInputsConfigs) => UnitablesInputsConfigs | UnitablesInputsConfigs
    ) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = cloneDeep(previousDmnRunnerPersistenceJson);
          if (typeof newConfigInputs === "function") {
            newDmnRunnerPersistenceJson.configs.inputs = newConfigInputs(
              previousDmnRunnerPersistenceJson.configs.inputs
            );
          } else {
            newDmnRunnerPersistenceJson.configs.inputs = newConfigInputs;
          }
          return newDmnRunnerPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const getDefaultValuesForInputs = useCallback((inputs: InputRow) => {
    return Object.entries(inputs).reduce(
      (acc, [key, value]) => {
        if (key === "id") {
          return acc;
        }
        if (typeof value === "string") {
          acc[key] = "";
        } else if (typeof value === "number") {
          acc[key] = null;
        } else if (typeof value === "boolean") {
          acc[key] = false;
        } else if (Array.isArray(value)) {
          acc[key] = [];
        } else if (typeof value === "object") {
          acc[key] = {};
        }
        return acc;
      },
      { id: generateUuid() } as Record<string, any>
    );
  }, []);

  const getRefField = useCallback((jsonSchema: DmnSchema, field: Record<string, any>): Record<string, any> => {
    const refPath = field.$ref.split("/").splice(1).join(".");
    const refField: Record<string, any> = getObjectValueByPath(jsonSchema, refPath);
    if (refField.$ref) {
      return getRefField(jsonSchema, refField);
    }
    return refField;
  }, []);

  const getDefaultValue = useCallback(
    (jsonSchema: DmnSchema, field: Record<string, any>) => {
      const fieldToCheck = field.$ref ? getRefField(jsonSchema, field) : field;
      if (fieldToCheck.type === "object") {
        return {};
      }
      if (fieldToCheck.type === "array") {
        return [];
      }
      if (fieldToCheck.type === "boolean") {
        return false;
      }
    },
    [getRefField]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (
          props.workspaceFile.extension !== "dmn" ||
          extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
        ) {
          dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { isExpanded: false } });
          return;
        }

        preparePayload()
          .then((payload) => {
            if (canceled.get() || payload === undefined) {
              return;
            }
            extendedServices.client.formSchema(payload).then((jsonSchema) => {
              setJsonSchema((previousJsonSchema) => {
                if (previousJsonSchema === undefined) {
                  return jsonSchema;
                }

                const propertiesDifference = diff(
                  getObjectValueByPath(previousJsonSchema, "definitions.InputSet.properties") ?? {},
                  getObjectValueByPath(jsonSchema, "definitions.InputSet.properties") ?? {}
                );

                if (!propertiesDifference) {
                  return jsonSchema;
                }

                const defaultInputValues = Object.entries(
                  getObjectValueByPath(jsonSchema, "definitions.InputSet.properties") ?? {}
                )?.reduce((acc, [key, field]: [string, Record<string, string>]) => {
                  const defaultValue = getDefaultValue(jsonSchema, field);
                  if (defaultValue) {
                    acc[key] = defaultValue;
                  }
                  return acc;
                }, {} as Record<string, any>);

                setDmnRunnerPersistenceJson({
                  newConfigInputs: (previousConfigInputs) => {
                    const newConfigs = cloneDeep(previousConfigInputs);
                    return Object.entries(propertiesDifference).reduce(
                      (configs, [property, value]) => {
                        if (value?.format) {
                          delete configs[property];
                        }
                        return configs;
                      },
                      { ...newConfigs }
                    );
                  },
                  newInputsRow: (previousInputs) => {
                    const newInputs = cloneDeep(previousInputs);
                    const updateInputs = newInputs.map((inputs) => {
                      return Object.entries(propertiesDifference).reduce(
                        (inputs, [property, value]) => {
                          if (Object.keys(inputs).length === 0) {
                            return inputs;
                          }

                          if (!value || value.type || value.$ref) {
                            delete inputs[property];
                          }
                          if (value?.format) {
                            inputs[property] = undefined;
                          }
                          return inputs;
                        },
                        { ...defaultInputValues, ...inputs }
                      );
                    });
                    return updateInputs;
                  },
                });

                return jsonSchema;
              });
            });
          })
          .catch((err) => {
            console.error(err);
            dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { error: true } });
          });
      },
      [
        extendedServices.status,
        extendedServices.client,
        props.workspaceFile.extension,
        preparePayload,
        setDmnRunnerPersistenceJson,
      ]
    )
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = cloneDeep(previousPersistenceJson);
          const index = args.beforeIndex === 0 ? 0 : args.beforeIndex - 1;

          // add default value;
          const newInputsRow = getDefaultValuesForInputs(newPersistenceJson.inputs[index]);
          newPersistenceJson.inputs.splice(args.beforeIndex, 0, newInputsRow);
          return newPersistenceJson;
        },
      });
      dmnRunnerDispatcher({
        type: DmnRunnerProviderActionType.DEFAULT,
        newState: { currentInputIndex: args.beforeIndex },
      });
    },
    [
      getDefaultValuesForInputs,
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = cloneDeep(previousPersistenceJson);
          // duplicate inputs
          newPersistenceJson.inputs.splice(args.rowIndex, 0, {
            ...JSON.parse(JSON.stringify(previousPersistenceJson.inputs[args.rowIndex])),
            id: generateUuid(),
          });
          return newPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = cloneDeep(previousPersistenceJson);
          // reset to defaul values;
          const resetedInputRows = getDefaultValuesForInputs(newPersistenceJson.inputs[args.rowIndex]);
          newPersistenceJson.inputs[args.rowIndex] = resetedInputRows;
          return newPersistenceJson;
        },
      });
    },
    [
      getDefaultValuesForInputs,
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = cloneDeep(previousPersistenceJson);
          // delete input row;
          newPersistenceJson.inputs.splice(args.rowIndex, 1);
          // re-generate ids for rows above the deleted one
          newPersistenceJson.inputs.forEach((e, i, newInputRows) => {
            if (i >= args.rowIndex) {
              newInputRows[i] = { ...e, id: generateUuid() };
            }
          });
          return newPersistenceJson;
        },
      });
    },
    [
      updatePersistenceJsonDebouce,
      dmnRunnerPersistenceJsonDispatcher,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const dmnRunnerDispatch = useMemo(
    () => ({
      dmnRunnerDispatcher,
      onRowAdded,
      onRowDeleted,
      onRowDuplicated,
      onRowReset,
      setDmnRunnerConfigInputs,
      setDmnRunnerInputs,
      setDmnRunnerMode,
      setDmnRunnerPersistenceJson,
    }),
    [
      onRowAdded,
      onRowDeleted,
      onRowDuplicated,
      onRowReset,
      setDmnRunnerConfigInputs,
      setDmnRunnerInputs,
      setDmnRunnerMode,
      setDmnRunnerPersistenceJson,
    ]
  );

  const dmnRunnerState = useMemo(
    () => ({
      canBeVisualized,
      configs: dmnRunnerConfigInputs,
      currentInputIndex,
      dmnRunnerPersistenceJson,
      error,
      inputs: dmnRunnerInputs,
      isExpanded,
      jsonSchema,
      mode: dmnRunnerMode,
      results,
      resultsDifference,
      status,
    }),
    [
      canBeVisualized,
      currentInputIndex,
      dmnRunnerConfigInputs,
      dmnRunnerInputs,
      dmnRunnerMode,
      dmnRunnerPersistenceJson,
      error,
      isExpanded,
      jsonSchema,
      results,
      resultsDifference,
      status,
    ]
  );

  return (
    <>
      <DmnRunnerStateContext.Provider value={dmnRunnerState}>
        <DmnRunnerDispatchContext.Provider value={dmnRunnerDispatch}>
          {props.children}
        </DmnRunnerDispatchContext.Provider>
      </DmnRunnerStateContext.Provider>
    </>
  );
}
