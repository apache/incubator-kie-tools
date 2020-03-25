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

const LINUX = "Linux";
const MACOS = "Mac OS";
const WINDOWS = "Windows";

export function DownloadHubModal(props: {}) {
  const context = useContext(GlobalContext);

  const [redirectToHome, setRedirectToHome] = useState(false);
  const [isDownloadModal, setIsDownloadModal] = useState(false);
  const [operationalSystem, setOperationalSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [isSelectExpanded, setSelectIsExpanded] = useState(false);

  const onDownload = useCallback((e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    setIsDownloadModal(true);
  }, []);

  const onClose = useCallback(() => {
    setRedirectToHome(true);
  }, []);

  const onToggle = useCallback(
    isExpanded => {
      setSelectIsExpanded(isExpanded);
    },
    [isSelectExpanded]
  );

  const onSelect = useCallback((e, selection, isPlaceholder) => {
    setOperationalSystem(chosenOs(selection));
    setSelectIsExpanded(false);
  }, []);

  const downloadHub = useMemo(() => {
    switch (operationalSystem) {
      case OperatingSystem.MACOS:
        return `samples/business-modeler-hub-macos.zip`;
      case OperatingSystem.WINDOWS:
        return `samples/business-modeler-hub-windows.zip`;
      default:
        return `samples/business-modeler-hub-linux.zip`;
    }
  }, [operationalSystem]);

  const chosenOs = useCallback(
    (selection: string) => {
      switch (selection) {
        case MACOS:
          return OperatingSystem.MACOS;
        case WINDOWS:
          return OperatingSystem.WINDOWS;
        default:
          return OperatingSystem.LINUX;
      }
    },
    [operationalSystem]
  );

  const availableOs = useMemo(() => {
    return new Map<OperatingSystem, string>([
      [OperatingSystem.LINUX, LINUX],
      [OperatingSystem.MACOS, MACOS],
      [OperatingSystem.WINDOWS, WINDOWS]
    ]);
  }, []);

  const selectOptions = useMemo(() => {
    return Array.from(availableOs.values()).map(value => ({
      value,
      disabled: false,
      isPlaceholder: false
    }));
  }, []);

  return (
    <div>
      {redirectToHome && <Redirect push={true} to={context.routes.home.url({})} />}
      {!isDownloadModal ? (
        <Modal
          title="The Kogito end-to-end hub allows you to access:"
          isOpen={true}
          isLarge={true}
          onClose={onClose}
          footer={
            <ModalBoxFooter
              className="kogito--editor-modal-download-hub-footer"
              children={
                <div>
                  <a key="download" href={downloadHub} download={true}>
                    <Button variant="primary" onClick={onDownload}>
                      Download
                    </Button>
                  </a>
                  <Button key="cancel" variant="link" onClick={onClose}>
                    Cancel
                  </Button>
                </div>
              }
            />
          }
        >
          <p>
            <strong>VS Code </strong>
            <small>
              Installs VS Code extension and gives you a convenient way to launch VS Code ready to work with Kogito
            </small>
          </p>
          <br />
          <p>
            <strong>GitHub Chrome Extension </strong>
            <small>Installs the Kogito GitHub extension for Chrome and gives you a shortcut to launch</small>
          </p>
          <br />
          <p>
            <strong>Desktop App </strong>
            <small>Installs the Business Modeler desktop app for use locally and offline</small>
          </p>
          <br />
          <p>
            <strong>Business Modeler Preview </strong>
            <small>Provides a quick link to access the website in the same hub</small>
          </p>
          <br />
          <p>Operation System:</p>
          <div>
            <Select
              variant={SelectVariant.single}
              aria-label="Select Input"
              onToggle={onToggle}
              onSelect={onSelect}
              selections={availableOs.get(operationalSystem)}
              isExpanded={isSelectExpanded}
              ariaLabelledBy={"select-os"}
              isDisabled={false}
              width={135} // FIXME
              direction={SelectDirection.up}
            >
              {selectOptions.map((option, index) => (
                <SelectOption
                  isDisabled={option.disabled}
                  key={index}
                  value={option.value}
                  isPlaceholder={option.isPlaceholder}
                />
              ))}
            </Select>
          </div>
        </Modal>
      ) : (
        <Modal
          title="Thank you for download the Business Modeler HUB!"
          isOpen={true}
          isLarge={true}
          onClose={onClose}
          footer={
            <ModalBoxFooter
              className="kogito--editor-modal-download-hub-footer"
              children={
                <div>
                  <Button key="close" variant="link" onClick={onClose}>
                    Close
                  </Button>
                </div>
              }
            />
          }
        >
          <p>
            <small>
              If the download does not begin automatically <a href={downloadHub} download={true}>click here</a>
            </small>
          </p>
        </Modal>
      )}
    </div>
  );
}
