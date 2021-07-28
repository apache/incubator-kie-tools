/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { File as UploadFile, newFile } from "@kie-tooling-core/editor/dist/channel";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardHeader, CardFooter } from "@patternfly/react-core/dist/js/components/Card";
import { Dropdown, DropdownItem, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { FileUpload } from "@patternfly/react-core/dist/js/components/FileUpload";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import {
  Page,
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageSection,
  PageHeaderToolsItem,
} from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { AnimatedTripleDotLabel } from "../common/AnimatedTripleDotLabel";
import { GlobalContext } from "../common/GlobalContext";
import { extractFileExtension, removeFileExtension } from "../common/utils";
import { useOnlineI18n } from "../common/i18n";

interface Props {
  onFileOpened: (file: UploadFile) => void;
}

enum InputFileUrlState {
  INITIAL,
  INVALID_URL,
  INVALID_EXTENSION,
  NOT_FOUND_URL,
  CORS_NOT_AVAILABLE,
  INVALID_GIST,
  INVALID_GIST_EXTENSION,
  VALIDATING,
  VALID,
}

interface InputFileUrlStateType {
  urlValidation: InputFileUrlState;
  urlToOpen: string | undefined;
}

export function HomePage(props: Props) {
  const context = useContext(GlobalContext);
  const history = useHistory();
  const { i18n } = useOnlineI18n();

  const [uploadedFileName, setUploadedFileName] = useState("");
  const [isUploadRejected, setIsUploadRejected] = useState(false);

  const [inputFileUrl, setInputFileUrl] = useState("");
  const [inputFileUrlState, setInputFileUrlState] = useState<InputFileUrlStateType>({
    urlValidation: InputFileUrlState.INITIAL,
    urlToOpen: undefined,
  });

  const onFileUpload = useCallback(
    (
      file: File,
      fileName: string,
      e:
        | React.DragEvent<HTMLElement>
        | React.ChangeEvent<HTMLTextAreaElement>
        | React.MouseEvent<HTMLButtonElement, MouseEvent>
    ) => {
      e.stopPropagation();
      e.preventDefault();

      setUploadedFileName(fileName);
      setIsUploadRejected(false);

      const fileExtension = extractFileExtension(fileName);
      if (!fileExtension || !context.editorEnvelopeLocator.mapping.has(fileExtension)) {
        return;
      }

      props.onFileOpened({
        isReadOnly: false,
        fileExtension,
        fileName: removeFileExtension(fileName),
        getFileContents: () =>
          new Promise<string | undefined>((resolve) => {
            const reader = new FileReader();
            reader.onload = (event: any) => resolve(event.target.result as string);
            reader.readAsText(file);
          }),
      });
      history.replace(context.routes.editor.url({ type: fileExtension }));
    },
    [context, history]
  );

  const onDropRejected = useCallback(() => setIsUploadRejected(true), []);

  const createEmptyFile = useCallback(
    (fileExtension: string) => {
      props.onFileOpened(newFile(fileExtension));
      history.replace(context.routes.editor.url({ type: fileExtension }));
    },
    [context, history]
  );

  const createEmptyBpmnFile = useCallback(() => {
    createEmptyFile("bpmn");
  }, [createEmptyFile]);

  const createEmptyDmnFile = useCallback(() => {
    createEmptyFile("dmn");
  }, [createEmptyFile]);

  const createEmptyPmmlFile = useCallback(() => {
    createEmptyFile("pmml");
  }, [createEmptyFile]);

  const trySample = useCallback(
    (fileExtension: string) => {
      const fileName = "sample";
      const filePath = `samples/${fileName}.${fileExtension}`;
      props.onFileOpened({
        isReadOnly: false,
        fileExtension: fileExtension,
        fileName: fileName,
        getFileContents: () => fetch(filePath).then((response) => response.text()),
      });
      history.replace(context.routes.editor.url({ type: fileExtension }));
    },
    [context, history]
  );

  const tryBpmnSample = useCallback(() => {
    trySample("bpmn");
  }, [trySample]);

  const tryDmnSample = useCallback(() => {
    trySample("dmn");
  }, [trySample]);

  const tryPmmlSample = useCallback(() => {
    trySample("pmml");
  }, [trySample]);

  const validateUrl = useCallback(async () => {
    if (inputFileUrl.trim() === "") {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INITIAL,
        urlToOpen: undefined,
      });
      return;
    }

    let url: URL;
    try {
      url = new URL(inputFileUrl);
    } catch (e) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_URL,
        urlToOpen: undefined,
      });
      return;
    }

    if (context.githubService.isGist(inputFileUrl)) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.VALIDATING,
        urlToOpen: undefined,
      });

      const gistId = context.githubService.isGistDefault(inputFileUrl)
        ? context.githubService.extractGistId(inputFileUrl)
        : context.githubService.extractGistIdFromRawUrl(inputFileUrl);

      const gistFileName = context.githubService.isGistDefault(inputFileUrl)
        ? context.githubService.extractGistFilename(inputFileUrl)
        : context.githubService.extractGistFilenameFromRawUrl(inputFileUrl);

      let rawUrl: string;
      try {
        rawUrl = await context.githubService.getGistRawUrlFromId(gistId, gistFileName);
      } catch (e) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.INVALID_GIST,
          urlToOpen: undefined,
        });
        return;
      }

      const gistExtension = extractFileExtension(new URL(rawUrl).pathname);
      if (gistExtension && context.editorEnvelopeLocator.mapping.has(gistExtension)) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: rawUrl,
        });
        return;
      }

      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_GIST_EXTENSION,
        urlToOpen: undefined,
      });
      return;
    }

    const fileExtension = extractFileExtension(url.pathname);
    if (!fileExtension || !context.editorEnvelopeLocator.mapping.has(fileExtension)) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_EXTENSION,
        urlToOpen: undefined,
      });
      return;
    }

    setInputFileUrlState({
      urlValidation: InputFileUrlState.VALIDATING,
      urlToOpen: undefined,
    });
    if (context.githubService.isGithub(inputFileUrl)) {
      try {
        const rawUrl = await context.githubService.getGithubRawUrl(inputFileUrl);
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: rawUrl,
        });
        return;
      } catch (err) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.NOT_FOUND_URL,
          urlToOpen: undefined,
        });
        return;
      }
    }

    try {
      if ((await fetch(inputFileUrl)).ok) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: inputFileUrl,
        });
        return;
      }

      setInputFileUrlState({
        urlValidation: InputFileUrlState.NOT_FOUND_URL,
        urlToOpen: undefined,
      });
    } catch (e) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.CORS_NOT_AVAILABLE,
        urlToOpen: undefined,
      });
    }
  }, [context.editorEnvelopeLocator.mapping, context.githubService, inputFileUrl]);

  useEffect(() => {
    validateUrl();
  }, [validateUrl]);

  const inputFileFromUrlChanged = useCallback((fileUrl: string) => {
    setInputFileUrl(fileUrl);
  }, []);

  const isUrlInputTextValid = useMemo(
    () =>
      inputFileUrlState.urlValidation === InputFileUrlState.VALID ||
      inputFileUrlState.urlValidation === InputFileUrlState.INITIAL ||
      inputFileUrlState.urlValidation === InputFileUrlState.VALIDATING,
    [inputFileUrlState]
  );

  const urlCanBeOpen = useMemo(() => inputFileUrlState.urlValidation === InputFileUrlState.VALID, [inputFileUrlState]);

  const onInputFileFromUrlBlur = useCallback(() => {
    if (inputFileUrl.trim() === "") {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INITIAL,
        urlToOpen: undefined,
      });
    }
  }, [inputFileUrl]);

  const openFileFromUrl = useCallback(() => {
    if (urlCanBeOpen && inputFileUrlState.urlToOpen) {
      const fileExtension = extractFileExtension(new URL(inputFileUrlState.urlToOpen).pathname);
      // FIXME: KOGITO-1202
      window.location.href = `?file=${inputFileUrlState.urlToOpen}#/editor/${fileExtension}`;
    }
  }, [inputFileUrl, inputFileUrlState, urlCanBeOpen, inputFileUrlState]);

  const helperMessageForInputFileFromUrlState = useMemo(() => {
    switch (inputFileUrlState.urlValidation) {
      case InputFileUrlState.VALIDATING:
        return <AnimatedTripleDotLabel label={i18n.homePage.openUrl.validating} />;
      default:
        return "";
    }
  }, [inputFileUrlState, i18n]);

  const helperInvalidMessageForInputFileFromUrlState = useMemo(() => {
    switch (inputFileUrlState.urlValidation) {
      case InputFileUrlState.INVALID_GIST_EXTENSION:
        return i18n.homePage.openUrl.invalidGistExtension;
      case InputFileUrlState.INVALID_EXTENSION:
        return i18n.homePage.openUrl.invalidExtension;
      case InputFileUrlState.INVALID_GIST:
        return i18n.homePage.openUrl.invalidGist;
      case InputFileUrlState.INVALID_URL:
        return i18n.homePage.openUrl.invalidUrl;
      case InputFileUrlState.NOT_FOUND_URL:
        return i18n.homePage.openUrl.notFoundUrl;
      case InputFileUrlState.CORS_NOT_AVAILABLE:
        return i18n.homePage.openUrl.corsNotAvailable;
      default:
        return "";
    }
  }, [inputFileUrlState, i18n]);

  const externalFileFormSubmit = useCallback(
    (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      e.stopPropagation();
      openFileFromUrl();
    },
    [inputFileUrl]
  );

  const logoProps = {
    href: window.location.href.split("?")[0].split("#")[0],
  };

  const linkDropdownItems = [
    <DropdownItem key="github-chrome-extension-dropdown-link">
      <Link to={context.routes.downloadHub.url({})}>{i18n.homePage.dropdown.getHub}</Link>
    </DropdownItem>,
  ];

  const userDropdownItems = [
    <DropdownItem key="">
      <a href={"https://groups.google.com/forum/#!forum/kogito-development"} target={"_blank"}>
        {i18n.homePage.dropdown.onlineForum}
        <ExternalLinkAltIcon className="pf-u-mx-sm" />
      </a>
    </DropdownItem>,
  ];

  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);
  const [isLinkDropdownOpen, setIsLinkDropdownOpen] = useState(false);

  const headerToolbar = (
    <PageHeaderTools>
      <PageHeaderToolsGroup>
        <PageHeaderToolsItem className="pf-u-display-none pf-u-display-flex-on-lg">
          <Link to={context.routes.downloadHub.url({})} className="kogito--editor-hub-download_link">
            {i18n.homePage.dropdown.getHub}
          </Link>
        </PageHeaderToolsItem>
        <PageHeaderToolsItem className="pf-u-display-none-on-lg">
          <Dropdown
            isPlain={true}
            position="right"
            isOpen={isLinkDropdownOpen}
            toggle={
              <DropdownToggle
                toggleIndicator={null}
                onToggle={setIsLinkDropdownOpen}
                aria-label="External links to hub"
              >
                <ExternalLinkAltIcon />
              </DropdownToggle>
            }
            dropdownItems={linkDropdownItems}
          />
        </PageHeaderToolsItem>
        <PageHeaderToolsItem>
          <Dropdown
            isPlain={true}
            position="right"
            isOpen={isUserDropdownOpen}
            toggle={
              <DropdownToggle toggleIndicator={null} onToggle={setIsUserDropdownOpen} aria-label="Links">
                <OutlinedQuestionCircleIcon />
              </DropdownToggle>
            }
            dropdownItems={userDropdownItems}
          />
        </PageHeaderToolsItem>
      </PageHeaderToolsGroup>
    </PageHeaderTools>
  );

  const Header = (
    <PageHeader
      logo={<Brand src={"images/BusinessModeler_Logo_38x389.svg"} alt="Logo" />}
      logoProps={logoProps}
      headerTools={headerToolbar}
    />
  );

  return (
    <Page header={Header} className="kogito--editor-landing">
      <PageSection variant="dark" className="kogito--editor-landing__title-section pf-u-p-2xl-on-lg">
        <TextContent>
          <Title size="3xl" headingLevel="h1">
            {i18n.homePage.header.title}
          </Title>
          <Text>{i18n.homePage.header.welcomeText}</Text>
          <Text component={TextVariants.small} className="pf-u-text-align-right">
            {`${i18n.terms.poweredBy} `}
            <Brand
              src={"images/kogito_logo_white.png"}
              alt="Kogito Logo"
              style={{ height: "1em", verticalAlign: "text-bottom" }}
            />
          </Text>
        </TextContent>
      </PageSection>
      <PageSection className="pf-u-px-2xl-on-lg">
        <Gallery hasGutter={true} className="kogito--editor-landing__gallery">
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.bpmnCard.title}
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>{i18n.homePage.bpmnCard.explanation}</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryBpmnSample} ouiaId="try-bpmn-sample-button">
                {i18n.homePage.trySample}
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyBpmnFile} ouiaId="new-bpmn-button">
                {i18n.homePage.bpmnCard.createNew}
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.dmnCard.title}
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>{i18n.homePage.dmnCard.explanation}</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryDmnSample} ouiaId="try-dmn-sample-button">
                {i18n.homePage.trySample}
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyDmnFile} ouiaId="new-dmn-button">
                {i18n.homePage.dmnCard.createNew}
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.pmmlCard.title}
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>{i18n.homePage.pmmlCard.explanation}</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryPmmlSample} ouiaId="try-pmml-sample-button">
                {i18n.homePage.trySample}
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyPmmlFile} ouiaId="new-pmml-button">
                {i18n.homePage.pmmlCard.createNew}
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.uploadFile.header}
              </Title>
            </CardHeader>
            <CardBody>{i18n.homePage.uploadFile.body}</CardBody>
            <CardFooter>
              <Form>
                <FormGroup
                  fieldId={"file-upload-field"}
                  helperText={i18n.homePage.uploadFile.helperText}
                  helperTextInvalid={i18n.homePage.uploadFile.helperInvalidText}
                  validated={isUploadRejected ? "error" : "default"}
                >
                  <FileUpload
                    id={"file-upload-field"}
                    filenamePlaceholder={i18n.homePage.uploadFile.placeholder}
                    filename={uploadedFileName}
                    onChange={onFileUpload}
                    dropzoneProps={{
                      accept: [...context.editorEnvelopeLocator.mapping.keys()].map((ext) => "." + ext).join(", "),
                      onDropRejected,
                    }}
                    validated={isUploadRejected ? "error" : "default"}
                  />
                </FormGroup>
              </Form>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.openUrl.openFromSource}
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>{i18n.homePage.openUrl.description}</CardBody>
            <CardBody isFilled={true}>
              <Form onSubmit={externalFileFormSubmit} disabled={!isUrlInputTextValid} spellCheck={false}>
                <FormGroup
                  label="URL"
                  fieldId="url-text-input"
                  data-testid="url-form-input"
                  validated={isUrlInputTextValid ? "default" : "error"}
                  helperText={helperMessageForInputFileFromUrlState}
                  helperTextInvalid={helperInvalidMessageForInputFileFromUrlState}
                >
                  <TextInput
                    isRequired={true}
                    onBlur={onInputFileFromUrlBlur}
                    validated={isUrlInputTextValid ? "default" : "error"}
                    autoComplete={"off"}
                    value={inputFileUrl}
                    onChange={inputFileFromUrlChanged}
                    type="url"
                    data-testid="url-text-input"
                    id="url-text-input"
                    name="urlText"
                    aria-describedby="url-text-input-helper"
                    data-ouia-component-id="url-input"
                  />
                </FormGroup>
              </Form>
            </CardBody>
            <CardFooter>
              <Button
                variant="secondary"
                onClick={openFileFromUrl}
                isDisabled={!urlCanBeOpen}
                data-testid="open-url-button"
                ouiaId="open-from-source-button"
              >
                {i18n.homePage.openUrl.openFromSource}
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
        <div className={"kogito-tooling--build-info"}>{process.env["WEBPACK_REPLACE__buildInfo"]}</div>
      </PageSection>
    </Page>
  );
}
