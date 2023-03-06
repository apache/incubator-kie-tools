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
import { KieSandboxExtendedServicesModelPayload } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputs } from "../../dmnRunnerInputs/DmnRunnerInputsHook";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api/Bee";

interface Props {
  isEditorReady?: boolean;
  workspaceFile: WorkspaceFile;
  dmnLanguageService?: DmnLanguageService;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();

  const [isVisible, setVisible] = useState<boolean>(false);
  useEffect(() => {
    if (props.isEditorReady) {
      setVisible(true);
    } else {
      setVisible(false);
    }
  }, [props.isEditorReady]);

  const { inputRows, setInputRows } = useDmnRunnerInputs(props.workspaceFile);
  const [error, setError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.FORM);
  const [currentInputRowIndex, setCurrentInputRowIndex] = useState<number>(0);

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

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setInputRows((currentInputRows) => {
        const n = [...currentInputRows];
        // add default value;
        n.splice(args.beforeIndex, 0, { id: generateUuid() });
        return n;
      });
    },
    [setInputRows]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setInputRows((currentInputRows) => {
        const newInputRows = [...currentInputRows];
        newInputRows.splice(args.rowIndex, 0, {
          ...JSON.parse(JSON.stringify(currentInputRows[args.rowIndex])),
          id: generateUuid(),
        });
        return newInputRows;
      });
    },
    [setInputRows]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setInputRows((currentInputRows) => {
        const newInputRows = [...currentInputRows];
        newInputRows[args.rowIndex] = { id: generateUuid() };
        return newInputRows;
      });
    },
    [setInputRows]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setInputRows((currentInputRows) => {
        const newInputRows = [...currentInputRows];
        newInputRows.splice(args.rowIndex, 1);
        newInputRows.forEach((e, i, newInputRows) => {
          if (i >= args.rowIndex) {
            newInputRows[i] = { ...e, id: generateUuid() };
          }
        });
        return newInputRows;
      });
    },
    [setInputRows]
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
      setInputRows,
      setMode,
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
      setInputRows,
      setMode,
    ]
  );

  const dmnRunnerState = useMemo(
    () => ({
      currentInputRowIndex,
      error,
      inputRows,
      isExpanded,
      isVisible,
      jsonSchema,
      mode,
      status,
    }),
    [currentInputRowIndex, error, inputRows, isExpanded, isVisible, jsonSchema, mode, status]
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
