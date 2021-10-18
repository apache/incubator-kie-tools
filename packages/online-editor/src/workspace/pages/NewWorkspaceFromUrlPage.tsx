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
import { OnlineEditorPage } from "../../home/pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";

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

  const isSample = useMemo(() => {
    if (!queryParamUrl) {
      return false;
    }

    const fileExtension = extractFileExtension(queryParamUrl);
    if (!fileExtension || !Array.from(globals.editorEnvelopeLocator.mapping.keys()).includes(fileExtension)) {
      return false;
    }

    return queryParamUrl === globals.routes.static.sample.path({ type: fileExtension });
  }, [globals.editorEnvelopeLocator.mapping, globals.routes.static.sample, queryParamUrl]);

  const createWorkspaceForFile = useCallback(
    (file: LocalFile) => {
      workspaces.createWorkspaceFromLocal([file]).then(({ workspace, suggestedFirstFile }) => {
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
    setFetchFileError(undefined);

    if (!queryParamUrl) {
      return;
    }

    let filePath: string;
    if (isSample) {
      filePath = queryParamUrl;
    } else {
      try {
        const validUrl = new URL(queryParamUrl);
        filePath = validUrl.origin + validUrl.pathname;
      } catch (error) {
        setFetchFileError({
          errors: [error],
          path: queryParamUrl,
        });
        return;
      }
    }

    if (settings.github.service.isGithub(queryParamUrl)) {
      const url = new URL(queryParamUrl);
      const match = matchPath(url.pathname, { path: "/:org/:repo", exact: true, strict: true });
      if (match) {
        if (!settings.github.token || !settings.github.user) {
          return;
        }
        workspaces
          .createWorkspaceFromGitRepository(url, "main", {
            user: {
              login: settings.github.user.login,
              email: settings.github.user.email,
              name: settings.github.user.name,
            },
            token: settings.github.token,
          })
          .then(() => {
            history.replace({ pathname: globals.routes.home.path({}) });
          });
        return;
      }
    }

    const filePathExtension = extractFileExtension(filePath)!;
    const extractedFileName = decodeURIComponent(removeFileExtension(removeDirectories(filePath) ?? "unknown"));

    if (settings.github.service.isGist(queryParamUrl)) {
      settings.github.service
        .fetchGistFile(settings.github.octokit, queryParamUrl)
        .then((content) => {
          if (canceled) {
            return;
          }

          return createWorkspaceForFile({
            path: `${extractedFileName}.${filePathExtension}`,
            getFileContents: () => Promise.resolve(encoder.encode(content)),
          });
        })
        .catch((error) => setFetchFileError({ errors: [error], path: queryParamUrl }));
      return;
    }

    if (settings.github.service.isGithub(queryParamUrl) || settings.github.service.isGithubRaw(queryParamUrl)) {
      settings.github.service
        .fetchGithubFile(settings.github.octokit, queryParamUrl)
        .then((response) => {
          if (canceled) {
            return;
          }

          return createWorkspaceForFile({
            path: `${extractedFileName}.${filePathExtension}`,
            getFileContents: () => Promise.resolve(encoder.encode(response)),
          });
        })
        .catch((error) => {
          setFetchFileError({ errors: [error], path: queryParamUrl });
        });
      return;
    }

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

    return () => {
      canceled = true;
    };
  }, [globals.routes, history, queryParamUrl, workspaces, isSample, createWorkspaceForFile, settings.github]);

  return (
    <>
      {fetchFileError && <EditorPageErrorPage path={fetchFileError.path} errors={fetchFileError.errors} />}
      {!fetchFileError && (
        <OnlineEditorPage>
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
        </OnlineEditorPage>
      )}
    </>
  );
}
