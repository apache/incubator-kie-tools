/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Button, Checkbox, Modal, ModalProps, Skeleton } from "@patternfly/react-core/dist/js";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";

export type ConfirmDeleteModalProps = Pick<ModalProps, "isOpen" | "onClose"> & {
  /**
   * set to false to manage the loading. Default is true.
   */
  dataLoaded?: boolean;

  deleteMessage: React.ReactNode;

  elementsTypeName: string;

  /**
   * set to true if there has been error loading the data.
   */
  fetchError?: boolean;

  onDelete: () => void;
};

export function ConfirmDeleteModal(props: ConfirmDeleteModalProps) {
  const { isOpen, onClose, onDelete, elementsTypeName, deleteMessage, dataLoaded = true, fetchError = false } = props;
  const [isDeleteCheck, setIsDeleteCheck] = useState(false);

  const onDeleteCheckChange = useCallback((checked: boolean) => {
    setIsDeleteCheck(checked);
  }, []);

  useEffect(() => {
    setIsDeleteCheck(false);
  }, [isOpen]);

  return (
    <>
      <Modal
        title={`Delete ${elementsTypeName}`}
        titleIconVariant={"warning"}
        isOpen={isOpen && !fetchError}
        onClose={onClose}
        aria-describedby="modal-custom-icon-description"
        actions={[
          dataLoaded ? (
            <Button key="confirm" variant="danger" onClick={onDelete} isDisabled={!isDeleteCheck} aria-label="Delete">
              Delete {elementsTypeName}
            </Button>
          ) : (
            <Skeleton width="100px" key="confirm-skeleton" />
          ),
          <Button key="cancel" variant="link" onClick={onClose} aria-label="Cancel">
            Cancel
          </Button>,
        ]}
        variant="small"
      >
        {dataLoaded ? <span id="modal-custom-icon-description">{deleteMessage}</span> : <Skeleton width="80%" />}
        <br />
        <br />
        <Checkbox
          label="I understand that this action cannot be undone."
          id="delete-model-check"
          isChecked={isDeleteCheck}
          onChange={onDeleteCheckChange}
          aria-label="Confirm checkbox delete model"
        />
      </Modal>

      <Modal
        title={`Error retrieving data`}
        titleIconVariant={"danger"}
        isOpen={isOpen && fetchError}
        onClose={onClose}
        aria-describedby="modal-custom-icon-description"
        variant="small"
      >
        <span id="modal-custom-icon-description">An error occurred while loading the data!</span>
      </Modal>
    </>
  );
}
