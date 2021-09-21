import { Page, PageHeader, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { EmptyStateIcon } from "@patternfly/react-core";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useGlobals } from "../common/GlobalContext";
import { File } from "@kie-tooling-core/editor/dist/channel";
import { useHistory } from "react-router";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { extractFileExtension } from "../common/utils";
import { useQueryParams } from "../queryParams/QueryParamsContext";

export enum FetchFileErrorReason {
  DIFFERENT_EXTENSION,
  CANT_FETCH,
}

export interface FetchFileError {
  details?: string;
  filePath: string;
  reason: FetchFileErrorReason;
}

export function EditorFetchFileErrorEmptyState(props: { currentFile: File; fetchFileError: FetchFileError }) {
  const globals = useGlobals();
  const history = useHistory();
  const queryParams = useQueryParams();
  const [showDetails, setShowDetails] = useState(false);

  const onClose = useCallback(() => {
    history.push({ pathname: globals.routes.home.path({}) });
  }, [history, globals.routes]);

  const fileExtension = useMemo(() => {
    return extractFileExtension(props.fetchFileError.filePath)!;
  }, [props.fetchFileError]);

  const openOnRightEditor = useCallback(() => {
    history.push({
      pathname: globals.routes.editor.path({ extension: fileExtension }),
      search: globals.routes.editor.queryArgs(queryParams).toString(),
    });
  }, [history, globals.routes.editor, fileExtension, queryParams]);

  const isFileExtensionSupported = useMemo(() => {
    return Array.from(globals.editorEnvelopeLocator.mapping.keys()).includes(fileExtension);
  }, [globals.editorEnvelopeLocator, fileExtension]);

  return (
    <Page
      header={
        <PageHeader
          logo={
            <Brand
              src={globals.routes.static.images.editorLogo.path({ type: props.currentFile.fileExtension })}
              alt={`${props.currentFile.fileExtension} kogito logo`}
            />
          }
          logoProps={{ onClick: onClose }}
          headerTools={[]}
          className={"kogito--editor__toolbar"}
          aria-label={"Page header"}
        />
      }
    >
      <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
        <br />
        <br />
        <br />
        <br />
        <br />
        {/*<Bullseye>*/}
        {props.fetchFileError.reason === FetchFileErrorReason.DIFFERENT_EXTENSION && (
          <EmptyState>
            <EmptyStateIcon icon={InfoCircleIcon} />
            <TextContent>
              <Text component={"h2"}>{"Oops!"}</Text>
            </TextContent>
            <EmptyStateBody style={{ maxWidth: "800px" }}>
              {`The requested file cannot be opened by the ${props.currentFile.fileExtension.toUpperCase()} Editor.`}
              <br />
              {isFileExtensionSupported && (
                <>{`You can open this file using the ${fileExtension.toUpperCase()} Editor`}</>
              )}
              {!isFileExtensionSupported && (
                <>{`Unfortunately there's no support for ".${fileExtension}" files at this moment.`}</>
              )}
            </EmptyStateBody>
            <br />
            {isFileExtensionSupported && (
              <Button variant={ButtonVariant.primary} onClick={openOnRightEditor}>
                {`Open on ${fileExtension.toUpperCase()} Editor`}
              </Button>
            )}
            <EmptyStateSecondaryActions>
              <Button variant={ButtonVariant.link} onClick={onClose}>
                Return home
              </Button>
            </EmptyStateSecondaryActions>
          </EmptyState>
        )}
        {props.fetchFileError.reason === FetchFileErrorReason.CANT_FETCH && (
          <EmptyState>
            <EmptyStateIcon icon={ExclamationTriangleIcon} />
            <TextContent>
              <Text component={"h2"}>{"Can't open file"}</Text>
            </TextContent>
            <EmptyStateBody style={{ maxWidth: "800px" }}>
              <PageSection>
                <TextContent style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                  {"There was an error opening the file from "}
                  <a href={props.fetchFileError.filePath}>{props.fetchFileError.filePath}</a>
                  {"."}
                </TextContent>
                <br />
                {props.fetchFileError.details && (
                  <>
                    <Button variant={ButtonVariant.link} onClick={() => setShowDetails((prev) => !prev)}>
                      {showDetails ? "Hide details" : "Show details"}
                    </Button>

                    {showDetails && (
                      <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
                        <ClipboardCopy
                          isReadOnly
                          hoverTip="Copy"
                          clickTip="Copied"
                        >{`${props.fetchFileError.details}`}</ClipboardCopy>
                      </PageSection>
                    )}
                  </>
                )}
                <br />
              </PageSection>
            </EmptyStateBody>
            <Button variant={ButtonVariant.tertiary} onClick={onClose}>
              Return home
            </Button>
          </EmptyState>
        )}
        {/*</Bullseye>*/}
      </PageSection>
    </Page>
  );
}
