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
import { EditorPageErrorPage } from "../../editor/EditorPageErrorPage";
import { BusinessAutomationStudioPage } from "../../home/pageTemplate/BusinessAutomationStudioPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { GIST_DEFAULT_BRANCH, GIT_DEFAULT_BRANCH } from "../services/GitService";
import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { basename, extname } from "path";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();
  const settings = useSettings();
  const queryParams = useQueryParams();
  const [error, setError] = useState<{ errors: string[]; path: string }>();

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
    async function run() {
      setError(undefined);

      if (!queryParamUrl) {
        return;
      }

      let url: URL;
      try {
        url = new URL(queryParamUrl);
      } catch (error) {
        setError({ errors: [error], path: queryParamUrl });
        return;
      }

      if (url.protocol !== "https:") {
        setError({ errors: ["Please use HTTPS."], path: queryParamUrl });
        return;
      }

      if (url.host === "github.com") {
        const match = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
          path: "/:org/:repo/(tree)?/:tree?",
          exact: true,
          strict: true,
          sensitive: false,
        });

        if (!match) {
          setError({ errors: ["Unsupported GitHub URL"], path: queryParamUrl });
          return;
        }

        if (!settings.github.token || !settings.github.user) {
          setError({ errors: ["You're not authenticated with GitHub."], path: queryParamUrl });
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
          setError({ errors: ["Unsupported Gist URL"], path: queryParamUrl });
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

      const extension = extname(url.pathname).replace(".", "");
      if (!extension) {
        setError({ errors: [`Can't determine file extension from URL.`], path: queryParamUrl });
        return;
      }

      if (extension === "zip") {
        //TODO: Import ZIP files. This is going to be import for DMN Dev Sandbox.
      }

      if (![...globals.editorEnvelopeLocator.mapping.keys()].includes(extension)) {
        setError({ errors: [`Unsupported extension '${extension}'`], path: queryParamUrl });
        return;
      }

      // import any file
      const response = await fetch(queryParamUrl);
      try {
        if (!response.ok) {
          setError({ errors: [`${response.status} - ${response.statusText}`], path: queryParamUrl });
          return;
        }

        const content = await response.text();

        await createWorkspaceForFile({
          path: basename(url.pathname),
          getFileContents: () => Promise.resolve(encoder.encode(content)),
        });
      } catch (e) {
        setError({ errors: [e], path: queryParamUrl });
        return;
      }
    }

    run();
  }, [globals, history, queryParamUrl, workspaces, createWorkspaceForFile, settings.github]);

  return (
    <>
      <BusinessAutomationStudioPage>
        {error && <EditorPageErrorPage path={error.path} errors={error.errors} />}
        {!error && (
          <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
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
