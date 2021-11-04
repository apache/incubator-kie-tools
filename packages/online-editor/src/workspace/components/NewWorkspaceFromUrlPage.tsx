import { encoder, LocalFile, useWorkspaces } from "../WorkspacesContext";
import { useGlobals } from "../../common/GlobalContext";
import { useHistory } from "react-router";
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
import { basename } from "path";
import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { GIST_DEFAULT_BRANCH, GIT_DEFAULT_BRANCH } from "../services/GitService";
import { UrlType, useImportableUrl } from "../hooks/ImportableUrlHooks";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();
  const settings = useSettings();
  const queryParams = useQueryParams();
  const [importingError, setImportingError] = useState("");

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

  const importableUrl = useImportableUrl(queryParamUrl);

  useEffect(() => {
    async function run() {
      try {
        if (importableUrl.errors) {
          return;
        }

        // github
        if (importableUrl.type === UrlType.GITHUB) {
          const githubSettings =
            settings.github.user && settings.github.token
              ? { user: settings.github.user, token: settings.github.token }
              : undefined;

          const { workspace, suggestedFirstFile } = await workspaces.createWorkspaceFromGitRepository({
            origin: {
              kind: WorkspaceKind.GITHUB,
              url: importableUrl.url,
              branch: importableUrl.branch ?? GIT_DEFAULT_BRANCH,
            },
            githubSettings,
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
        }

        // gist
        else if (importableUrl.type === UrlType.GIST) {
          importableUrl.url.hash = "";

          const { workspace, suggestedFirstFile } = await workspaces.createWorkspaceFromGitRepository({
            origin: { kind: WorkspaceKind.GIST, url: importableUrl.url, branch: GIST_DEFAULT_BRANCH },
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
        }

        // any
        else if (importableUrl.type === UrlType.FILE) {
          const response = await fetch(importableUrl.url.toString());
          if (!response.ok) {
            setImportingError(`${response.status}${response.statusText ? `- ${response.statusText}` : ""}`);
            return;
          }

          const content = await response.text();

          await createWorkspaceForFile({
            path: basename(importableUrl.url.pathname),
            getFileContents: () => Promise.resolve(encoder.encode(content)),
          });
        }

        // zip
        else if (importableUrl.type === UrlType.ZIP) {
          throw new Error("Importing ZIPs is not supported yet.");
        }

        // invalid
        else {
          throw new Error("Invalid UrlType " + importableUrl.type);
        }
      } catch (e) {
        setImportingError(e.toString());
        return;
      }
    }

    run();
  }, [createWorkspaceForFile, globals, history, importableUrl, settings.github, workspaces]);

  return (
    <>
      <BusinessAutomationStudioPage>
        {importableUrl.errors && <EditorPageErrorPage path={importableUrl.url} errors={importableUrl.errors} />}
        {importingError && <EditorPageErrorPage path={importableUrl.url.toString()} errors={[importingError]} />}
        {!importableUrl.errors && (
          <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Importing workspace from '${importableUrl.url.toString()}'`}</Text>
              </TextContent>
            </Bullseye>
          </PageSection>
        )}
      </BusinessAutomationStudioPage>
    </>
  );
}
