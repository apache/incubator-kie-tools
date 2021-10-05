import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmptyState, EmptyStateBody } from "@patternfly/react-core/dist/js/components/EmptyState";
import { EmptyStateIcon } from "@patternfly/react-core";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useGlobals } from "../common/GlobalContext";
import { useHistory } from "react-router";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { OnlineEditorPage } from "../home/pageTemplate/OnlineEditorPage";

export interface Props {
  errors: string[];
  path: string;
}

export function EditorPageErrorPage(props: Props) {
  const globals = useGlobals();
  const history = useHistory();
  const [showDetails, setShowDetails] = useState(false);

  const returnHome = useCallback(() => {
    history.push({ pathname: globals.routes.home.path({}) });
  }, [history, globals.routes]);

  const detailsString = useMemo(() => {
    return props.errors.join("\n");
  }, [props.errors]);

  return (
    <OnlineEditorPage>
      <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
        <br />
        <br />
        <br />
        <br />
        <br />
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{"Can't open file"}</Text>
          </TextContent>
          <EmptyStateBody>
            <PageSection>
              <TextContent style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                {`There was an error opening "${props.path}".`}
              </TextContent>
              <br />
              {props.errors && (
                <>
                  <Button variant={ButtonVariant.link} onClick={() => setShowDetails((prev) => !prev)}>
                    {showDetails ? "Hide details" : "Show details"}
                  </Button>

                  {showDetails && (
                    <PageSection variant={"light"} isFilled={true} style={{ height: "100%", minWidth: "1000px" }}>
                      <ClipboardCopy isReadOnly hoverTip="Copy" clickTip="Copied">{`${detailsString}`}</ClipboardCopy>
                    </PageSection>
                  )}
                </>
              )}
              <br />
            </PageSection>
          </EmptyStateBody>
          <Button variant={ButtonVariant.tertiary} onClick={returnHome}>
            Return home
          </Button>
        </EmptyState>
      </PageSection>
    </OnlineEditorPage>
  );
}
