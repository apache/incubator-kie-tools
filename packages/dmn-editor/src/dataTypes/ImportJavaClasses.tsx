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
import { useCallback, useState } from "react";
import { DropdownItem } from "@patternfly/react-core/dist/js/deprecated/components/Dropdown/DropdownItem";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextContent, Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import {
  ImportJavaClasses,
  JavaClass,
  JavaCodeCompletionService,
  useImportJavaClassesWizardI18n,
  useLanguageServerAvailable,
} from "@kie-tools/import-java-classes-component";
import { addTopLevelItemDefinition as _addTopLevelItemDefinition } from "../mutations/addTopLevelItemDefinition";
import { JavaClassConflictOptions, JavaClassWithConflictInfo, useImportJavaClasses } from "./useImportJavaClasses";
import { Radio } from "@patternfly/react-core/dist/esm/components/Radio";

const ImportJavaClassesWrapper = ({
  javaCodeCompletionService,
}: {
  javaCodeCompletionService: JavaCodeCompletionService;
}) => {
  const {
    handleConflictAction,
    handleImportJavaClasses,
    conflictsClasses,
    isConflictsOccured,
    handleCloseConflictsModal,
  } = useImportJavaClasses();
  return (
    <>
      <ImportJavaClasses
        loadJavaClassesInDataTypeEditor={handleImportJavaClasses}
        javaCodeCompletionService={javaCodeCompletionService}
      />
      {isConflictsOccured && conflictsClasses?.length > 0 && (
        <ImportJavaClassNameConflictsModal
          isOpen={isConflictsOccured}
          handleConfirm={handleConflictAction}
          conflictedJavaClasses={conflictsClasses}
          onClose={handleCloseConflictsModal}
        />
      )}
    </>
  );
};

const ImportJavaClassesDropdownItem = ({
  javaCodeCompletionService,
  ...props
}: React.ComponentProps<typeof DropdownItem> & { javaCodeCompletionService: JavaCodeCompletionService }) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const { isLanguageServerLoading, isLanguageServerDisabled, isLanguageServerError } =
    useLanguageServerAvailable(javaCodeCompletionService);
  const defineTooltipMessage = React.useCallback(() => {
    if (isLanguageServerDisabled) {
      return i18n.modalButton.disabledMessage;
    } else if (isLanguageServerError) {
      return i18n.modalButton.errorMessage;
    }
    return undefined;
  }, [
    isLanguageServerDisabled,
    isLanguageServerError,
    i18n.modalButton.disabledMessage,
    i18n.modalButton.errorMessage,
  ]);
  return (
    <>
      {defineTooltipMessage() ? (
        <Tooltip content={defineTooltipMessage()}>
          <DropdownItem
            style={{ minWidth: "240px" }}
            icon={isLanguageServerLoading ? <Spinner size="md" /> : <PlusIcon />}
            isDisabled={isLanguageServerDisabled || isLanguageServerLoading}
            {...props}
          >
            {i18n.modalButton.text}
          </DropdownItem>
        </Tooltip>
      ) : (
        <DropdownItem
          style={{ minWidth: "240px" }}
          icon={isLanguageServerLoading ? <Spinner size="md" /> : <PlusIcon />}
          isDisabled={isLanguageServerDisabled || isLanguageServerLoading}
          {...props}
        >
          {i18n.modalButton.text}
        </DropdownItem>
      )}
    </>
  );
};

