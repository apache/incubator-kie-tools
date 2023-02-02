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

import * as React from "react";
import { FunctionComponent, useMemo } from "react";
import { WorkspacesContextProvider } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContextProvider";
import { useEnv } from "../../env/hooks/EnvContext";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { PromiseModal, PromiseModalRef } from "./PromiseModal";
import { WorkspaceCommitModal } from "./WorkspaceCommitModal";
import { ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useOnlineI18n } from "../../i18n";

export const WorkspacesContextProviderWithCustomCommitMessagesModal: FunctionComponent = (props) => {
  const { env } = useEnv();
  const { i18n } = useOnlineI18n();
  const [promiseModalController, promiseModalRef] = useController<PromiseModalRef<string>>();

  const onCommitMessageRequest = useMemo(
    () => (promiseModalController ? () => promiseModalController.open() : undefined),
    [promiseModalController]
  );

  return (
    <>
      {onCommitMessageRequest && (
        <WorkspacesContextProvider
          workspacesSharedWorkerScriptUrl={"workspace/worker/sharedWorker.js"}
          shouldRequireCommitMessage={env.KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGES}
          onCommitMessageRequest={onCommitMessageRequest}
        >
          {props.children}
        </WorkspacesContextProvider>
      )}
      <PromiseModal<string> title={i18n.commitModal.title} variant={ModalVariant.medium} forwardRef={promiseModalRef}>
        {({ onReturn, onClose }) => <WorkspaceCommitModal onReturn={onReturn} onClose={onClose} />}
      </PromiseModal>
    </>
  );
};
