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
  ModalVariant,
  Button,
  Select,
  SelectOption,
  SelectDirection,
  SelectVariant
} from "@patternfly/react-core";
import { Redirect } from "react-router";
import { GlobalContext } from "../common/GlobalContext";
import { OperatingSystem, getOperatingSystem } from "../common/utils";

enum ModalState {
  SELECT_OS,
  DOWNLOADED,
  CLOSE
}

const availableOperatingSystems = new Map<OperatingSystem, string>([
  [OperatingSystem.LINUX, "Linux"],
  [OperatingSystem.MACOS, "macOS"],
  [OperatingSystem.WINDOWS, "Windows"]
]);

export function DownloadHubModal(props: {}) {
  const context = useContext(GlobalContext);

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
          title="The Business Modeler Hub Preview allows you to access:"
          isOpen={true}
          variant={ModalVariant.large}
          onClose={onClose}
          actions={[
            <a key="download" href={downloadHub} download={true}>
              <Button variant="primary" onClick={onDownload}>
                Download
              </Button>
            </a>,
            <Button key="cancel" variant="link" onClick={onClose}>
              Cancel
            </Button>
          ]}
        >
          <p>
            <strong>VS Code </strong>
            <small>
              Installs VS Code extension and gives you a convenient way to launch VS Code ready to work with Kogito.
            </small>
          </p>
          <br />
          <p>
            <strong>GitHub Chrome Extension </strong>
            <small>Provides detailed instructions on how to install Kogito GitHub Extension for Chrome.</small>
          </p>
          <br />
          <p>
            <strong>Desktop App </strong>
            <small>Installs the Business Modeler desktop app for use locally and offline.</small>
          </p>
          <br />
          <p>
            <strong>Business Modeler Preview </strong>
            <small>Provides a quick link to access the website in the same hub.</small>
          </p>
          <br />
          <p>Operation System:</p>
          <div style={{ width: "140px" }}>
            <Select
              variant={SelectVariant.single}
              aria-label="Select OS"
              onToggle={onSelectOsToggle}
              onSelect={onSelectOperatingSystem}
              selections={operationalSystem}
              isOpen={isSelectExpanded}
              aria-labelledby={"select-os"}
              isDisabled={false}
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
          title="Thank you for downloading Business Modeler Hub Preview!"
          isOpen={true}
          variant={ModalVariant.large}
          onClose={onClose}
          actions={[
            <Button key="close" variant="link" onClick={onClose}>
              Close
            </Button>
          ]}
        >
          <p>
            <small>
              If the download does not begin automatically,{" "}
              <a href={downloadHub} download={true}>
                click here
              </a>
            </small>
          </p>
        </Modal>
      )}
    </div>
  );
}
