import { encoder, LocalFile, useWorkspaces } from "../WorkspacesContext";
import { useGlobals } from "../../common/GlobalContext";
import { matchPath, useHistory } from "react-router";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { QueryParams } from "../../common/Routes";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useSettings } from "../../settings/SettingsContext";
import { EditorPageErrorPage, Props } from "../../editor/EditorPageErrorPage";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../../common/utils";
import { BusinessAutomationStudioPage } from "../../home/pageTemplate/BusinessAutomationStudioPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { GIST_DEFAULT_BRANCH, GIT_DEFAULT_BRANCH } from "../services/GitService";
import { WorkspaceKind } from "../model/WorkspaceOrigin";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();
  const settings = useSettings();
  const queryParams = useQueryParams();
  const [fetchFileError, setFetchFileError] = useState<Props>();

  const queryParamUrl = useMemo(() => {
    return queryParams.get(QueryParams.URL);
  }, [queryParams]);

  const createWorkspaceForFile = useCallback(
    (file: LocalFile) => {
      workspaces
        .createWorkspaceFromLocal({ useInMemoryFs: false, localFiles: [file] })
        .then(({ workspace, suggestedFirstFile }) => {
          if (!suggestedFirstFile) {
            return;
          }
          history.replace({
            pathname: globals.routes.workspaceWithFilePath.path({
              workspaceId: workspace.workspaceId,
              fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
              extension: suggestedFirstFile.extension,
            }),
          });
        });
    },
    [globals, history, workspaces]
  );

  useEffect(() => {
    let canceled = false;
    async function run() {
      setFetchFileError(undefined);

      if (!queryParamUrl) {
        return;
      }

      let filePath: string;
      let url: URL;
      try {
        url = new URL(queryParamUrl);
        filePath = url.origin + url.pathname;
      } catch (error) {
        setFetchFileError({
          errors: [error],
          path: queryParamUrl,
        });
        return;
      }

      if (url.protocol !== "https:") {
        setFetchFileError({
          errors: ["Please use HTTPS."],
          path: queryParamUrl,
        });
        return;
      }

      if (url.host === "github.com") {
        const match = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
          path: "/:org/:repo/(tree)?/:tree?",
          exact: true,
          strict: true,
        });

        if (!match) {
          setFetchFileError({
            errors: ["Unsupported GitHub URL"],
            path: queryParamUrl,
          });
          return;
        }

        if (!settings.github.token || !settings.github.user) {
          setFetchFileError({
            errors: ["You're not authenticated with GitHub."],
            path: queryParamUrl,
          });
          return;
        }

        const { workspace, suggestedFirstFile } = await workspaces.createWorkspaceFromGitRepository({
          origin: { kind: WorkspaceKind.GITHUB, url, branch: match?.params.tree ?? GIT_DEFAULT_BRANCH },
          githubSettings: { user: settings.github.user, token: settings.github.token },
        });

        if (!suggestedFirstFile) {
          history.replace({ pathname: globals.routes.home.path({}) });
          return;
        }

        history.replace({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: workspace.workspaceId,
            fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
            extension: suggestedFirstFile.extension,
          }),
        });
        return;
      }

      if (url.host === "gist.github.com") {
        const match = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
          path: "/:user/:gistId",
          exact: true,
          strict: true,
        });

        if (!match) {
          setFetchFileError({
            errors: ["Unsupported Gist URL"],
            path: queryParamUrl,
          });
          return;
        }

        url.hash = "";

        const { workspace, suggestedFirstFile } = await workspaces.createWorkspaceFromGitRepository({
          origin: { kind: WorkspaceKind.GIST, url, branch: GIST_DEFAULT_BRANCH },
        });

        if (!suggestedFirstFile) {
          history.replace({ pathname: globals.routes.home.path({}) });
          return;
        }

        history.replace({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: workspace.workspaceId,
            fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
            extension: suggestedFirstFile.extension,
          }),
        });
        return;
      }

      //fetch github file
      // if (settings.github.service.isGithub(queryParamUrl) || settings.github.service.isGithubRaw(queryParamUrl)) {
      //   settings.github.service
      //     .fetchGithubFile(settings.github.octokit, queryParamUrl)
      //     .then((response) => {
      //       if (canceled) {
      //         return;
      //       }
      //
      //       return createWorkspaceForFile({
      //         path: `${extractedFileName}.${filePathExtension}`,
      //         getFileContents: () => Promise.resolve(encoder.encode(response)),
      //       });
      //     })
      //     .catch((error) => {
      //       setFetchFileError({ errors: [error], path: queryParamUrl });
      //     });
      //   return;
      // }

      // fetch any file
      const filePathExtension = extractFileExtension(filePath)!;
      const extractedFileName = decodeURIComponent(removeFileExtension(removeDirectories(filePath) ?? "unknown"));
      fetch(queryParamUrl)
        .then((response) => {
          if (canceled) {
            return;
          }

          if (!response.ok) {
            setFetchFileError({
              errors: [`${response.status} - ${response.statusText}`],
              path: queryParamUrl,
            });
            return;
          }

          // do not inline this variable.
          const content = response.text();

          return createWorkspaceForFile({
            path: `${extractedFileName}.${filePathExtension}`,
            getFileContents: () => content.then((c) => encoder.encode(c)),
          });
        })
        .catch((error) => {
          setFetchFileError({ errors: [error], path: queryParamUrl });
        });
    }
    run();

    return () => {
      canceled = true;
    };
  }, [globals.routes, history, queryParamUrl, workspaces, createWorkspaceForFile, settings.github]);

  return (
    <>
      <BusinessAutomationStudioPage>
        {fetchFileError && <EditorPageErrorPage path={fetchFileError.path} errors={fetchFileError.errors} />}
        {!fetchFileError && (
          <PageSection
            variant={"light"}
            isFilled={true}
            padding={{ default: "noPadding" }}
            className={"kogito--editor__page-section"}
          >
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Importing workspace from '${queryParamUrl}'`}</Text>
              </TextContent>
            </Bullseye>
          </PageSection>
        )}
      </BusinessAutomationStudioPage>
    </>
  );
}
