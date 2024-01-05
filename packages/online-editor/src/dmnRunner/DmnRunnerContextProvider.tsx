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
import {
  PropsWithChildren,
  useCallback,
  useEffect,
  useLayoutEffect,
  useMemo,
  useState,
  useReducer,
  useRef,
} from "react";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerDispatchContext, DmnRunnerStateContext } from "./DmnRunnerContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { InputRow } from "@kie-tools/form-dmn";
import {
  DecisionResult,
  ExtendedServicesDmnJsonSchema,
  DmnEvaluationMessages,
  ExtendedServicesModelPayload,
} from "@kie-tools/extended-services-api";
import { DmnRunnerAjv } from "@kie-tools/dmn-runner/dist/ajv";
import { SCHEMA_DRAFT4 } from "@kie-tools/dmn-runner/dist/constants";
import { useDmnRunnerPersistence } from "../dmnRunnerPersistence/DmnRunnerPersistenceHook";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { generateUuid } from "../dmnRunnerPersistence/DmnRunnerPersistenceService";
import { useDmnRunnerPersistenceDispatch } from "../dmnRunnerPersistence/DmnRunnerPersistenceDispatchContext";
import cloneDeep from "lodash/cloneDeep";
import { UnitablesInputsConfigs } from "@kie-tools/unitables/dist/UnitablesTypes";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useOnlineI18n } from "../i18n";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { useCompanionFsFileSyncedWithWorkspaceFile } from "../companionFs/CompanionFsHooks";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { DmnRunnerPersistenceReducerActionType } from "../dmnRunnerPersistence/DmnRunnerPersistenceTypes";
import { CompanionFsServiceBroadcastEvents } from "../companionFs/CompanionFsService";
import {
  DmnRunnerProviderAction,
  DmnRunnerProviderActionType,
  DmnRunnerProviderState,
  DmnRunnerResults,
  DmnRunnerResultsAction,
  DmnRunnerResultsActionType,
} from "./DmnRunnerTypes";
import { DmnRunnerDockToggle } from "./DmnRunnerDockToggle";
import { PanelId, useEditorDockContext } from "../editor/EditorPageDockContextProvider";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { diff } from "deep-object-diff";
import {
  resolveReferencesAndCheckForRecursion,
  removeChangedPropertiesAndAdditionalProperties,
  getDefaultValues,
} from "@kie-tools/dmn-runner/dist/jsonSchema";
import { extractDifferencesFromArray } from "@kie-tools/dmn-runner/dist/results";

interface Props {
  isEditorReady?: boolean;
  workspaceFile: WorkspaceFile;
  dmnLanguageService?: DmnLanguageService;
}

const initialDmnRunnerProviderStates: DmnRunnerProviderState = {
  isExpanded: false,
  currentInputIndex: 0,
};

