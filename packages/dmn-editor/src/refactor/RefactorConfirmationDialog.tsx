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

import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import * as React from "react";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useCallback, useMemo, useState } from "react";
import { renameDrgElement } from "../mutations/renameNode";

export function RefactorConfirmationDialog({
  onConfirmExpressionRefactor,
  onConfirmRenameOnly,
  onCancel,
  isRefactorModalOpen,
  fromName,
  toName,
}: {
  onConfirmExpressionRefactor: () => void;
  onConfirmRenameOnly: () => void;
  onCancel: () => void;
  isRefactorModalOpen: boolean;
  fromName: string | undefined;
  toName: string | undefined;
}) {
  return (
    <Modal
      aria-labelledby={"identifier-renamed"}
      variant={ModalVariant.small}
      isOpen={isRefactorModalOpen}
      showClose={true}
      onClose={onCancel}
      title={"Renaming identifier"}
      actions={[
        <Button key="confirm" variant={ButtonVariant.primary} onClick={onConfirmExpressionRefactor}>
          Yes, rename and replace
        </Button>,
        <Button key="rename" variant={ButtonVariant.secondary} onClick={onConfirmRenameOnly}>
          No, just rename
        </Button>,
        <Button key="cancel" variant={ButtonVariant.link} onClick={onCancel}>
          Cancel
        </Button>,
      ]}
    >
      The identifier{" "}
      <pre style={{ display: "inline" }}>
        {'"'}
        {fromName ?? "<undefined>"}
        {'"'}
      </pre>{" "}
      was renamed to{" "}
      <pre style={{ display: "inline" }}>
        {'"'}
        {toName ?? "<undefined>"}
        {'"'}
      </pre>
      , and it is used by one or more expressions.
      <br />
      <br />
      Would you like to automatically replace all occurrences of{" "}
      <pre style={{ display: "inline" }}>
        {'"'}
        {fromName ?? "<undefined>"}
        {'"'}
      </pre>{" "}
      with{" "}
      <pre style={{ display: "inline" }}>
        {'"'}
        {toName ?? "<undefined>"}
        {'"'}
      </pre>
      ?
    </Modal>
  );
}

export function isIdentifierReferencedInSomeExpression(args: {
  identifierUuid: string;
  dmnDefinitions: Normalized<DMN15__tDefinitions>;
  externalDmnModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
}) {
  const identifiersRefactor = new IdentifiersRefactor({
    writeableDmnDefinitions: args.dmnDefinitions,
    _readonly_externalDmnModelsByNamespaceMap: args.externalDmnModelsByNamespaceMap,
  });

  return Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier(args.identifierUuid)).length > 0;
}

export function useRefactor({
  index,
  identifierId,
  oldName,
}: {
  index: number;
  identifierId: string;
  oldName: string;
}) {
  const { externalModelsByNamespace } = useExternalModels();
  const externalDmnModelsByNamespaceMap = useDmnEditorStore((s) =>
    s.computed(s).getExternalDmnModelsByNamespaceMap(externalModelsByNamespace)
  );
  const [isRefactorModalOpen, setIsRefactorModalOpen] = useState(false);
  const [newName, setNewName] = useState("");
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const applyRename = useCallback(
    (args: {
      definitions: Normalized<DMN15__tDefinitions>;
      newName: string;
      shouldRenameReferencedExpressions: boolean;
    }) => {
      renameDrgElement({
        ...args,
        index,
        externalDmnModelsByNamespaceMap,
      });
    },
    [externalDmnModelsByNamespaceMap, index]
  );

  const setNewIdentifierNameCandidate = useCallback(
    (name: string) => {
      if (name === oldName) {
        return;
      }
      dmnEditorStoreApi.setState((state) => {
        if (
          isIdentifierReferencedInSomeExpression({
            identifierUuid: identifierId,
            dmnDefinitions: state.dmn.model.definitions,
            externalDmnModelsByNamespaceMap,
          })
        ) {
          setNewName(name);
          setIsRefactorModalOpen(true);
        } else {
          applyRename({
            definitions: state.dmn.model.definitions,
            newName: name,
            shouldRenameReferencedExpressions: false,
          });
        }
      });
    },
    [oldName, dmnEditorStoreApi, identifierId, externalDmnModelsByNamespaceMap, applyRename]
  );

  const refactorConfirmationDialog = useMemo(
    () => (
      <RefactorConfirmationDialog
        onConfirmExpressionRefactor={() => {
          setIsRefactorModalOpen(false);
          dmnEditorStoreApi.setState((state) => {
            applyRename({
              definitions: state.dmn.model.definitions,
              newName,
              shouldRenameReferencedExpressions: true,
            });
          });
        }}
        onConfirmRenameOnly={() => {
          setIsRefactorModalOpen(false);
          dmnEditorStoreApi.setState((state) => {
            applyRename({
              definitions: state.dmn.model.definitions,
              newName,
              shouldRenameReferencedExpressions: false,
            });
          });
        }}
        onCancel={() => {
          setIsRefactorModalOpen(false);
          setNewName("");
        }}
        isRefactorModalOpen={isRefactorModalOpen}
        fromName={oldName}
        toName={newName}
      />
    ),
    [applyRename, dmnEditorStoreApi, isRefactorModalOpen, newName, oldName]
  );

  return {
    setNewIdentifierNameCandidate,
    isRefactorModalOpen,
    setIsRefactorModalOpen,
    refactorConfirmationDialog,
    newName,
  };
}
