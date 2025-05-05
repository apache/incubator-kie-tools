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

import React, { useCallback, useState, useEffect } from "react";
import { ResponsiveDropdown } from "../../../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../../../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useEditorToolbarContext } from "../EditorToolbarContextProvider";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import {
  useAcceleratorsDispatch,
  useAvailableAccelerators,
  useCurrentAccelerator,
} from "../../../accelerators/AcceleratorsHooks";
import { DropdownItem, Select, SelectOption } from "@patternfly/react-core/deprecated";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { AcceleratorConfig } from "../../../accelerators/AcceleratorsApi";
import { useOnlineI18n } from "../../../i18n";
import { AcceleratorModal } from "./AcceleratorModal";
import { AcceleratorIcon } from "./AcceleratorIcon";
import { useClonableUrl, useImportableUrlValidation } from "../../../importFromUrl/ImportableUrlHooks";
import { GitAuthSession, isGitAuthSession } from "../../../authSessions/AuthSessionApi";
import { Divider, Tooltip } from "@patternfly/react-core/components";
import { CheckCircleIcon, TimesCircleIcon } from "@patternfly/react-icons/dist/js";
import { useAuthSession, useAuthSessions } from "../../../authSessions/AuthSessionsContext";
import { AuthProviderType } from "../../../authProviders/AuthProvidersApi";

interface AcceleratorAccessibilityResult {
  acceleratorName: string;
  validated: boolean;
  success: boolean;
  errorMessage?: string;
}

interface Props {
  workspaceFile: WorkspaceFile;
}

const AcceleratorDropdownItem: React.FC<{
  accelerator: AcceleratorConfig;
  authInfo: any;
  authSession: any;
  selectedAuthSessionId?: string;
  onOpenApplyAccelerator: (accelerator: AcceleratorConfig) => void;
}> = ({ accelerator, authInfo, authSession, selectedAuthSessionId, onOpenApplyAccelerator }) => {
  const clonableUrl = useClonableUrl(
    accelerator.gitRepositoryUrl,
    authInfo,
    accelerator.gitRepositoryGitRef ?? "main",
    false
  );

  const validation = useImportableUrlValidation(
    authSession,
    accelerator.gitRepositoryUrl,
    accelerator.gitRepositoryGitRef ?? "main",
    clonableUrl,
    React.useRef(null)
  );

  const isDisabled = !selectedAuthSessionId || validation.option === "error";
  const errorMessage = validation.helperTextInvalid;

  // Tooltip content for the main dropdown item
  const dropdownItemTooltip = isDisabled
    ? !selectedAuthSessionId
      ? "Please select an authentication session first"
      : errorMessage ?? "You don't have access to this Git repository."
    : undefined;

  return (
    <Tooltip
      content={dropdownItemTooltip}
      position="right"
      isVisible={isDisabled} // Force show when disabled
    >
      <div>
        {" "}
        {/* Wrapper needed for tooltip positioning */}
        <DropdownItem
          key={accelerator.name}
          icon={<AcceleratorIcon iconUrl={accelerator.iconUrl} />}
          isDisabled={isDisabled}
          onClick={() => !isDisabled && onOpenApplyAccelerator(accelerator)}
        >
          <span style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <span>{accelerator.name}</span>
            {selectedAuthSessionId && validation.option === "error" && (
              <Tooltip content={errorMessage}>
                <TimesCircleIcon color="red" />
              </Tooltip>
            )}
            {selectedAuthSessionId && validation.option === "success" && (
              <Tooltip content="You can clone this accelerator.">
                <CheckCircleIcon color="green" />
              </Tooltip>
            )}
          </span>
        </DropdownItem>
      </div>
    </Tooltip>
  );
};

