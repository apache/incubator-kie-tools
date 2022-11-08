/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useState } from "react";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { ModalVariant } from "@patternfly/react-core/dist/esm/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import ExternalLinkSquareAltIcon from "@patternfly/react-icons/dist/esm/icons/external-link-square-alt-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { StartupBlockerTemplate } from "./StartupBlockerTemplate";
import { KieIcon } from "./KieIcon";
import { LATEST_VERSION_COMPATIBLE_WITH_LFS } from "./LatestCompatibleVersion";
import { APP_NAME } from "../../AppConstants";
import Dexie from "dexie";

const OLD_WORKSPACES_IDB_NAME = "workspaces";

export async function isTrue() {
  return await Dexie.exists(OLD_WORKSPACES_IDB_NAME);
}

export function Component() {
  const [isDeleteConfirmationModalOpen, setDeleteConfirmationModalOpen] = useState(false);
  const [isDeleting, setDeleting] = useState(false);

  return (
    <StartupBlockerTemplate>
      <Modal
        variant={ModalVariant.medium}
        isOpen={isDeleteConfirmationModalOpen}
        showClose={false}
        titleIconVariant={KieIcon}
        title={"Please confirm deleting everything"}
        actions={[
          <Button
            key="delete-everything"
            variant="primary"
            isLarge={true}
            style={{ width: "50%", backgroundColor: "var(--pf-c-button--m-danger--BackgroundColor)" }}
            isLoading={isDeleting}
            onClick={async () => {
              setDeleting(true);
              await Promise.all((await Dexie.getDatabaseNames()).map(async (dbName) => Dexie.delete(dbName)));
              window.location.reload();
            }}
          >
            Delete everything
            <br />
            and start fresh
          </Button>,
          <Button
            key="latest-version"
            variant="tertiary"
            iconPosition="right"
            style={{ width: "50%" }}
            icon={<ExternalLinkSquareAltIcon />}
            isLarge={true}
            onClick={() => {
              window.open(LATEST_VERSION_COMPATIBLE_WITH_LFS);
            }}
          >
            Open latest
            <br />
            compatible version
          </Button>,
        ]}
      >
        <br />
        <TextContent>
          <Text component={TextVariants.h4}>This action cannot be undone.</Text>
        </TextContent>
        <br />
        <hr />
        <br />
        Continuing will delete all models you have saved on this browser. Please certify that your work is already
        persisted somewhere else.
        <br />
        <br />
        {`If you're not sure, proceed to the latest compatible version to push or download your models.`}
      </Modal>
      <Modal
        showClose={false}
        isOpen={!isDeleteConfirmationModalOpen}
        variant={ModalVariant.medium}
        title={"Oops!"}
        titleIconVariant={KieIcon}
        actions={[
          <Button
            key="delete-everything"
            variant="primary"
            isLarge={true}
            style={{ width: "50%" }}
            onClick={() => setDeleteConfirmationModalOpen(true)}
          >
            Delete everything
            <br />
            and start fresh
          </Button>,
          <Button
            key="latest-version"
            variant="secondary"
            iconPosition="right"
            style={{ width: "50%" }}
            icon={<ExternalLinkSquareAltIcon />}
            isLarge={true}
            onClick={() => {
              window.open(LATEST_VERSION_COMPATIBLE_WITH_LFS);
            }}
          >
            Open latest
            <br />
            compatible version
          </Button>,
        ]}
      >
        <br />
        <TextContent>
          <Text component={TextVariants.h4}>{`${APP_NAME} has changed the way it saves your models.`}</Text>
        </TextContent>
        <br />
        <hr />
        <br />
        {`If you have work that is not saved outside of this browser yet, proceed to the latest compatible version to push or download your models.`}
        <br />
        <br />
        {`If your work is already safe somewhere else, start fresh by deleting the old models.`}
        <br />
      </Modal>
    </StartupBlockerTemplate>
  );
}
