/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React from "react";
import { Alert, AlertActionCloseButton, Button } from "@patternfly/react-core/dist/js";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useCallback, useEffect, useState } from "react";
import { APP_NAME } from "../../AppConstants";
import { routes } from "../../navigation/Routes";
import { setPageTitle } from "../../PageTitle";
import { SETTINGS_PAGE_SECTION_TITLE } from "../SettingsContext";
import { isBrowserChromiumBased } from "../../workspace/startupBlockers/SupportedBrowsers";
import { useHistory } from "react-router";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";
import { ConfirmDeleteModal } from "../../table";
import { useStorage } from "./useStorage";

const PAGE_TITLE = "Storage";

export function StorageSettings() {
  const history = useHistory();
  const { wipeOutStorage } = useStorage();
  const [isDeleteCookiesChecked, setDeleteCookiesChecked] = useState(false);
  const [isDeleteLocalStorageChecked, setDeleteLocalStorageChecked] = useState(false);
  const [isConfirmDeleteModalOpen, setConfirmDeleteModalOpen] = useState(false);

  const toggleConfirmModal = useCallback(() => {
    setConfirmDeleteModalOpen((isOpen) => !isOpen);
  }, []);

  const deleteErrorAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          variant="danger"
          title={`Oops, something went wrong while trying to delete the selected data. Please refresh the page and try again. If the problem persists, you can try deleting site data for this application in your browser's settings.`}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const onConfirmDeleteModalDelete = useCallback(async () => {
    toggleConfirmModal();

    try {
      await wipeOutStorage({
        includeCookies: isDeleteCookiesChecked,
        includeLocalStorage: isDeleteLocalStorageChecked,
      });
    } catch (e) {
      console.error(e);
      deleteErrorAlert.show();
    }
  }, [toggleConfirmModal, wipeOutStorage, isDeleteCookiesChecked, isDeleteLocalStorageChecked, deleteErrorAlert]);

  useEffect(() => {
    if (!isBrowserChromiumBased()) {
      history.replace(routes.settings.home.path({}));
    }
    setPageTitle([SETTINGS_PAGE_SECTION_TITLE, PAGE_TITLE]);
  }, [history]);

  return (
    <>
      <Page>
        <PageSection variant={"light"} isWidthLimited>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              Here, you have the ability to completely erase all stored data in your browser.
              <br />
              Safely delete your cookies, modules, settings and all information locally stored in your browser, giving a
              fresh start to {APP_NAME}.
            </Text>
          </TextContent>
        </PageSection>

        <PageSection>
          <PageSection variant={"light"}>
            <Form>
              <Checkbox
                id="delete-indexedDB"
                label="Storage"
                description={"Delete all databases. You will lose all your modules and workspaces."}
                isChecked
                isDisabled
              />
              <Alert
                variant="warning"
                isInline
                title="By selecting the cookies and local storage, all your saved settings will be permanently erased."
              >
                <br />
                <Checkbox
                  id="delete-cookies"
                  label="Cookies"
                  description={"Delete all cookies."}
                  isChecked={isDeleteCookiesChecked}
                  onChange={setDeleteCookiesChecked}
                />
                <br />
                <Checkbox
                  id="delete-localStorage"
                  label="LocalStorage"
                  description={"Delete all localStorage information."}
                  isChecked={isDeleteLocalStorageChecked}
                  onChange={setDeleteLocalStorageChecked}
                />
              </Alert>
            </Form>
            <br />
            <Button variant="danger" onClick={toggleConfirmModal}>
              Delete data
            </Button>
          </PageSection>
        </PageSection>
        <ConfirmDeleteModal
          isOpen={isConfirmDeleteModalOpen}
          onClose={toggleConfirmModal}
          onDelete={onConfirmDeleteModalDelete}
          elementsTypeName="data"
          deleteMessage="All stored information will be permanently deleted and you will be redirected to the Overview page."
        />
      </Page>
    </>
  );
}