export function AcceleratorsDropdown(props: Props) {
  const { i18n } = useOnlineI18n();
  const { workspace } = useEditorToolbarContext();
  const accelerators = useAvailableAccelerators();
  const { applyAcceleratorToWorkspace } = useAcceleratorsDispatch(workspace);
  const workspaceAuthSession = useAuthSession(workspace.descriptor.gitAuthSessionId).authSession;

  const [isApplyModalOpen, setApplyModalOpen] = useState(false);
  const [selectedAccelerator, setSelectedAccelerator] = useState<AcceleratorConfig | undefined>();
  const { authSessions } = useAuthSessions();
  const [selectedAuthSessionId, setSelectedAuthSessionId] = useState<string | undefined>();
  const { authInfo, authSession } = useAuthSession(selectedAuthSessionId);
  const [isAcceleratorsDropdownOpen, setIsAcceleratorsDropdownOpen] = useState(false);
  const [isAuthSelectOpen, setIsAuthSelectOpen] = useState(false);

  const currentAccelerator = useCurrentAccelerator(props.workspaceFile.workspaceId);

  useEffect(() => {
    if (isAcceleratorsDropdownOpen && !selectedAuthSessionId && workspaceAuthSession) {
      setSelectedAuthSessionId(workspaceAuthSession.id);
    }
  }, [isAcceleratorsDropdownOpen, selectedAuthSessionId, workspaceAuthSession]);
  useEffect(() => {
    if (isAcceleratorsDropdownOpen && !selectedAuthSessionId) {
      if (workspaceAuthSession) {
        setSelectedAuthSessionId(workspaceAuthSession.id);
      } else if (authSessions.size > 0) {
        const firstSessionId = Array.from(authSessions.keys())[0];
        setSelectedAuthSessionId(firstSessionId);
      }
    }
  }, [isAcceleratorsDropdownOpen, selectedAuthSessionId, workspaceAuthSession, authSessions]);

  const onOpenApplyAccelerator = useCallback(
    (accelerator: AcceleratorConfig) => {
      if (!selectedAuthSessionId) {
        console.warn("No auth session selected");
        return;
      }
      setSelectedAccelerator(accelerator);
      setApplyModalOpen(true);
      setIsAcceleratorsDropdownOpen(false);
    },
    [selectedAuthSessionId]
  );

  const onApplyAccelerator = useCallback(async () => {
    if (!selectedAccelerator || !selectedAuthSessionId) {
      console.error("Missing required parameters to apply accelerator");
      return;
    }
    const authSession = authSessions.get(selectedAuthSessionId);
    try {
      await applyAcceleratorToWorkspace(selectedAccelerator, props.workspaceFile, {
        authInfo: { username: (authSession as GitAuthSession).login, password: (authSession as GitAuthSession).token },
        gitRef: selectedAccelerator.gitRepositoryGitRef ?? "main",
        insecurelyDisableTlsCertificateValidation: false,
      });
    } catch (error) {
      console.error("Failed to apply accelerator:", error);
    } finally {
      setSelectedAccelerator(undefined);
      setApplyModalOpen(false);
    }
  }, [applyAcceleratorToWorkspace, authSessions, props.workspaceFile, selectedAccelerator, selectedAuthSessionId]);

  const mapAuthProviderId = (authProviderId: string): AuthProviderType | undefined => {
    if (authProviderId.includes("github")) return AuthProviderType.github;
    if (authProviderId.includes("bitbucket")) return AuthProviderType.bitbucket;
    if (authProviderId.includes("gitlab")) return AuthProviderType.gitlab;
    return undefined;
  };

  if (currentAccelerator) {
    return <></>;
  }

  return (
    <>
      <ResponsiveDropdown
        title="Accelerators"
        className="kie-tools--masthead-hoverable"
        isPlain
        onClose={() => setIsAcceleratorsDropdownOpen(false)}
        onSelect={() => setIsAcceleratorsDropdownOpen(false)}
        position="right"
        isOpen={isAcceleratorsDropdownOpen}
        toggle={
          <ResponsiveDropdownToggle
            onToggle={() => setIsAcceleratorsDropdownOpen((prev) => !prev)}
            toggleIndicator={CaretDownIcon}
            icon={<i>🚀</i>}
          >
            {i18n.accelerators.applyAccelerator}
          </ResponsiveDropdownToggle>
        }
        dropdownItems={[
          <div style={{ padding: "8px 16px", minWidth: "400px" }} key={"auth-session-select"}>
            <Select
              aria-label="Auth session"
              variant="single"
              isDisabled={authSessions.size === 0}
              onToggle={() => setIsAuthSelectOpen((prev) => !prev)}
              onSelect={(_, value) => {
                setSelectedAuthSessionId(value as string);
                setIsAuthSelectOpen(false);
              }}
              selections={selectedAuthSessionId}
              isOpen={isAuthSelectOpen}
              placeholderText="Select authentication"
            >
              {Array.from(authSessions.entries())
                .filter(([_, session]) => isGitAuthSession(session))
                .map(([id, session]) => (
                  <SelectOption key={id} value={id}>
                    {`${mapAuthProviderId((session as GitAuthSession).authProviderId)} : ${(session as GitAuthSession).login}`}
                  </SelectOption>
                ))}
            </Select>
            <Divider />
          </div>,
          ...accelerators.map((acc) => (
            <AcceleratorDropdownItem
              key={acc.name}
              accelerator={acc}
              authInfo={authInfo}
              authSession={authSession}
              selectedAuthSessionId={selectedAuthSessionId}
              onOpenApplyAccelerator={onOpenApplyAccelerator}
            />
          )),
        ]}
      />
      {selectedAccelerator && (
        <AcceleratorModal
          isOpen={isApplyModalOpen}
          onClose={() => setApplyModalOpen(false)}
          onApplyAccelerator={onApplyAccelerator}
          accelerator={selectedAccelerator}
          isApplying={true}
          authSession={authSession}
        />
      )}
    </>
  );
}
