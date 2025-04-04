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
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useRoutes } from "../navigation/Hooks";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { AUTH_SESSION_NONE } from "../authSessions/AuthSessionApi";

export function NewWorkspaceWithEmptyFilePage() {
  const workspaces = useWorkspaces();
  const navigate = useNavigate();
  const { extension } = useParams<{ extension: string }>();
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
          extension: extension!,
        })
      )
      .then((file) => {
        navigate(
          {
            pathname: routes.workspaceWithFilePath.path({
              workspaceId: file.workspaceId,
              fileRelativePath: file.relativePath,
            }),
          },
          { replace: true }
        );
      });
  }, [routes, navigate, extension, workspaces]);

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