const ImportJavaClassNameConflictsModal = ({
  isOpen,
  handleConfirm,
  conflictedJavaClasses,
  onClose,
}: {
  isOpen: boolean;
  handleConfirm: (options: { internal: JavaClassConflictOptions; external: JavaClassConflictOptions }) => void;
  conflictedJavaClasses: JavaClassWithConflictInfo[];
  onClose: () => void;
}) => {
  const internalConflicts = conflictedJavaClasses.filter((c) => !c.isExternalConflict);
  const externalConflicts = conflictedJavaClasses.filter((c) => c.isExternalConflict);
  const hasInternalConflicts = internalConflicts.length > 0;
  const hasExternalConflicts = externalConflicts.length > 0;

  const [internalAction, setInternalAction] = useState<JavaClassConflictOptions>(JavaClassConflictOptions.REPLACE);
  const [externalAction, setExternalAction] = useState<JavaClassConflictOptions>(JavaClassConflictOptions.KEEP_BOTH);

  const handleInternalRadioBtnClick = useCallback((event: React.FormEvent<HTMLInputElement>) => {
    setInternalAction(event.currentTarget.name as JavaClassConflictOptions);
  }, []);

  const handleExternalRadioBtnClick = useCallback((event: React.FormEvent<HTMLInputElement>) => {
    setExternalAction(event.currentTarget.name as JavaClassConflictOptions);
  }, []);

  const handleActionButtonClick = useCallback(() => {
    handleConfirm?.({
      internal: internalAction,
      external: externalAction,
    });
  }, [handleConfirm, internalAction, externalAction]);

  return (
    <Modal
      title="Duplicate DMN Data Type Detected"
      titleIconVariant="warning"
      aria-describedby="modal-import-java-classes-conflict-description"
      onClose={onClose}
      isOpen={isOpen}
      variant={ModalVariant.small}
      position="top"
      actions={[
        <Button key="import-java-classes-conflict-btn" variant="primary" onClick={handleActionButtonClick}>
          Import
        </Button>,
        <Button key="import-java-classes-cancel-btn" variant="primary" onClick={onClose}>
          Cancel
        </Button>,
      ]}
    >
      <TextContent>
        <Text component={TextVariants.p}>
          Conflicts have been detected between imported Java classes and existing DMN data types. Please review the
          details below and choose how to proceed.
        </Text>

        {hasInternalConflicts && (
          <>
            <Text component={TextVariants.h4}>Internal Data Type Conflicts</Text>
            <Text>
              <b>{internalConflicts.map((c) => c.name).join(", ")}</b>- These are editable DMN Types. Choose how to
              resolve them.
            </Text>
            <Text
              component={TextVariants.blockquote}
              style={{ background: "none", display: "flex", flexDirection: "column", gap: "1rem" }}
            >
              <Radio
                isChecked={internalAction === JavaClassConflictOptions.REPLACE}
                id={`radio-internal-${JavaClassConflictOptions.REPLACE}`}
                label={JavaClassConflictOptions.REPLACE}
                name={JavaClassConflictOptions.REPLACE}
                onChange={handleInternalRadioBtnClick}
                description="This option will replace the existing DMN type with the new one."
                isLabelWrapped={true}
              />
              <Radio
                isChecked={internalAction === JavaClassConflictOptions.KEEP_BOTH}
                id={`radio-internal-${JavaClassConflictOptions.KEEP_BOTH}`}
                label={JavaClassConflictOptions.KEEP_BOTH}
                name={JavaClassConflictOptions.KEEP_BOTH}
                onChange={handleInternalRadioBtnClick}
                description="This option will preserve the existing DMN type and create a new one with a unique name."
                isLabelWrapped={true}
              />
            </Text>
          </>
        )}

        {hasExternalConflicts && (
          <>
            <Text component={TextVariants.h4}>External Data Type Conflicts</Text>
            <Text>
              <b>{externalConflicts.map((c) => c.name).join(", ")}</b>- These types come from external sources and
              cannot be replaced.
            </Text>
            <Text
              component={TextVariants.blockquote}
              style={{ background: "none", display: "flex", flexDirection: "column", gap: "1rem" }}
            >
              <Radio
                isChecked={externalAction === JavaClassConflictOptions.REPLACE}
                id={`radio-external-${JavaClassConflictOptions.REPLACE}`}
                label={JavaClassConflictOptions.REPLACE}
                name={JavaClassConflictOptions.REPLACE + "-external"}
                onChange={handleExternalRadioBtnClick}
                description="This option will replace the existing DMN type with the new one."
                isLabelWrapped={true}
                isDisabled={true}
              />
              <Radio
                isChecked={externalAction === JavaClassConflictOptions.KEEP_BOTH}
                id={`radio-external-${JavaClassConflictOptions.KEEP_BOTH}`}
                label={JavaClassConflictOptions.KEEP_BOTH}
                name={JavaClassConflictOptions.KEEP_BOTH + "-external"}
                onChange={handleExternalRadioBtnClick}
                description="This option will preserve the existing DMN type and create a new one with a unique name."
                isLabelWrapped={true}
              />
            </Text>
          </>
        )}
      </TextContent>
    </Modal>
  );
};

export { ImportJavaClassesWrapper, ImportJavaClassesDropdownItem, ImportJavaClassNameConflictsModal };
