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

import { EditorType, File as UploadFile, newFile } from "@kogito-tooling/embedded-editor";
import {
  Brand,
  Bullseye,
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Dropdown,
  DropdownItem,
  DropdownToggle,
  Form,
  FormGroup,
  Gallery,
  Page,
  PageHeader,
  PageSection,
  Text,
  TextContent,
  TextInput,
  TextVariants,
  Title,
  Toolbar,
  ToolbarGroup,
  ToolbarItem
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, OutlinedQuestionCircleIcon } from "@patternfly/react-icons";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
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
  VALID
}

enum UploadFileInputState {
  INITIAL,
  INVALID_EXTENSION
}

enum UploadFileDndState {
  INITIAL,
  INVALID_EXTENSION,
  HOVER
}

interface InputFileUrlStateType {
  urlValidation: InputFileUrlState;
  urlToOpen: string | undefined;
}

export function HomePage(props: Props) {
  const context = useContext(GlobalContext);
  const history = useHistory();
  const { i18n } = useOnlineI18n();

  const uploadInputRef = useRef<HTMLInputElement>(null);

  const [inputFileUrl, setInputFileUrl] = useState("");
  const [inputFileUrlState, setInputFileUrlState] = useState<InputFileUrlStateType>({
    urlValidation: InputFileUrlState.INITIAL,
    urlToOpen: undefined
  });
  const [uploadFileDndState, setUploadFileDndState] = useState(UploadFileDndState.INITIAL);
  const [uploadFileInputState, setUploadFileInputState] = useState(UploadFileInputState.INITIAL);

  const uploadDndOnDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    setUploadFileDndState(UploadFileDndState.HOVER);
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const uploadDndOnDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    setUploadFileDndState(UploadFileDndState.INITIAL);
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const openFile = useCallback(
    (file: File) => {
      props.onFileOpened({
        isReadOnly: false,
        editorType: extractFileExtension(file.name) as EditorType,
        fileName: removeFileExtension(file.name),
        getFileContents: () =>
          new Promise<string | undefined>(resolve => {
            const reader = new FileReader();
            reader.onload = (event: any) => resolve(event.target.result as string);
            reader.readAsText(file);
          })
      });
      history.replace(context.routes.editor.url({ type: extractFileExtension(file.name)! }));
    },
    [context, history]
  );

  const onFileUploadFromDnd = useCallback((file: File) => {
    const fileExtension = extractFileExtension(file.name);
    if (!fileExtension || !context.router.getLanguageData(fileExtension)) {
      setUploadFileDndState(UploadFileDndState.INVALID_EXTENSION);
    } else {
      openFile(file);
    }
  }, []);

  const uploadDndOnDrop = useCallback(
    (e: React.DragEvent<HTMLDivElement>) => {
      setUploadFileDndState(UploadFileDndState.INITIAL);
      e.stopPropagation();
      e.preventDefault();

      const file = e.dataTransfer.files[0];
      // FIXME: On chrome it was observed that sometimes `file` is undefined, although its type says that it's not possible.
      if (!file) {
        return false;
      }

      onFileUploadFromDnd(file);
      return false;
    },
    [onFileUploadFromDnd]
  );

  const messageForUploadFileFromDndState = useMemo(() => {
    switch (uploadFileDndState) {
      case UploadFileDndState.INVALID_EXTENSION:
        return i18n.homePage.uploadFile.dndZone.invalidFile;
      default:
        return i18n.homePage.uploadFile.dndZone.waitingFile;
    }
  }, [uploadFileDndState]);

  const uploadDndClassName = useMemo(() => {
    switch (uploadFileDndState) {
      case UploadFileDndState.INVALID_EXTENSION:
        return "invalid";
      case UploadFileDndState.HOVER:
        return "hover";
      default:
        return "";
    }
  }, [uploadFileDndState]);

  const onFileUploadFromInput = useCallback((file: File) => {
    const fileExtension = extractFileExtension(file.name);
    if (!fileExtension || !context.router.getLanguageData(fileExtension)) {
      setUploadFileInputState(UploadFileInputState.INVALID_EXTENSION);
    } else {
      openFile(file);
    }
  }, []);

  const uploadFileFromInput = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      e.preventDefault();
      e.stopPropagation();

      if (uploadInputRef.current!.files) {
        const file = uploadInputRef.current!.files![0];
        onFileUploadFromInput(file);
      }
      e.target.value = "";
    },
    [onFileUploadFromInput]
  );

  const onDndInvalidFileExtensionAnimationEnd = useCallback((e: React.AnimationEvent<HTMLInputElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setUploadFileDndState(UploadFileDndState.INITIAL);
  }, []);

  const onInputInvalidFileExtensionAnimationEnd = useCallback((e: React.AnimationEvent<HTMLInputElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setUploadFileInputState(UploadFileInputState.INITIAL);
  }, []);

  const messageForUploadFileFromInputState = useMemo(() => {
    switch (uploadFileInputState) {
      case UploadFileInputState.INVALID_EXTENSION:
        return i18n.homePage.uploadFile.fileInput;
      default:
        return "";
    }
  }, [uploadFileInputState]);

  const uploadInputClassName = useMemo(() => {
    switch (uploadFileInputState) {
      case UploadFileInputState.INVALID_EXTENSION:
        return "invalid";
      default:
        return "";
    }
  }, [uploadFileInputState]);

  const createEmptyFile = useCallback(
    (editorType: EditorType) => {
      props.onFileOpened(newFile(editorType));
      history.replace(context.routes.editor.url({ type: editorType }));
    },
    [context, history]
  );

  const createEmptyBpmnFile = useCallback(() => {
    createEmptyFile(EditorType.BPMN);
  }, [createEmptyFile]);

  const createEmptyDmnFile = useCallback(() => {
    createEmptyFile(EditorType.DMN);
  }, [createEmptyFile]);

  const trySample = useCallback(
    (editorType: EditorType) => {
      const fileName = "sample";
      const filePath = `samples/${fileName}.${editorType}`;
      props.onFileOpened({
        isReadOnly: false,
        editorType: editorType,
        fileName: fileName,
        getFileContents: () => fetch(filePath).then(response => response.text())
      });
      history.replace(context.routes.editor.url({ type: editorType }));
    },
    [context, history]
  );

  const tryBpmnSample = useCallback(() => {
    trySample(EditorType.BPMN);
  }, [trySample]);

  const tryDmnSample = useCallback(() => {
    trySample(EditorType.DMN);
  }, [trySample]);

  const validateUrl = useCallback(async () => {
    if (inputFileUrl.trim() === "") {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INITIAL,
        urlToOpen: undefined
      });
      return;
    }

    let url: URL;
    try {
      url = new URL(inputFileUrl);
    } catch (e) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_URL,
        urlToOpen: undefined
      });
      return;
    }

    if (context.githubService.isGist(inputFileUrl)) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.VALIDATING,
        urlToOpen: undefined
      });

      const gistId = context.githubService.extractGistId(inputFileUrl);

      let rawUrl: string;
      try {
        rawUrl = await context.githubService.getGistRawUrlFromId(gistId);
      } catch (e) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.INVALID_GIST,
          urlToOpen: undefined
        });
        return;
      }

      const gistExtension = extractFileExtension(new URL(rawUrl).pathname);
      if (gistExtension && context.router.getLanguageData(gistExtension)) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: rawUrl
        });
        return;
      }

      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_GIST_EXTENSION,
        urlToOpen: undefined
      });
      return;
    }

    const fileExtension = extractFileExtension(url.pathname);
    if (!fileExtension || !context.router.getLanguageData(fileExtension)) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.INVALID_EXTENSION,
        urlToOpen: undefined
      });
      return;
    }

    setInputFileUrlState({
      urlValidation: InputFileUrlState.VALIDATING,
      urlToOpen: undefined
    });
    if (context.githubService.isGithub(inputFileUrl)) {
      if (await context.githubService.checkFileExistence(inputFileUrl)) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: inputFileUrl
        });
        return;
      }

      setInputFileUrlState({
        urlValidation: InputFileUrlState.NOT_FOUND_URL,
        urlToOpen: undefined
      });
      return;
    }

    try {
      if ((await fetch(inputFileUrl)).ok) {
        setInputFileUrlState({
          urlValidation: InputFileUrlState.VALID,
          urlToOpen: inputFileUrl
        });
        return;
      }

      setInputFileUrlState({
        urlValidation: InputFileUrlState.NOT_FOUND_URL,
        urlToOpen: undefined
      });
    } catch (e) {
      setInputFileUrlState({
        urlValidation: InputFileUrlState.CORS_NOT_AVAILABLE,
        urlToOpen: undefined
      });
    }
  }, [inputFileUrl]);

  useEffect(() => {
    validateUrl();
  }, [inputFileUrl]);

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
        urlToOpen: undefined
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
  }, [inputFileUrlState]);

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
  }, [inputFileUrlState]);

  const externalFileFormSubmit = useCallback(
    (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      e.stopPropagation();
      openFileFromUrl();
    },
    [inputFileUrl]
  );

  const logoProps = {
    href: window.location.href.split("?")[0].split("#")[0]
  };

  const linkDropdownItems = [
    <DropdownItem key="github-chrome-extension-dropdown-link">
      <Link to={context.routes.downloadHub.url({})} className="kogito--editor-hub-download_link">
        {i18n.homePage.dropdown.getHub}
      </Link>
    </DropdownItem>
  ];

  const userDropdownItems = [
    /*<DropdownItem key="">
      <Link to ={'/'}>Documentation</Link>
    </DropdownItem>,*/
    <DropdownItem key="">
      <a href={"https://groups.google.com/forum/#!forum/kogito-development"} target={"_blank"}>
        {i18n.homePage.dropdown.onlineForum} <ExternalLinkAltIcon className="pf-u-mx-sm" />
      </a>
    </DropdownItem>
  ];

  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);
  const [isLinkDropdownOpen, setIsLinkDropdownOpen] = useState(false);

  const headerToolbar = (
    <>
      <Toolbar>
        <ToolbarGroup>
          <ToolbarItem className="pf-u-display-none pf-u-display-flex-on-lg">
            <Link to={context.routes.downloadHub.url({})} className="kogito--editor-hub-download_link">
              {i18n.homePage.dropdown.getHub}
              {/*<Button variant="plain">Get Business Modeler Hub Preview</Button>*/}
            </Link>
          </ToolbarItem>
          <ToolbarItem className="pf-u-display-none-on-lg">
            <Dropdown
              isPlain={true}
              position="right"
              isOpen={isLinkDropdownOpen}
              toggle={
                <DropdownToggle
                  iconComponent={null}
                  onToggle={setIsLinkDropdownOpen}
                  aria-label="External links to extensions"
                >
                  <ExternalLinkAltIcon />
                </DropdownToggle>
              }
              dropdownItems={linkDropdownItems}
            />
          </ToolbarItem>
          <ToolbarItem>
            <Dropdown
              isPlain={true}
              position="right"
              isOpen={isUserDropdownOpen}
              toggle={
                <DropdownToggle iconComponent={null} onToggle={setIsUserDropdownOpen} aria-label="Links">
                  <OutlinedQuestionCircleIcon />
                </DropdownToggle>
              }
              dropdownItems={userDropdownItems}
            />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </>
  );

  const Header = (
    <PageHeader
      logo={<Brand src={"images/BusinessModeler_Logo_38x389.svg"} alt="Logo" />}
      logoProps={logoProps}
      toolbar={headerToolbar}
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
            {i18n.terms.poweredBy}{" "}
            <Brand
              src={"images/kogito_logo_white.png"}
              alt="Kogito Logo"
              style={{ height: "1em", verticalAlign: "text-bottom" }}
            />
          </Text>
        </TextContent>
      </PageSection>
      <PageSection className="pf-u-px-2xl-on-lg">
        <Gallery gutter="lg" className="kogito--editor-landing__gallery">
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.bpmnCard.title}
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>{i18n.homePage.bpmnCard.explanation}</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryBpmnSample}>
                {i18n.homePage.trySample}
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyBpmnFile}>
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
              <Button variant="link" isInline={true} onClick={tryDmnSample}>
                {i18n.homePage.trySample}
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyDmnFile}>
                {i18n.homePage.dmnCard.createNew}
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                {i18n.homePage.editExistingFile}
              </Title>
            </CardHeader>
            <CardBody isFilled={true} className="kogito--editor-landing__upload-box">
              {/* Upload Drag Target */}
              <div
                onDragOver={uploadDndOnDragOver}
                onDragLeave={uploadDndOnDragLeave}
                onDrop={uploadDndOnDrop}
                className={uploadDndClassName}
                onAnimationEnd={onDndInvalidFileExtensionAnimationEnd}
              >
                <Bullseye>{messageForUploadFileFromDndState}</Bullseye>
              </div>
            </CardBody>
            <CardBody>{i18n.terms.or}</CardBody>
            <CardFooter className="kogito--editor-landing__upload-input">
              <Button variant="secondary" className="kogito--editor-landing__upload-btn">
                {i18n.homePage.chooseLocalFile}
                {/* Transparent file input overlays the button */}
                <input
                  accept={".dmn, .bpmn, .bpmn2"}
                  className="pf-c-button"
                  type="file"
                  aria-label="File selection"
                  ref={uploadInputRef}
                  onChange={uploadFileFromInput}
                />
              </Button>
              <div className={uploadInputClassName} onAnimationEnd={onInputInvalidFileExtensionAnimationEnd}>
                {messageForUploadFileFromInputState}
              </div>
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
                  isValid={isUrlInputTextValid}
                  helperText={helperMessageForInputFileFromUrlState}
                  helperTextInvalid={helperInvalidMessageForInputFileFromUrlState}
                >
                  <TextInput
                    isRequired={true}
                    onBlur={onInputFileFromUrlBlur}
                    isValid={isUrlInputTextValid}
                    autoComplete={"off"}
                    value={inputFileUrl}
                    onChange={inputFileFromUrlChanged}
                    type="url"
                    data-testid="url-text-input"
                    id="url-text-input"
                    name="urlText"
                    aria-describedby="url-text-input-helper"
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
              >
                {i18n.homePage.openUrl.openFromSource}
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
      </PageSection>
    </Page>
  );
}
