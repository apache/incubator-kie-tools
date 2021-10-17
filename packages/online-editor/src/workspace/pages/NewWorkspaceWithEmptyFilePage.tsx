import { encoder, useWorkspaces } from "../WorkspacesContext";
import * as React from "react";
import { useEffect } from "react";
import { useHistory } from "react-router";
import { useGlobals } from "../../common/GlobalContext";
import { OnlineEditorPage } from "../../home/pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";

export function NewWorkspaceWithEmptyFilePage(props: { extension: string }) {
  const workspaces = useWorkspaces();
  const history = useHistory();
  const globals = useGlobals();

  useEffect(() => {
    workspaces
      .createWorkspaceFromLocal([
        {
          path: "Untitled." + props.extension,
          getFileContents: () => Promise.resolve(encoder.encode("")),
        },
      ])
      .then(({ suggestedFirstFile }) => {
        history.replace({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: suggestedFirstFile!.workspaceId,
            fileRelativePath: suggestedFirstFile!.relativePathWithoutExtension,
            extension: suggestedFirstFile!.extension,
          }),
        });
      });
  }, [globals, history, props.extension, workspaces]);

  return (
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
            <Text component={TextVariants.p}>{`Loading...`}</Text>
          </TextContent>
        </Bullseye>
      </PageSection>
    </OnlineEditorPage>
  );
}
