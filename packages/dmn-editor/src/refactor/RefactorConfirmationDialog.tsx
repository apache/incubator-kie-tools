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
import { DMN_LATEST__tDefinitions } from "@kie-tools/dmn-marshaller";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useCallback, useMemo, useState } from "react";
import { renameDrgElement } from "../mutations/renameNode";
import { useDmnEditorI18n } from "../i18n";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";

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
  const { i18n } = useDmnEditorI18n();
  return (
    <Modal
      aria-labelledby={"identifier-renamed"}
      variant={ModalVariant.small}
      isOpen={isRefactorModalOpen}
      showClose={true}
      onClose={onCancel}
      title={i18n.renamingIdentifier}
      actions={[
        <Button key="confirm" variant={ButtonVariant.primary} onClick={onConfirmExpressionRefactor}>
          {i18n.renameAndReplace}
        </Button>,
        <Button key="rename" variant={ButtonVariant.secondary} onClick={onConfirmRenameOnly}>
          {i18n.justRename}
        </Button>,
        <Button key="cancel" variant={ButtonVariant.link} onClick={onCancel}>
          {i18n.cancel}
        </Button>,
      ]}
    >
      <I18nWrapped
        components={{
          fromIdentifier: (
            <pre style={{ display: "inline" }}>
              {'"'}
              {fromName ?? i18n.undefined}
              {'"'}
            </pre>
          ),
          toIdentifier: (
            <pre style={{ display: "inline" }}>
              {'"'}
              {toName ?? i18n.undefined}
              {'"'}
            </pre>
          ),
          lineBreak: <br />,
        }}
      >
        {i18n.dataTypes.identifierRenameMessage}
      </I18nWrapped>
    </Modal>
  );
}

export function isIdentifierReferencedInSomeExpression(args: {
  identifierUuid: string;
  dmnDefinitions: Normalized<DMN_LATEST__tDefinitions>;
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
      definitions: Normalized<DMN_LATEST__tDefinitions>;
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
