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
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { Alert, AlertActionCloseButton, Button } from "@patternfly/react-core/dist/js";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useCallback, useEffect, useState } from "react";
import { Alerts, AlertsController, useAlert } from "../../alerts/Alerts";
import { APP_NAME } from "../../AppConstants";
import { routes } from "../../navigation/Routes";
import { setPageTitle } from "../../PageTitle";
import { ConfirmDeleteModal } from "../../table/ConfirmDeleteModal";
import { SETTINGS_PAGE_SECTION_TITLE } from "../SettingsContext";
import { deleteAllCookies } from "../../cookies";
import { isBrowserChromiumBased } from "../../workspace/startupBlockers/SupportedBrowsers";
import { useHistory } from "react-router";

const PAGE_TITLE = "Storage";
/**
 * delete alert delay in seconds before reloading the app.
 */
const DELETE_ALERT_DELAY = 5;

/**
 * Delete all indexed DBs
 */
const deleteAllIndexedDBs = async () => {
  Promise.all(
    (await indexedDB.databases()).filter((db) => db.name).map(async (db) => indexedDB.deleteDatabase(db.name!))
  );
};

function Timer(props: { delay: number }) {
  const [delay, setDelay] = useState(props.delay);

  useEffect(() => {
    const timer = setInterval(() => {
      setDelay((prevDelay) => prevDelay - 1);
    }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, []);

  return <>{delay}</>;
}

export function StorageSettings() {
  const [isDeleteCookiesChecked, setIsDeleteCookiesChecked] = useState(false);
  const [isDeleteLocalStorageChecked, setIsDeleteLocalStorageChecked] = useState(false);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [alerts, alertsRef] = useController<AlertsController>();
  const history = useHistory();

  const toggleConfirmModal = useCallback(() => {
    setIsConfirmDeleteModalOpen((isOpen) => !isOpen);
  }, []);

  const deleteSuccessAlert = useAlert(
    alerts,
    useCallback(({ close }) => {
      setTimeout(() => {
        window.location.href = routes.home.path({});
      }, DELETE_ALERT_DELAY * 1000);
      return (
        <Alert
          variant="success"
          title={
            <>
              Data deleted successfully. <br />
              You will be redirected to the home page in <Timer delay={DELETE_ALERT_DELAY} /> seconds
            </>
          }
        />
      );
    }, [])
  );

  const deleteErrorAlert = useAlert(
    alerts,
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
      await deleteAllIndexedDBs();
      if (isDeleteLocalStorageChecked) {
        localStorage.clear();
      }
      if (isDeleteCookiesChecked) {
        deleteAllCookies();
      }
      deleteSuccessAlert.show();
    } catch (e) {
      deleteErrorAlert.show();
    }
  }, [toggleConfirmModal, deleteSuccessAlert, isDeleteLocalStorageChecked, deleteErrorAlert, isDeleteCookiesChecked]);

  useEffect(() => {
    if (!isBrowserChromiumBased()) {
      history.replace(routes.settings.home.path({}));
    }
    setPageTitle([SETTINGS_PAGE_SECTION_TITLE, PAGE_TITLE]);
  }, [history]);

  return (
    <>
      <Alerts ref={alertsRef} width={"500px"} />
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
                  onChange={setIsDeleteCookiesChecked}
                />
                <br />
                <Checkbox
                  id="delete-localStorage"
                  label="LocalStorage"
                  description={"Delete all localStorage information."}
                  isChecked={isDeleteLocalStorageChecked}
                  onChange={setIsDeleteLocalStorageChecked}
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
          deleteMessage="By deleting this data will permanently erase your stored information."
        />
      </Page>
    </>
  );
}
