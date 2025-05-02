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
import { JavaClassConflictOptions, useImportJavaClasses } from "./useImportJavaClasses";
import { Radio } from "@patternfly/react-core/dist/esm/components/Radio";

const ImportJavaClassesWrapper = ({
  javaCodeCompletionService,
}: {
  javaCodeCompletionService: JavaCodeCompletionService;
}) => {
  const { handleConflictAction, handleImportJavaClasses, conflictsClasses, isConflictsOccured } =
    useImportJavaClasses();
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
          confliectedJavaClasses={conflictsClasses}
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
  confliectedJavaClasses,
}: {
  isOpen: boolean;
  handleConfirm: (options: JavaClassConflictOptions) => void;
  confliectedJavaClasses: JavaClass[];
}) => {
  const [action, setAction] = useState<JavaClassConflictOptions>(JavaClassConflictOptions.REPLACE);
  const handleActionButtonClick = useCallback(() => handleConfirm?.(action), [handleConfirm, action]);
  const handleRadioBtnClick = useCallback((_, e) => setAction?.(e?.target?.name), []);
  const classNames = confliectedJavaClasses?.map((javaClass) => javaClass?.name);
  return (
    <Modal
      title="Duplicate DMN Data Type Detected"
      titleIconVariant="warning"
      aria-describedby="modal-import-java-classes-conflict-description"
      showClose={false}
      isOpen={isOpen}
      variant={ModalVariant.small}
      position="top"
      actions={[
        <Button key="import-java-classes-conflict-btn" variant="primary" onClick={handleActionButtonClick}>
          Import
        </Button>,
      ]}
    >
      <TextContent>
        {classNames?.length === 1 ? (
          <Text component={TextVariants.p}>
            An existing DMN type named <b>{classNames?.join()}</b> has been detected. This type is currently in use
            within the system. How would you like to proceed?
          </Text>
        ) : (
          <Text component={TextVariants.p}>
            Multiple DMN types have been detected in the list. The following DMN types are currently in use within the
            system <b>{classNames?.join()}</b>. How would you like to proceed?
          </Text>
        )}
        <Text
          component={TextVariants.blockquote}
          style={{ background: "none", display: "flex", flexDirection: "column", gap: "1rem" }}
        >
          <Radio
            isChecked={action === JavaClassConflictOptions.REPLACE}
            id={`radio-${JavaClassConflictOptions.REPLACE}`}
            label={JavaClassConflictOptions.REPLACE}
            name={JavaClassConflictOptions.REPLACE}
            onChange={handleRadioBtnClick}
            description="This option will replace the existing DMN type with the new one."
            isLabelWrapped={true}
          />
          <Radio
            isChecked={action === JavaClassConflictOptions.KEEP_BOTH}
            id={`radio-${JavaClassConflictOptions.KEEP_BOTH}`}
            label={JavaClassConflictOptions.KEEP_BOTH}
            name={JavaClassConflictOptions.KEEP_BOTH}
            onChange={handleRadioBtnClick}
            description="This option will preserve the existing DMN type and create a new one with a unique name."
            isLabelWrapped={true}
          />
        </Text>
      </TextContent>
    </Modal>
  );
};

export { ImportJavaClassesWrapper, ImportJavaClassesDropdownItem, ImportJavaClassNameConflictsModal };
