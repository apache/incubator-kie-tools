/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useContext, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { GlobalContext } from "../common/GlobalContext";
import { EMPTY_FILE, File as UploadFile } from "../common/File";
import {
  Bullseye,
  Button,
  Page,
  PageSection,
  Title,
  Toolbar,
  ToolbarItem,
  PageHeader,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Gallery,
  ToolbarGroup,
  Dropdown,
  DropdownToggle,
  DropdownItem,
  Text,
  TextVariants,
  TextContent,
  Brand,
  TextInput,
  FormGroup,
  Form
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, OutlinedQuestionCircleIcon } from "@patternfly/react-icons";
import { extractFileExtension, removeFileExtension } from "../common/utils";
import { GithubService } from "../common/GithubService";

interface Props {
  onFileOpened: (file: UploadFile) => void;
}

enum InputFileUrlState {
  VALID,
  INITIAL,
  INVALID_URL,
  NO_FILE_URL,
  INVALID_EXTENSION,
  NOT_FOUND_URL,
  CORS_NOT_AVAILABLE
}

enum UploadFileInputState {
  INITIAL,
  INVALID_EXTENSION
}

enum UploadFileDndState {
  INITIAL,
  INVALID_EXTENSION
}

const githubService = new GithubService();

export function HomePage(props: Props) {
  const context = useContext(GlobalContext);
  const history = useHistory();

  const uploadInputRef = useRef<HTMLInputElement>(null);
  const uploadDndRef = useRef<HTMLDivElement>(null);

  const [inputFileUrl, setInputFileUrl] = useState("");
  const [inputFileUrlState, setInputFileUrlState] = useState(InputFileUrlState.INITIAL);
  const [uploadFileDndState, setUploadFileDndState] = useState(UploadFileDndState.INITIAL);
  const [uploadFileInputState, setUploadFileInputState] = useState(UploadFileInputState.INITIAL);

  const uploadDndOnDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadDndRef.current!.className = "hover";
    setUploadFileDndState(UploadFileDndState.INITIAL);
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const uploadDndOnDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadDndRef.current!.className = "";
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const openFile = useCallback(
    (file: File) => {
      props.onFileOpened({
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
      uploadDndRef.current!.className = "";
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

  const uploadFileFromInput = useCallback(() => {
    setUploadFileInputState(UploadFileInputState.INITIAL);
    if (uploadInputRef.current!.files) {
      const file = uploadInputRef.current!.files![0];
      onFileUploadFromInput(file);
    }
  }, [onFileUploadFromInput]);

  const onDndInvalidFileExtensionAnimationEnd = useCallback(() => {
    setUploadFileDndState(UploadFileDndState.INITIAL);
  }, [uploadFileDndState]);

  const onInputInvalidFileExtensionAnimationEnd = useCallback(() => {
    setUploadFileInputState(UploadFileInputState.INITIAL);
  }, [uploadFileInputState]);

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
      props.onFileOpened(EMPTY_FILE);
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

  const checkResponseFromFetch = useCallback(responseSucceed => {
    if (responseSucceed) {
      setInputFileUrlState(InputFileUrlState.VALID);
    } else {
      setInputFileUrlState(InputFileUrlState.NOT_FOUND_URL);
    }
  }, []);

  const validateFileOnUrl = useCallback((fileUrl: string) => {
    if (githubService.isGithub(fileUrl)) {
      githubService
        .checkGithubFileExistence(fileUrl)
        .then(checkResponseFromFetch)
        .catch(err => setInputFileUrlState(InputFileUrlState.NOT_FOUND_URL));
    } else {
      fetch(fileUrl)
        .then(({ ok }) => checkResponseFromFetch(ok))
        .catch(err => {
          setInputFileUrlState(InputFileUrlState.CORS_NOT_AVAILABLE);
        });
    }
  }, []);

  const validateUrl = useCallback((fileUrl: string) => {
    let url: URL;
    try {
      url = new URL(fileUrl);
    } catch (e) {
      setInputFileUrlState(InputFileUrlState.INVALID_URL);
      return;
    }
    const fileExtension = extractFileExtension(url.pathname);
    if (!fileExtension) {
      setInputFileUrlState(InputFileUrlState.NO_FILE_URL);
    } else if (!context.router.getLanguageData(fileExtension)) {
      setInputFileUrlState(InputFileUrlState.INVALID_EXTENSION);
    } else {
      validateFileOnUrl(fileUrl);
    }
  }, []);

  const inputFileFromUrlChanged = useCallback((fileUrl: string) => {
    setInputFileUrl(fileUrl);
    validateUrl(fileUrl);
  }, []);

  const validateUrlInputText = useMemo(
    () => inputFileUrlState === InputFileUrlState.VALID || inputFileUrlState === InputFileUrlState.INITIAL,
    [inputFileUrlState]
  );

  const validateUrlInputButton = useMemo(() => inputFileUrlState === InputFileUrlState.VALID, [inputFileUrlState]);

  const onInputFileFromUrlBlur = useCallback(() => {
    if (inputFileUrl.trim() === "") {
      setInputFileUrlState(InputFileUrlState.INITIAL);
    }
  }, [inputFileUrl]);

  const openFileFromUrl = useCallback(() => {
    if (validateUrlInputText) {
      const fileUrl = new URL(inputFileUrl);
      const fileExtension = extractFileExtension(fileUrl.pathname);
      // FIXME: KOGITO-1202
      window.location.href = `?file=${inputFileUrl}#/editor/${fileExtension}`;
    }
  }, [inputFileUrl, validateUrlInputText]);

  const messageForInputFileFromUrlState = useMemo(() => {
    switch (inputFileUrlState) {
      case InputFileUrlState.INVALID_EXTENSION:
        return "The file type of this URL is not supported.";
      case InputFileUrlState.INVALID_URL:
        return 'This URL is not valid (don\'t forget the "https://"!).';
      case InputFileUrlState.NO_FILE_URL:
        return "This URL is not from a file.";
      case InputFileUrlState.NOT_FOUND_URL:
        return "This URL does not exist.";
      case InputFileUrlState.CORS_NOT_AVAILABLE:
        return "This URL cannot be opened because it not allow other websites to download their files.";
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
      <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
        Get GitHub Chrome extension <ExternalLinkAltIcon className="pf-u-mx-sm" />
      </a>
    </DropdownItem>,
    <DropdownItem key="vscode-extension-dropdown-link">
      <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
        Get VSCode extension <ExternalLinkAltIcon className="pf-u-mx-sm" />
      </a>
    </DropdownItem>
  ];

  const userDropdownItems = [
    /*<DropdownItem key="">
      <Link to ={'/'}>Documentation</Link>
    </DropdownItem>,*/
    <DropdownItem key="">
      <a href={"https://groups.google.com/forum/#!forum/kogito-development"} target={"_blank"}>
        Online forum <ExternalLinkAltIcon className="pf-u-mx-sm" />
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
            <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
              <Button variant="plain">
                Get GitHub Chrome extension
                <ExternalLinkAltIcon className="pf-u-mx-sm" />
              </Button>
            </a>
            <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
              <Button variant="plain">
                Get VSCode extension
                <ExternalLinkAltIcon className="pf-u-mx-sm" />
              </Button>
            </a>
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
        <Gallery gutter="lg" className="kogito--editor-landing__gallery">
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
                ref={uploadDndRef}
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
              <Form onSubmit={externalFileFormSubmit} disabled={!validateUrlInputText} spellCheck={false}>
                <FormGroup
                  label="URL"
                  fieldId="url-text-input"
                  isValid={validateUrlInputText}
                  helperText=""
                  helperTextInvalid={messageForInputFileFromUrlState}
                >
                  <TextInput
                    isRequired={true}
                    onBlur={onInputFileFromUrlBlur}
                    isValid={validateUrlInputText}
                    value={inputFileUrl}
                    onChange={inputFileFromUrlChanged}
                    type="url"
                    id="url-text-input"
                    name="urlText"
                    aria-describedby="url-text-input-helper"
                  />
                </FormGroup>
              </Form>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={openFileFromUrl} isDisabled={!validateUrlInputButton}>
                Open from source
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
      </PageSection>
    </Page>
  );
}
