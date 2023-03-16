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
import { PropsWithChildren, useCallback, useEffect, useMemo, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerDispatchContext, DmnRunnerStateContext } from "./DmnRunnerContext";
import { KieSandboxExtendedServicesModelPayload } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerPersistence } from "../dmnRunnerPersistence/DmnRunnerPersistenceHook";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import {
  generateUuid,
  DEFAULT_DMN_RUNNER_CONFIG_INPUT,
  deepCopyPersistenceJson,
  ConfigInputRow,
} from "../dmnRunnerPersistence/DmnRunnerPersistenceService";
import {
  useDmnRunnerPersistenceDispatch,
  DmnRunnerPersistenceReducerActionType,
} from "../dmnRunnerPersistence/DmnRunnerPersistenceDispatchContext";
import { DmnRunnerPersistenceDebouncer } from "../dmnRunnerPersistence/DmnRunnerPersistenceDebouncer";

interface Props {
  isEditorReady?: boolean;
  workspaceFile: WorkspaceFile;
  dmnLanguageService?: DmnLanguageService;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();
  const { dmnRunnerPersistenceService, dmnRunnerPersistenceJson, dispatchDmnRunnerPersistenceJson } =
    useDmnRunnerPersistenceDispatch();
  useDmnRunnerPersistence(props.workspaceFile.workspaceId, props.workspaceFile.relativePath);

