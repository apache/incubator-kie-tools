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

import * as React from "react";
import { FunctionComponent, useMemo } from "react";
import { WorkspacesContextProvider } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContextProvider";
import { useEnv } from "../../env/hooks/EnvContext";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { PromiseModal, PromiseModalRef } from "./PromiseModal";
import { WorkspaceCommitModal, WorkspaceCommitModalArgs } from "./WorkspaceCommitModal";
import { ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useOnlineI18n } from "../../i18n";

export const WorkspacesContextProviderWithCustomCommitMessagesModal: FunctionComponent = (props) => {
  const { env } = useEnv();
  const { i18n } = useOnlineI18n();
  const [promiseModalController, promiseModalRef] = useController<PromiseModalRef<string, WorkspaceCommitModalArgs>>();

  const onCommitMessageRequest = useMemo(
    () =>
      promiseModalController
        ? (defaultCommitMessage?: string) => promiseModalController.open({ defaultCommitMessage })
        : undefined,
    [promiseModalController]
  );

  return (
    <>
      {onCommitMessageRequest && (
        <WorkspacesContextProvider
          workspacesSharedWorkerScriptUrl={"workspace/worker/sharedWorker.js"}
          shouldRequireCommitMessage={env.KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE}
          onCommitMessageRequest={onCommitMessageRequest}
          workerNamePrefix={`kie-sandbox-${env.KIE_SANDBOX_VERSION}`}
        >
          {props.children}
        </WorkspacesContextProvider>
      )}
      <PromiseModal<string, WorkspaceCommitModalArgs>
        title={i18n.commitModal.title}
        variant={ModalVariant.medium}
        forwardRef={promiseModalRef}
      >
        {({ onReturn, onClose, args }) => <WorkspaceCommitModal onReturn={onReturn} onClose={onClose} args={args} />}
      </PromiseModal>
    </>
  );
};