function dmnRunnerContextProviderReducer(dmnRunnerProvider: DmnRunnerProviderState, action: DmnRunnerProviderAction) {
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

const initialDmnRunnerResults: DmnRunnerResults = {
  results: [],
  resultsDifference: [[{}]],
};

function dmnRunnerResultsReducer(dmnRunnerResults: DmnRunnerResults, action: DmnRunnerResultsAction) {
  if (action.type === DmnRunnerResultsActionType.CLONE_LAST) {
    return {
      results: [...dmnRunnerResults.results, dmnRunnerResults.results[dmnRunnerResults.results.length - 1]],
      resultsDifference: dmnRunnerResults.resultsDifference,
    };
  } else if (action.type === DmnRunnerResultsActionType.DEFAULT) {
    const differences = extractDifferencesFromArray(action.newResults ?? [], dmnRunnerResults.results);
    return {
      results: action.newResults ? [...action.newResults] : [],
      resultsDifference: [...differences],
    };
  } else {
    throw new Error("Invalid use of dmnRunnerResultDispatcher");
  }
}

export function DmnRunnerContextProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  // Calling forceDmnRunnerReRender will cause a update in the dmnRunnerKey
  // dmnRunnerKey should be placed in the Unitables and DmnForm;
  const [dmnRunnerKey, forceDmnRunnerReRender] = useReducer((x) => x + 1, 0);

  // States that can be changed down in the tree with dmnRunnerDispatcher;
  const [{ currentInputIndex, isExpanded }, setDmnRunnerContextProviderState] = useReducer(
    dmnRunnerContextProviderReducer,
    initialDmnRunnerProviderStates
  );

  // States that are in control of the DmnRunnerProvider;
  const [canBeVisualized, setCanBeVisualized] = useState<boolean>(false);
  const [extendedServicesError, setExtendedServicesError] = useState<boolean>(false);
  const [jsonSchema, setJsonSchema] = useState<ExtendedServicesDmnJsonSchema | undefined>(undefined);
  const [{ results, resultsDifference }, setDmnRunnerResults] = useReducer(
    dmnRunnerResultsReducer,
    initialDmnRunnerResults
  );

  // CUSTOM HOOKs
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();
  const {
    dmnRunnerPersistenceService,
    updatePersistenceJsonDebouce,
    dmnRunnerPersistenceJson,
    dmnRunnerPersistenceJsonDispatcher,
  } = useDmnRunnerPersistenceDispatch();
  useDmnRunnerPersistence(props.workspaceFile.workspaceId, props.workspaceFile.relativePath);
  const prevExtendedServicesStatus = usePrevious(extendedServices.status);
  const { panel, setNotifications, addToggleItem, removeToggleItem, onOpenPanel, onTogglePanel } =
    useEditorDockContext();

  const dmnRunnerInputs = useMemo(() => dmnRunnerPersistenceJson.inputs, [dmnRunnerPersistenceJson.inputs]);
  const dmnRunnerMode = useMemo(() => dmnRunnerPersistenceJson.configs.mode, [dmnRunnerPersistenceJson.configs.mode]);
  const dmnRunnerConfigInputs = useMemo(
    () => dmnRunnerPersistenceJson?.configs?.inputs,
    [dmnRunnerPersistenceJson?.configs?.inputs]
  );
  const status = useMemo(() => (isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE), [isExpanded]);
  const dmnRunnerAjv = useMemo(() => new DmnRunnerAjv().getAjv(), []);

  useLayoutEffect(() => {
    if (props.isEditorReady) {
      setCanBeVisualized(true);
    } else {
      setCanBeVisualized(false);
    }
  }, [props.isEditorReady]);

  useLayoutEffect(() => {
    setExtendedServicesError(false);
  }, [jsonSchema]);

  // Refer to effect responsible for getting decision results
  const hasJsonSchema = useRef<boolean>(false);
  // Reset JSON Schema;
  useLayoutEffect(() => {
    setJsonSchema(undefined);
    hasJsonSchema.current = false;
  }, [props.workspaceFile.relativePath]);

  useLayoutEffect(() => {
    if (jsonSchema) {
      hasJsonSchema.current = true;
    }
  }, [jsonSchema]);

  // Control the isExpaded state based on the extended services status;
  useLayoutEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      extendedServices.status === ExtendedServicesStatus.STOPPED ||
      extendedServices.status === ExtendedServicesStatus.NOT_RUNNING
    ) {
      setDmnRunnerContextProviderState({ type: DmnRunnerProviderActionType.DEFAULT, newState: { isExpanded: false } });
    }
  }, [prevExtendedServicesStatus, extendedServices.status, props.workspaceFile.extension]);

  const extendedServicesModelPayload = useCallback<(formInputs?: InputRow) => Promise<ExtendedServicesModelPayload>>(
    async (formInputs) => {
      const fileContent = await workspaces.getFileContent({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: props.workspaceFile.relativePath,
      });

      const decodedFileContent = decoder.decode(fileContent);
      const importIndex = await props.dmnLanguageService?.buildImportIndex([
        {
          content: decodedFileContent,
          normalizedPosixPathRelativeToTheWorkspaceRoot: props.workspaceFile.relativePath,
        },
      ]);

      return {
        context: formInputs,
        mainURI: props.workspaceFile.relativePath,
        resources: [...(importIndex?.models.entries() ?? [])].map(
          ([normalizedPosixPathRelativeToTheWorkspaceRoot, model]) => ({
            content: model.xml,
            URI: normalizedPosixPathRelativeToTheWorkspaceRoot,
          })
        ),
      };
    },
    [props.dmnLanguageService, props.workspaceFile.relativePath, props.workspaceFile.workspaceId, workspaces]
  );

  // Responsible for getting decision results
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (props.workspaceFile.extension !== "dmn" || extendedServices.status !== ExtendedServicesStatus.RUNNING) {
          return;
        }

        Promise.all(
          dmnRunnerInputs.map((dmnRunnerInput) => {
            // extendedServicesModelPayload triggers a re-run in this effect before the jsonSchema values is updated
            // in the useLayoutEffect, making this effect to be triggered with the previous file dmnRunnerInputs.
            const input = hasJsonSchema.current === false ? {} : dmnRunnerInput;
            return extendedServicesModelPayload(input);
          })
        )
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
                setExtendedServicesError(true);
                break;
              }
              if (result) {
                runnerResults.push(result.decisionResults);
              }
            }
            setDmnRunnerResults({ type: DmnRunnerResultsActionType.DEFAULT, newResults: runnerResults });
          })
          .catch((err) => {
            console.log(err);
            setDmnRunnerResults({ type: DmnRunnerResultsActionType.DEFAULT });
          });
      },
      [
        props.workspaceFile.extension,
        extendedServices.status,
        extendedServices.client,
        dmnRunnerInputs,
        extendedServicesModelPayload,
      ]
    )
  );

  // EditorDock drawer controller;
  useLayoutEffect(() => {
    if (dmnRunnerMode === DmnRunnerMode.TABLE) {
      addToggleItem(PanelId.DMN_RUNNER_TABLE, <DmnRunnerDockToggle key="dmn-runner-toggle-item" />);

      return () => {
        removeToggleItem(PanelId.DMN_RUNNER_TABLE);
      };
    }
  }, [addToggleItem, removeToggleItem, dmnRunnerMode]);

  useLayoutEffect(() => {
    if (dmnRunnerMode === DmnRunnerMode.FORM && panel === PanelId.DMN_RUNNER_TABLE) {
      onOpenPanel(PanelId.NONE);
    }
  }, [dmnRunnerMode, panel, onOpenPanel, onTogglePanel, isExpanded]);

  // BEGIN -
  // At the first render it should open the DMN Runner Table if runner is in Table mode and isExpanded = true
  // This effect will run everytime the file name is changed;
  const runEffect = useRef(true);
  useLayoutEffect(() => {
    if (panel !== PanelId.DMN_RUNNER_TABLE) {
      runEffect.current = true;
    }

    // it should exec only when the relativePath changes;
  }, [props.workspaceFile.relativePath]);

  useLayoutEffect(() => {
    if (runEffect.current) {
      if (panel !== PanelId.DMN_RUNNER_TABLE && dmnRunnerMode === DmnRunnerMode.TABLE && isExpanded) {
        onTogglePanel(PanelId.DMN_RUNNER_TABLE);
      }
      runEffect.current = false;
    }
  }, [dmnRunnerMode, isExpanded, onTogglePanel, panel]);
  // END

  // Set execution tab on Problems panel;
  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn" || extendedServices.status !== ExtendedServicesStatus.RUNNING) {
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
      }, new Map<string, DmnEvaluationMessages[]>()) ?? new Map<string, DmnEvaluationMessages[]>();

    const notifications: Notification[] = [...messagesBySourceId.entries()].flatMap(([sourceId, messages]) => {
      const path = decisionNameByDecisionId?.get(sourceId) ?? "";
      return messages.map((message: any) => ({
        type: "PROBLEM",
        normalizedPosixPathRelativeToTheWorkspaceRoot: path,
        severity: message.severity,
        message: `${message.messageType}: ${message.message}`,
      }));
    });

    setNotifications(i18n.terms.execution, "", notifications as any);
  }, [
    setNotifications,
    i18n.terms.execution,
    results,
    currentInputIndex,
    props.workspaceFile.extension,
    extendedServices.status,
  ]);

  const setDmnRunnerPersistenceJson = useCallback(
    (args: {
      newInputsRow?: ((previousInputs: Array<InputRow>) => Array<InputRow>) | Array<InputRow>;
      newMode?: DmnRunnerMode;
      newConfigInputs?:
        | ((previousConfigInputs: UnitablesInputsConfigs) => UnitablesInputsConfigs)
        | UnitablesInputsConfigs;
      shouldUpdateFs?: boolean;
      cancellationToken?: Holder<boolean>;
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
        shouldUpdateFs: args.shouldUpdateFs !== undefined ? args.shouldUpdateFs : true,
        cancellationToken: args.cancellationToken ?? new Holder(false),
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
      setDmnRunnerPersistenceJson({ newInputsRow });
    },
    [setDmnRunnerPersistenceJson]
  );

  const setDmnRunnerMode = useCallback(
    (newMode: DmnRunnerMode) => {
      setDmnRunnerPersistenceJson({ newMode });
    },
    [setDmnRunnerPersistenceJson]
  );

  const setDmnRunnerConfigInputs = useCallback(
    (
      newConfigInputs: (previousConfigInputs: UnitablesInputsConfigs) => UnitablesInputsConfigs | UnitablesInputsConfigs
    ) => {
      setDmnRunnerPersistenceJson({ newConfigInputs });
    },
    [setDmnRunnerPersistenceJson]
  );

  // The refreshCallback is called after a CompanionFS event;
  // When another TAB updates the FS, this callback will sync up;
  useCompanionFsFileSyncedWithWorkspaceFile(
    dmnRunnerPersistenceService.companionFsService,
    props.workspaceFile.workspaceId,
    props.workspaceFile.relativePath,
    useCallback(
      async (cancellationToken: Holder<boolean>, workspaceFileEvent: CompanionFsServiceBroadcastEvents | undefined) => {
        if (!jsonSchema || !workspaceFileEvent) {
          return;
        }

        // CFSF_ADD is triggered on file creation, file rename, persistence deletion or upload.
        if (workspaceFileEvent.type === "CFSF_ADD") {
          const dmnRunnerPersistenceJson = dmnRunnerPersistenceService.parseDmnRunnerPersistenceJson(
            workspaceFileEvent.content
          );

          // Remove incompatible values and add default values;
          try {
            const validate = dmnRunnerAjv.compile(jsonSchema);
            dmnRunnerPersistenceJson.inputs.forEach((input) => {
              // save id;
              const id = input.id;
              removeChangedPropertiesAndAdditionalProperties(validate, input);
              input.id = id;
            });
          } catch (error) {
            console.debug("DMN RUNNER AJV:", error);
          }

          setDmnRunnerPersistenceJson({
            newConfigInputs: cloneDeep(dmnRunnerPersistenceJson.configs.inputs),
            newInputsRow: cloneDeep(dmnRunnerPersistenceJson.inputs).map((dmnRunnerInput) => ({
              ...getDefaultValues(jsonSchema),
              ...dmnRunnerInput,
            })),
            shouldUpdateFs: false,
            cancellationToken,
          });
          forceDmnRunnerReRender();
          return;
        }

        // CFSF_UPDATE will be called on every runner update;
        // CFSF_DELETE will be called when FS is deleted;
        if (workspaceFileEvent.type === "CFSF_UPDATE" || workspaceFileEvent.type === "CFSF_DELETE") {
          const dmnRunnerPersistenceJson = dmnRunnerPersistenceService.parseDmnRunnerPersistenceJson(
            workspaceFileEvent.content
          );

          if (!dmnRunnerPersistenceJson) {
            return;
          }

          setDmnRunnerPersistenceJson({
            newConfigInputs: dmnRunnerPersistenceJson.configs.inputs,
            newInputsRow: dmnRunnerPersistenceJson.inputs,
            shouldUpdateFs: false,
            cancellationToken,
          });
          return;
        }

        if (workspaceFileEvent.type === "CFSF_MOVE" || workspaceFileEvent.type === "CFSF_RENAME") {
          // ignore;
        }
        return;
      },
      [dmnRunnerAjv, dmnRunnerPersistenceService, jsonSchema, setDmnRunnerPersistenceJson]
    )
  );

  // Responsible to set the JSON schema based on the DMN model;
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (props.workspaceFile.extension !== "dmn" || extendedServices.status !== ExtendedServicesStatus.RUNNING) {
          setDmnRunnerContextProviderState({
            type: DmnRunnerProviderActionType.DEFAULT,
            newState: { isExpanded: false },
          });
          return;
        }

        extendedServicesModelPayload()
          .then((modelPayload) => {
            if (canceled.get() || modelPayload === undefined) {
              return;
            }
            extendedServices.client.formSchema(modelPayload).then((jsonSchema) => {
              if (canceled.get()) {
                return;
              }

              const jsonSchemaDraft4 = {
                $schema: SCHEMA_DRAFT4,
                ...jsonSchema,
              };

              resolveReferencesAndCheckForRecursion(jsonSchemaDraft4, canceled)
                .then((resolvedSchema) => {
                  if (canceled.get() || !resolvedSchema) {
                    return;
                  }

                  setJsonSchema((previousJsonSchema) => {
                    // Early bailout in the DMN first render;
                    // This prevents to set the inputs from the previous DMN
                    if (!previousJsonSchema) {
                      return resolvedSchema;
                    }

                    const validateInputs = dmnRunnerAjv.compile(resolvedSchema);

                    // Add default values and delete changed data types;
                    setDmnRunnerPersistenceJson({
                      newConfigInputs: (previousConfigInputs) => {
                        const newConfigInputs = cloneDeep(previousConfigInputs);
                        removeChangedPropertiesAndAdditionalProperties(validateInputs, newConfigInputs);
                        return newConfigInputs;
                      },
                      newInputsRow: (previousInputs) => {
                        return cloneDeep(previousInputs).map((input) => {
                          const id = input.id;
                          removeChangedPropertiesAndAdditionalProperties(validateInputs, input);
                          input.id = id;
                          return { ...getDefaultValues(resolvedSchema), ...input };
                        });
                      },
                      cancellationToken: canceled,
                    });

                    // This should be done to remove any previous errors or to add new errors
                    if (Object.keys(diff(previousJsonSchema, resolvedSchema)).length > 0) {
                      forceDmnRunnerReRender();
                    }
                    return resolvedSchema;
                  });
                })
                .catch((err) => {
                  if (canceled.get()) {
                    return;
                  }
                  console.log(err);
                  setJsonSchema(undefined);
                });
            });
          })
          .catch((err) => {
            console.error(err);
            setExtendedServicesError(true);
          });
      },
      [
        dmnRunnerAjv,
        extendedServices.client,
        extendedServices.status,
        extendedServicesModelPayload,
        props.workspaceFile.extension,
        setDmnRunnerPersistenceJson,
      ]
    )
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setDmnRunnerInputs((previousInputs) => {
        const newInputs = cloneDeep(previousInputs);
        const defaultValues = getDefaultValues(jsonSchema ?? {});
        newInputs.splice(args.beforeIndex, 0, { id: generateUuid(), ...defaultValues });
        return newInputs;
      });
      setDmnRunnerContextProviderState({
        type: DmnRunnerProviderActionType.DEFAULT,
        newState: { currentInputIndex: args.beforeIndex },
      });
      setDmnRunnerResults({ type: DmnRunnerResultsActionType.CLONE_LAST });
    },
    [setDmnRunnerInputs, jsonSchema]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setDmnRunnerInputs((previousInputs) => {
        const newInputs = cloneDeep(previousInputs);
        newInputs.splice(args.rowIndex, 0, {
          ...JSON.parse(JSON.stringify(previousInputs[args.rowIndex])),
          id: generateUuid(),
        });
        return newInputs;
      });
      setDmnRunnerResults({ type: DmnRunnerResultsActionType.CLONE_LAST });
    },
    [setDmnRunnerInputs]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setDmnRunnerInputs((previousInputs) => {
        const newInputs = cloneDeep(previousInputs);
        const defaultValues = getDefaultValues(jsonSchema ?? {});
        newInputs[args.rowIndex] = { id: generateUuid(), ...defaultValues };
        return newInputs;
      });
    },
    [jsonSchema, setDmnRunnerInputs]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setDmnRunnerInputs((previousInputs) => {
        const newInputs = cloneDeep(previousInputs);
        newInputs.splice(args.rowIndex, 1);
        newInputs.forEach((e, i, newInputRows) => {
          if (i >= args.rowIndex) {
            newInputRows[i] = { ...e, id: generateUuid() };
          }
        });
        return newInputs;
      });
    },
    [setDmnRunnerInputs]
  );

  const dmnRunnerDispatch = useMemo(
    () => ({
      setDmnRunnerContextProviderState,
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
      dmnRunnerKey,
      dmnRunnerPersistenceJson,
      extendedServicesError,
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
      dmnRunnerKey,
      dmnRunnerMode,
      dmnRunnerPersistenceJson,
      extendedServicesError,
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

export function DmnRunnerExtendedServicesError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened while trying to show your inputs</p>
          <br />
          <p>
            If your DMN file has included models, please check if they still exist. If you have deleted a file that was
            being used as an included model, please remove its reference in the Included Models tab.
          </p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
