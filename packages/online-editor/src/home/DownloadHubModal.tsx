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
import { Modal, Button, Select, SelectOption, SelectDirection, SelectVariant } from "@patternfly/react-core";
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

  const onDownload = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    setIsDownloadModal(true);
  };

  const onClose = () => {
    setRedirectToHome(true);
  };

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

  const downloadHubUrl = useMemo(() => {
    switch (operationalSystem) {
      case OperatingSystem.MACOS:
        return `samples/macos.dmn`;
      case OperatingSystem.WINDOWS:
        return `samples/windows.dmn`;
      default:
        return `samples/linux.dmn`;
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

  const selectOperationalSystem = (
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
  );

  const spinner = (
    <div>
      <div className="pf-l-bullseye">
        <div className="pf-c-empty-state pf-m-lg">
          <div className="pf-u-mb-lg">
            <div className="pf-c-spinner" role="progressbar" aria-valuetext="Loading...">
              <div className="pf-c-spinner__clipper" />
              <div className="pf-c-spinner__lead-ball" />
              <div className="pf-c-spinner__tail-ball" />
            </div>
          </div>
          <h5 className="pf-c-title pf-m-lg">Loading...</h5>
          <div className="pf-c-empty-state__body" />
        </div>
      </div>
    </div>
  );

  return (
    <div>
      {redirectToHome && <Redirect push={true} to={context.routes.home.url({})} />}
      {!isDownloadModal ? (
        <Modal
          title="The Kogito end-to-end hub allows you to access:"
          isOpen={true}
          isLarge={true}
          actions={[
            <Button key="confirm" variant="primary" onClick={onDownload}>
              Download
            </Button>,
            <Button key="cancel" variant="link" onClick={onClose}>
              Cancel
            </Button>
          ]}
          onClose={onClose}
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
            {selectOperationalSystem}
          </div>
        </Modal>
      ) : (
        <Modal
          title="Downloading Business Modeler..."
          isOpen={true}
          isLarge={true}
          actions={[
            <Button key="cancel" variant="link" onClick={onClose}>
              Cancel
            </Button>
          ]}
          onClose={onClose}
        >
          {spinner}
          <p>
            If the download does not begin automatically{" "}
            <a href={downloadHubUrl} download={`${operationalSystem.toLowerCase()}.dmn`}>
              click here
            </a>
          </p>
        </Modal>
      )}
    </div>
  );
}
