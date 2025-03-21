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
import { useCallback } from "react";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Spinner } from "@patternfly/react-core/dist/esm/components/Spinner";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextContent, Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import {
  ImportJavaClasses,
  JavaClass,
  useImportJavaClassesWizardI18n,
  useLanguageServerAvailable,
} from "@kie-tools/import-java-classes-component";
import { addTopLevelItemDefinition as _addTopLevelItemDefinition } from "../mutations/addTopLevelItemDefinition";
import { JavaClassConflictOptions, useImportJavaClasses } from "./useImportJavaClasses";

const ImportJavaClassesWrapper = () => {
  const {
    javaCodeCompletionService,
    handleConflictAction,
    handleImportJavaClasses,
    conflictsClasses,
    isConflictsOccured,
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
          conflictsNames={conflictsClasses}
        />
      )}
    </>
  );
};

const ImportJavaClassesDropdownItem = (props: React.ComponentProps<typeof DropdownItem>) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const { javaCodeCompletionService } = useImportJavaClasses();
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
  conflictsNames,
}: {
  isOpen: boolean;
  handleConfirm: (options: JavaClassConflictOptions) => void;
  conflictsNames: JavaClass[];
}) => {
  const handleActionButtonClick = useCallback((e: any) => handleConfirm?.(e?.target?.name), [handleConfirm]);
  const classNames = conflictsNames?.map((javaClass) => javaClass?.name);
  return (
    <Modal
      title="Duplicate DMN Data Type Detected"
      titleIconVariant="warning"
      aria-describedby="modal-title-icon-description"
      showClose={false}
      isOpen={isOpen}
      variant={ModalVariant.small}
      position="top"
      actions={[
        <Button
          name={JavaClassConflictOptions.OVERWRITE}
          key={JavaClassConflictOptions.OVERWRITE}
          variant="primary"
          onClick={handleActionButtonClick}
        >
          {JavaClassConflictOptions.OVERWRITE}
        </Button>,
        <Button
          name={JavaClassConflictOptions.CREATE_NEW}
          key={JavaClassConflictOptions.CREATE_NEW}
          variant="link"
          onClick={handleActionButtonClick}
        >
          {JavaClassConflictOptions.CREATE_NEW}
        </Button>,
      ]}
    >
      <TextContent>
        {classNames?.length === 1 ? (
          <>
            An existing DMN type named{" "}
            <Text component={TextVariants.pre} style={{ display: "inline" }}>
              <b>{classNames?.join()}</b>
            </Text>{" "}
            has been detected. This type is currently in use within the system. How would you like to proceed?
          </>
        ) : (
          <>
            Multiple DMN types have been detected in the list. The following DMN types are currently in use within the
            system:{" "}
            <Text component={TextVariants.pre} style={{ display: "inline" }}>
              <b>{classNames?.join()}</b>
            </Text>
            . How would you like to proceed?
          </>
        )}
        <Text component={TextVariants.blockquote} style={{ background: "none" }}>
          <HelperText>
            <HelperTextItem variant="indeterminate" hasIcon>
              <b>{JavaClassConflictOptions.OVERWRITE}:</b> This option will replace the existing DMN type with the new
              one.
            </HelperTextItem>
            <HelperTextItem variant="indeterminate" hasIcon>
              <b>{JavaClassConflictOptions.CREATE_NEW}:</b> This option will preserve the existing DMN type and create a
              new one with a unique name.
            </HelperTextItem>
          </HelperText>
        </Text>
      </TextContent>
    </Modal>
  );
};

export { ImportJavaClassesWrapper, ImportJavaClassesDropdownItem, ImportJavaClassNameConflictsModal };
