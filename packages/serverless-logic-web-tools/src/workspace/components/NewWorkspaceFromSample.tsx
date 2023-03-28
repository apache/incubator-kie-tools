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
import { useRoutes } from "../../navigation/Hooks";
import { useHistory } from "react-router";
import { useCallback, useEffect, useState } from "react";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParam } from "../../queryParams/QueryParamsContext";
import { OnlineEditorPage } from "../../pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettingsDispatch } from "../../settings/SettingsContext";
import { EditorPageErrorPage } from "../../editor/EditorPageErrorPage";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { fetchSample, kieSamplesRepo } from "../../home/sample/sampleApi";

export function NewWorkspaceFromSample() {
  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const history = useHistory();
  const settingsDispatch = useSettingsDispatch();
  const [openingError, setOpeningError] = useState("");

  const sampleId = useQueryParam(QueryParams.SAMPLE_ID) ?? "";

  const createWorkspaceForFiles = useCallback(
    async (files: LocalFile[]) => {
      workspaces.createWorkspaceFromLocal({ localFiles: files }).then(({ workspace, suggestedFirstFile }) => {
        if (!suggestedFirstFile) {
          return;
        }
        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            workspaceId: workspace.workspaceId,
            fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
            extension: suggestedFirstFile.extension,
          }),
        });
      });
    },
    [routes, history, workspaces]
  );

  useEffect(() => {
    async function run() {
      // proceed normally
      try {
        fetchSample({ octokit: settingsDispatch.github.octokit, sampleId }).then((sampleFiles) => {
          createWorkspaceForFiles(sampleFiles);
        });
      } catch (e) {
        setOpeningError(e.toString());
        return;
      }
    }

    run();
  }, [sampleId]);

  return (
    <>
      <OnlineEditorPage>
        {openingError && <EditorPageErrorPage path={`${kieSamplesRepo.path}/${sampleId}`} errors={[openingError]} />}
        {!openingError && (
          <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Loading sample...`}</Text>
              </TextContent>
            </Bullseye>
          </PageSection>
        )}
      </OnlineEditorPage>
    </>
  );
}