  const [canBeVisualized, setCanBeVisualized] = useState<boolean>(false);
  const [error, setError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [currentInputRowIndex, setCurrentInputRowIndex] = useState<number>(0);

  const dmnRunnerInputs = useMemo(() => dmnRunnerPersistenceJson.inputs, [dmnRunnerPersistenceJson.inputs]);
  const dmnRunnerMode = useMemo(() => dmnRunnerPersistenceJson.configs.mode, [dmnRunnerPersistenceJson.configs.mode]);
  const dmnRunnerPersistenceDebouncer = useMemo(() => {
    return new DmnRunnerPersistenceDebouncer(dmnRunnerPersistenceService.companionFsService);
  }, [dmnRunnerPersistenceService.companionFsService]);
  const dmnRunnerConfigInputs = useMemo(
    () => dmnRunnerPersistenceJson?.configs?.inputs,
    [dmnRunnerPersistenceJson?.configs?.inputs]
  );

  useEffect(() => {
    if (props.isEditorReady) {
      setCanBeVisualized(true);
    } else {
      setCanBeVisualized(false);
    }
  }, [props.isEditorReady]);

  const status = useMemo(() => {
    return isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isExpanded]);

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
    if (
      props.workspaceFile.extension !== "dmn" ||
      extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
    ) {
      setExpanded(false);
      return;
    }

    preparePayload()
      .then((payload) => {
        extendedServices.client.formSchema(payload).then((jsonSchema) => {
          setJsonSchema(jsonSchema);
        });
      })
      .catch((err) => {
        console.error(err);
        setError(true);
      });
  }, [extendedServices.status, extendedServices.client, props.workspaceFile.extension, preparePayload]);

  const prevKieSandboxExtendedServicesStatus = usePrevious(extendedServices.status);
  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      extendedServices.status === KieSandboxExtendedServicesStatus.STOPPED ||
      extendedServices.status === KieSandboxExtendedServicesStatus.NOT_RUNNING
    ) {
      setExpanded(false);
    }
  }, [prevKieSandboxExtendedServicesStatus, extendedServices.status, props.workspaceFile.extension]);

  const setDmnRunnerPersistenceJson = useCallback(
    (args: {
      newInputsRow?: (previousInputs: Array<InputRow>) => Array<InputRow> | Array<InputRow>;
      newMode?: DmnRunnerMode;
      newConfigInputs?: (previousConfigInputs: ConfigInputRow) => ConfigInputRow | ConfigInputRow;
    }) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = deepCopyPersistenceJson(previousDmnRunnerPersistenceJson);
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
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerInputs = useCallback(
    (newInputsRow: (previousInputs: Array<InputRow>) => Array<InputRow> | Array<InputRow>) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = deepCopyPersistenceJson(previousDmnRunnerPersistenceJson);
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
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerMode = useCallback(
    (newMode: DmnRunnerMode) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = deepCopyPersistenceJson(previousDmnRunnerPersistenceJson);
          newDmnRunnerPersistenceJson.configs.mode = newMode;
          return newDmnRunnerPersistenceJson;
        },
      });
    },
    [
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const setDmnRunnerConfigInputs = useCallback(
    (newConfigInputs: (previousConfigInputs: ConfigInputRow) => ConfigInputRow | ConfigInputRow) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson: (previousDmnRunnerPersistenceJson) => {
          const newDmnRunnerPersistenceJson = deepCopyPersistenceJson(previousDmnRunnerPersistenceJson);
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
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
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

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = deepCopyPersistenceJson(previousPersistenceJson);
          const index = args.beforeIndex === 0 ? 0 : args.beforeIndex - 1;

          // add default value;
          const newInputsRow = getDefaultValuesForInputs(newPersistenceJson.inputs[index]);

          // add default configs;
          const newConfigInputsRow = Object.entries(newPersistenceJson.inputs[index]).reduce((acc, [key, _]) => {
            if (key === "id") {
              return acc;
            }
            acc[key] = { ...DEFAULT_DMN_RUNNER_CONFIG_INPUT };
            return acc;
          }, {} as any);
          newPersistenceJson.inputs.splice(args.beforeIndex, 0, newInputsRow);
          // newPersistenceJson.configs.inputs.splice(args.beforeIndex, 0, newConfigInputsRow);
          return newPersistenceJson;
        },
      });
      setCurrentInputRowIndex(args.beforeIndex);
    },
    [
      getDefaultValuesForInputs,
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = deepCopyPersistenceJson(previousPersistenceJson);
          // duplicate inputs
          newPersistenceJson.inputs.splice(args.rowIndex, 0, {
            ...JSON.parse(JSON.stringify(previousPersistenceJson.inputs[args.rowIndex])),
            id: generateUuid(),
          });
          // duplicate configs
          // newPersistenceJson.configs.inputs.splice(args.rowIndex, 0, {
          //   ...JSON.parse(JSON.stringify(previousPersistenceJson.configs.inputs[args.rowIndex])),
          //   id: generateUuid(),
          // });
          return newPersistenceJson;
        },
      });
    },
    [
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = deepCopyPersistenceJson(previousPersistenceJson);
          // reset to defaul values;
          const resetedInputRows = getDefaultValuesForInputs(newPersistenceJson.inputs[args.rowIndex]);
          // reset default configs;
          const newConfigInputsRow = Object.entries(newPersistenceJson.inputs[args.rowIndex]).reduce(
            (acc, [key, _]) => {
              if (key === "id") {
                return acc;
              }
              acc[key] = { ...DEFAULT_DMN_RUNNER_CONFIG_INPUT };
              return acc;
            },
            {} as any
          );
          newPersistenceJson.inputs[args.rowIndex] = resetedInputRows;
          newPersistenceJson.configs.inputs[args.rowIndex] = newConfigInputsRow;
          return newPersistenceJson;
        },
      });
    },
    [
      getDefaultValuesForInputs,
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      dispatchDmnRunnerPersistenceJson({
        dmnRunnerPersistenceDebouncer,
        workspaceFileRelativePath: props.workspaceFile.relativePath,
        workspaceId: props.workspaceFile.workspaceId,
        type: DmnRunnerPersistenceReducerActionType.PREVIOUS,
        newPersistenceJson(previousPersistenceJson) {
          const newPersistenceJson = deepCopyPersistenceJson(previousPersistenceJson);
          // delete input row;
          newPersistenceJson.inputs.splice(args.rowIndex, 1);
          // re-generate ids for rows above the deleted one
          newPersistenceJson.inputs.forEach((e, i, newInputRows) => {
            if (i >= args.rowIndex) {
              newInputRows[i] = { ...e, id: generateUuid() };
            }
          });
          // delete config of input;
          // newPersistenceJson.configs.inputs.splice(args.rowIndex, 1);
          return newPersistenceJson;
        },
      });
    },
    [
      dmnRunnerPersistenceDebouncer,
      dispatchDmnRunnerPersistenceJson,
      props.workspaceFile.relativePath,
      props.workspaceFile.workspaceId,
    ]
  );

  const dmnRunnerDispatch = useMemo(
    () => ({
      onRowAdded,
      onRowDuplicated,
      onRowReset,
      onRowDeleted,
      preparePayload,
      setCurrentInputRowIndex,
      setError,
      setExpanded,
      setDmnRunnerPersistenceJson,
      setDmnRunnerInputs,
      setDmnRunnerMode,
      setDmnRunnerConfigInputs,
    }),
    [
      onRowAdded,
      onRowDuplicated,
      onRowReset,
      onRowDeleted,
      preparePayload,
      setCurrentInputRowIndex,
      setError,
      setExpanded,
      setDmnRunnerPersistenceJson,
      setDmnRunnerInputs,
      setDmnRunnerMode,
      setDmnRunnerConfigInputs,
    ]
  );

  const dmnRunnerState = useMemo(
    () => ({
      currentInputRowIndex,
      error,
      dmnRunnerPersistenceJson,
      configs: dmnRunnerConfigInputs,
      inputs: dmnRunnerInputs,
      isExpanded,
      canBeVisualized,
      jsonSchema,
      mode: dmnRunnerMode,
      status,
    }),
    [
      error,
      currentInputRowIndex,
      dmnRunnerConfigInputs,
      dmnRunnerPersistenceJson,
      dmnRunnerInputs,
      dmnRunnerMode,
      isExpanded,
      canBeVisualized,
      jsonSchema,
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
