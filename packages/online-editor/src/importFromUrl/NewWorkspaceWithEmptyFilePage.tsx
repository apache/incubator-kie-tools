/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useEffect } from "react";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { AUTH_SESSION_NONE } from "../accounts/authSessions/AuthSessionApi";

export function NewWorkspaceWithEmptyFilePage(props: { extension: string }) {
  const workspaces = useWorkspaces();
  const history = useHistory();
  const routes = useRoutes();

  useEffect(() => {
    workspaces
      .createWorkspaceFromLocal({
        localFiles: [],
        gitAuthSessionId: AUTH_SESSION_NONE.id,
      })
      .then(async ({ workspace }) =>
        workspaces.addEmptyFile({
          workspaceId: workspace.workspaceId,
          destinationDirRelativePath: "",
          extension: props.extension,
        })
      )
      .then((file) => {
        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            workspaceId: file.workspaceId,
            fileRelativePath: file.relativePathWithoutExtension,
            extension: file.extension,
          }),
        });
      });
  }, [routes, history, props.extension, workspaces]);

  return (
    <OnlineEditorPage>
      <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
        <Bullseye>
          <TextContent>
            <Bullseye>
              <Spinner />
            </Bullseye>
            <br />
            <Text component={TextVariants.p}>{`Loading...`}</Text>
          </TextContent>
        </Bullseye>
      </PageSection>
    </OnlineEditorPage>
  );
}
