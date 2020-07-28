/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useContext, useMemo, useState } from "react";
import {
  Modal,
  Button,
  Select,
  SelectOption,
  SelectDirection,
  SelectVariant,
  ModalBoxFooter
} from "@patternfly/react-core";
import { Redirect } from "react-router";
import { GlobalContext } from "../common/GlobalContext";
import { OperatingSystem, getOperatingSystem } from "../common/utils";
import { useOnlineI18n } from "../common/i18n";

enum ModalState {
  SELECT_OS,
  DOWNLOADED,
  CLOSE
}

export function DownloadHubModal(props: {}) {
  const context = useContext(GlobalContext);
  const { i18n } = useOnlineI18n();

  const [modalState, setModalState] = useState(ModalState.SELECT_OS);
  const [operationalSystem, setOperationalSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [isSelectExpanded, setSelectIsExpanded] = useState(false);

  const onDownload = useCallback((e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    setModalState(ModalState.DOWNLOADED);
  }, []);

  const onClose = useCallback(() => {
    setModalState(ModalState.CLOSE);
  }, []);

  const onSelectOsToggle = useCallback(isExpanded => {
    setSelectIsExpanded(isExpanded);
  }, []);

  const onSelectOperatingSystem = useCallback((e, selection) => {
    setOperationalSystem(selection);
    setSelectIsExpanded(false);
  }, []);

  const availableOperatingSystems = useMemo(
    () =>
      new Map<OperatingSystem, string>([
        [OperatingSystem.LINUX, i18n.names.linux],
        [OperatingSystem.MACOS, i18n.names.macos],
        [OperatingSystem.WINDOWS, i18n.names.windows]
      ]),
    []
  );

  const downloadHub = useMemo(() => {
    switch (operationalSystem) {
      case OperatingSystem.MACOS:
        return "$_{WEBPACK_REPLACE__hubMacOsUrl}";
      case OperatingSystem.WINDOWS:
        return "$_{WEBPACK_REPLACE__hubWindowsUrl}";
      case OperatingSystem.LINUX:
      default:
        return "$_{WEBPACK_REPLACE__hubLinuxUrl}";
    }
  }, [operationalSystem]);

  return (
    <div>
      {modalState === ModalState.CLOSE && <Redirect push={true} to={context.routes.home.url({})} />}
      {modalState === ModalState.SELECT_OS && (
        <Modal
          title={`${i18n.downloadHubModal.beforeDownload.title}:`}
          isOpen={true}
          isLarge={true}
          onClose={onClose}
          footer={
            <ModalBoxFooter
              className="kogito--editor-hub-download_modal-footer"
              children={
                <div>
                  <a key="download" href={downloadHub} download={true}>
                    <Button variant="primary" onClick={onDownload}>
                      {i18n.terms.download}
                    </Button>
                  </a>
                  <Button key="cancel" variant="link" onClick={onClose}>
                    {i18n.terms.cancel}
                  </Button>
                </div>
              }
            />
          }
        >
          <p>
            <strong>{i18n.names.vscode} </strong>
            <small>{i18n.downloadHubModal.beforeDownload.vscodeDescription}</small>
          </p>
          <br />
          <p>
            <strong>{i18n.downloadHubModal.beforeDownload.githubChromeExtension.title} </strong>
            <small>{i18n.downloadHubModal.beforeDownload.githubChromeExtension.description}</small>
          </p>
          <br />
          <p>
            <strong>{i18n.downloadHubModal.beforeDownload.desktop.title} </strong>
            <small>{i18n.downloadHubModal.beforeDownload.desktop.description}</small>
          </p>
          <br />
          <p>
            <strong>{i18n.downloadHubModal.beforeDownload.businessModeler.title} </strong>
            <small>{i18n.downloadHubModal.beforeDownload.businessModeler.description}</small>
          </p>
          <br />
          <p>{i18n.terms.os.full}:</p>
          <div>
            <Select
              variant={SelectVariant.single}
              aria-label="Select Input"
              onToggle={onSelectOsToggle}
              onSelect={onSelectOperatingSystem}
              selections={operationalSystem}
              isExpanded={isSelectExpanded}
              ariaLabelledBy={"select-os"}
              isDisabled={false}
              width={"135px"}
              direction={SelectDirection.up}
            >
              {Array.from(availableOperatingSystems.entries()).map(([key, label]) => (
                <SelectOption isDisabled={false} key={key} value={key} isPlaceholder={false}>
                  {label}
                </SelectOption>
              ))}
            </Select>
          </div>
        </Modal>
      )}
      {modalState === ModalState.DOWNLOADED && (
        <Modal
          title={i18n.downloadHubModal.afterDownload.title + "!"}
          isOpen={true}
          isLarge={true}
          onClose={onClose}
          footer={
            <ModalBoxFooter
              className="kogito--editor-hub-download_modal-footer"
              children={
                <div>
                  <Button key="close" variant="link" onClick={onClose}>
                    {i18n.terms.close}
                  </Button>
                </div>
              }
            />
          }
        >
          <p>
            <small>
              {i18n.downloadHubModal.afterDownload.message}{" "}
              <a href={downloadHub} download={true}>
                {i18n.downloadHubModal.afterDownload.link}
              </a>
            </small>
          </p>
        </Modal>
      )}
    </div>
  );
}
