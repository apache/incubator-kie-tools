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

import { File as UploadFile, newFile } from "@kogito-tooling/editor/dist/embedded";
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
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, OutlinedQuestionCircleIcon } from "@patternfly/react-icons";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { AnimatedTripleDotLabel } from "../common/AnimatedTripleDotLabel";
import { GlobalContext } from "../common/GlobalContext";
import { extractFileExtension, removeFileExtension } from "../common/utils";

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
      const fileExtension = extractFileExtension(file.name)!;
      props.onFileOpened({
        isReadOnly: false,
        fileExtension: fileExtension,
        fileName: removeFileExtension(file.name),
        getFileContents: () =>
          new Promise<string | undefined>(resolve => {
            const reader = new FileReader();
            reader.onload = (event: any) => resolve(event.target.result as string);
            reader.readAsText(file);
          })
      });
      history.replace(context.routes.editor.url({ type: fileExtension }));
    },
    [context, history]
  );

  const onFileUploadFromDnd = useCallback((file: File) => {
    const fileExtension = extractFileExtension(file.name);
    if (!fileExtension || !context.editorEnvelopeLocator.mapping.has(fileExtension)) {
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
        return "File extension is not supported";
      default:
        return "Drop a BPMN or DMN file here";
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
    if (!fileExtension || !context.editorEnvelopeLocator.mapping.has(fileExtension)) {
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
        return "File extension is not supported";
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

  const trySample = useCallback(
    (fileExtension: string) => {
      const fileName = "sample";
      const filePath = `samples/${fileName}.${fileExtension}`;
      props.onFileOpened({
        isReadOnly: false,
        fileExtension: fileExtension,
        fileName: fileName,
        getFileContents: () => fetch(filePath).then(response => response.text())
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
      if (gistExtension && context.editorEnvelopeLocator.mapping.has(gistExtension)) {
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
    if (!fileExtension || !context.editorEnvelopeLocator.mapping.has(fileExtension)) {
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
        return <AnimatedTripleDotLabel label={"Validating URL"} />;
      default:
        return "";
    }
  }, [inputFileUrlState]);

  const helperInvalidMessageForInputFileFromUrlState = useMemo(() => {
    switch (inputFileUrlState.urlValidation) {
      case InputFileUrlState.INVALID_GIST_EXTENSION:
        return "File type on the provided gist is not supported.";
      case InputFileUrlState.INVALID_EXTENSION:
        return "File type on the provided URL is not supported.";
      case InputFileUrlState.INVALID_GIST:
        return "Enter a valid Gist URL.";
      case InputFileUrlState.INVALID_URL:
        return 'This URL is not valid (don\'t forget "https://"!).';
      case InputFileUrlState.NOT_FOUND_URL:
        return "This URL does not exist.";
      case InputFileUrlState.CORS_NOT_AVAILABLE:
        return "This URL cannot be opened because it doesn't allow other websites to access it.";
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
      <Link to={context.routes.downloadHub.url({})}>Get Business Modeler Hub Preview</Link>
    </DropdownItem>
  ];

  const userDropdownItems = [
    <DropdownItem key="">
      <a href={"https://groups.google.com/forum/#!forum/kogito-development"} target={"_blank"}>
        Online forum <ExternalLinkAltIcon className="pf-u-mx-sm" />
      </a>
    </DropdownItem>
  ];

  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);
  const [isLinkDropdownOpen, setIsLinkDropdownOpen] = useState(false);

  const headerToolbar = (
    <PageHeaderTools>
      <PageHeaderToolsGroup>
        <PageHeaderToolsItem className="pf-u-display-none pf-u-display-flex-on-lg">
          <Link to={context.routes.downloadHub.url({})} className="kogito--editor-hub-download_link">
            Get Business Modeler Hub Preview
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
            Asset Editor for Kogito and Process Automation
          </Title>
          <Text>
            Welcome to Business Modeler! These simple BPMN and DMN editors are here to allow you to collaborate quickly
            and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch
            in the forum or review the documentation for more information.
          </Text>
          <Text component={TextVariants.small} className="pf-u-text-align-right">
            Powered by{" "}
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
                Workflow (.BPMN)
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>BPMN files are used to generate business processes.</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryBpmnSample}>
                Try Sample
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyBpmnFile}>
                Create new workflow
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                Decision model (.DMN)
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>DMN files are used to generate decision models</CardBody>
            <CardBody isFilled={true}>
              <Button variant="link" isInline={true} onClick={tryDmnSample}>
                Try Sample
              </Button>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={createEmptyDmnFile}>
                Create new decision model
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader>
              <Title headingLevel="h2" size="2xl">
                Edit existing file
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
            <CardBody>or</CardBody>
            <CardFooter className="kogito--editor-landing__upload-input">
              <Button variant="secondary" className="kogito--editor-landing__upload-btn">
                Choose a local file
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
                Open from source
              </Title>
            </CardHeader>
            <CardBody isFilled={false}>Paste a URL to a source code link (GitHub, Dropbox, etc.)</CardBody>
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
                Open from source
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
      </PageSection>
    </Page>
  );
}
