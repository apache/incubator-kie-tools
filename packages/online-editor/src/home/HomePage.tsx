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
  TextInput,
  FormGroup,
  ToolbarGroup,
  Dropdown,
  DropdownToggle,
  DropdownItem,
  Text,
  TextVariants,
  TextContent,
  Brand
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, OutlinedQuestionCircleIcon } from "@patternfly/react-icons";
import { Link } from "react-router-dom";

interface Props {
  onFileOpened: (file: UploadFile) => void;
}

export function HomePage(props: Props) {
  const context = useContext(GlobalContext);
  const history = useHistory();

  const uploadInputRef = useRef<HTMLInputElement>(null);
  const uploadBoxRef = useRef<HTMLDivElement>(null);

  const uploadBoxOnDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadBoxRef.current!.className = "hover";
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const uploadBoxOnDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadBoxRef.current!.className = "";
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const onFileUpload = useCallback(
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

  const uploadBoxOnDrop = useCallback(
    (e: React.DragEvent<HTMLDivElement>) => {
      uploadBoxRef.current!.className = "";
      e.stopPropagation();
      e.preventDefault();

      const file = e.dataTransfer.files[0];
      onFileUpload(file);

      return false;
    },
    [onFileUpload]
  );

  const editFile = useCallback(() => {
    if (uploadInputRef.current!.files) {
      const file = uploadInputRef.current!.files![0];
      onFileUpload(file);
    }
  }, [onFileUpload]);

  const createFile = useCallback((fileType: string) => {
    props.onFileOpened(EMPTY_FILE);
    history.replace(context.routes.editor.url({ type: fileType }));
  }, [context, history]);

  const trySample = useCallback((fileType: string) => {
      const fileName = "sample";
      const filePath = `samples/${fileName}.${fileType}`;
      props.onFileOpened({
        fileName: fileName,
        getFileContents: () => fetch(filePath).then(response => response.text())
      });
      history.replace(context.routes.editor.url({ type: fileType }));
  }, [context, history]);

  const logoProps = {
    // TODO: Is this the correct link?
    href: 'https://www.kiegroup.org/',
    target: '_blank'
  };

  const linkDropdownItems = [
    <DropdownItem key="">
      {/* TODO: Need url */}
      <Link to ={'/'}>Get GitHub Chrome extension <ExternalLinkAltIcon className="pf-u-mx-sm" /></Link>
    </DropdownItem>,
    <DropdownItem key="">
      {/* TODO: Need url */}
      <Link to ={'/'}>Get VSCode extension <ExternalLinkAltIcon className="pf-u-mx-sm" /></Link>
    </DropdownItem>
];

  const userDropdownItems = [
    <DropdownItem key="">
      {/* TODO: Need url */}
      <Link to ={'/'}>Documentation</Link>
    </DropdownItem>,
    <DropdownItem key="">
      {/* TODO: Need url */}
      <Link to ={'/'}>Online forum</Link>
    </DropdownItem>,
  ];
  
  const [isUserDropdownOpen, setisUserDropdownOpen] = useState(false);
  const [isLinkDropdownOpen, setIsLinkDropdownOpen] = useState(false);


  const headerToolbar = (
    <React.Fragment>
      <Toolbar>
        <ToolbarGroup>
          <ToolbarItem className="pf-u-display-none pf-u-display-flex-on-lg">
            <Button variant="plain">
              Get GitHub Chrome extension 
              <ExternalLinkAltIcon className="pf-u-mx-sm" />
            </Button>
            <Button variant="plain">
              Get VSCode extension
              <ExternalLinkAltIcon className="pf-u-mx-sm" />
            </Button>
          </ToolbarItem>
          <ToolbarItem  className="pf-u-display-none-on-lg">
            <Dropdown
              isPlain={true}
              position="right"
              isOpen={isLinkDropdownOpen}
              toggle={
                <DropdownToggle iconComponent={null} onToggle={setIsLinkDropdownOpen} aria-label="External links to extensions">
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
                <DropdownToggle iconComponent={null} onToggle={setisUserDropdownOpen} aria-label="Links">
                  <OutlinedQuestionCircleIcon />
                </DropdownToggle>
              }
              dropdownItems={userDropdownItems}
            />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
  
  const Header = (
    <PageHeader
      logo={<Brand src={"images/IntelliApp_Logo_342x76.svg"} alt="Kogito Logo" />}
      logoProps={logoProps}
      toolbar={headerToolbar}
    />
  );

  return (
    <Page header={Header} className="kogito--editor-landing">
      <PageSection variant="dark" className="kogito--editor-landing__title-section pf-u-p-2xl-on-lg">
        <TextContent>
        <Title size="3xl" headingLevel="h1">Asset Editor for Kogito and Process Automation</Title>
        <Text>Welcome to the Asset Editor! This simple BPMN and DMN editor is here to allow you to collaborate in an easy way and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch in the forum or review the documentation for more information.
          </Text> 
        <Text component={TextVariants.small} className="pf-u-text-align-right">Powered by <Brand src={"images/kogito_logo_white.png"} alt="Kogito Logo" style={{height: "1em", verticalAlign: "text-bottom"}}/></Text>
          </TextContent>
      </PageSection>
      <PageSection className="pf-u-px-2xl-on-lg">
        <Gallery gutter="lg" className="kogito--editor-landing__gallery">
            <Card>
              <CardHeader>
                <Title headingLevel="h2" size="2xl">Workflow (.BPMN)</Title>
              </CardHeader>
              <CardBody isFilled={false}>
                BPMN files are used to generate business processes.
              </CardBody>
              <CardBody isFilled={true}>
                <Button variant="link" isInline={true} onClick={() => trySample("bpmn")}>
                  Try Sample
                </Button>
              </CardBody>
              <CardFooter>
                <Button variant="secondary" onClick={() => createFile("bpmn")}>
                  Create new workflow
                </Button>
              </CardFooter>
            </Card>
            <Card>
              <CardHeader>
              <Title headingLevel="h2" size="2xl">Decision model (.DMN)</Title>
              </CardHeader>
              <CardBody isFilled={false}>
                DMN files are used to generate decision models
              </CardBody>
              <CardBody isFilled={true}>
                <Button variant="link" isInline={true} onClick={() => trySample("dmn")}>
                  Try Sample
                </Button>
              </CardBody>
              <CardFooter>
                <Button variant="secondary" onClick={() => createFile("dmn")}>
                  Create new decision model
                </Button>
              </CardFooter>
            </Card>
            <Card>
              <CardHeader>
              <Title headingLevel="h2" size="2xl">Edit existing file</Title>
              </CardHeader>
              <CardBody isFilled={true} className="kogito--editor-landing__upload-box">
                  {/* Upload Drag Target */}
                  <div
                    ref={uploadBoxRef}
                    onDragOver={uploadBoxOnDragOver}
                    onDragLeave={uploadBoxOnDragLeave}
                    onDrop={uploadBoxOnDrop}
                  >
                    <Bullseye>Drop a BPMN or DMN file here</Bullseye>
                  </div>
                </CardBody>
                <CardBody>
                  or
                </CardBody>
              <CardFooter>
                    <Button variant="secondary" onClick={editFile}  className="kogito--editor-landing__upload-btn">
                      Choose a local file
                      {/* Transparent file input overlays the button */}
                      <input className="pf-c-button" type="file" aria-label="File selection" ref={uploadInputRef} onChange={editFile} />
                    </Button>
              </CardFooter>
            </Card>
            <Card>
              <CardHeader>
              <Title headingLevel="h2" size="2xl">Import source code</Title>
              </CardHeader>
              <CardBody isFilled={false}>
                Paste a URL to a source code link (GitHub, Dropbox, etc.)
              </CardBody>
              <CardBody isFilled={true}>
              {/* TODO New feature upload from source code needs to be implemented */}
                <FormGroup
                  label="URL"
                  fieldId="url-text-input"
                  helperText="http://"
                >
                  <TextInput
                    type="url"
                    id="url-text-input"
                    name="urlText"
                    aria-describedby="url-text-input-helper"
                  />
                </FormGroup>
              </CardBody>
              <CardFooter>
                <Button variant="secondary">
                  Import source code
                </Button>
              </CardFooter>
            </Card>
        </Gallery>
      </PageSection>
    </Page>
  );
}

function extractFileExtension(fileName: string) {
  return fileName
    .split(".")
    .pop()
    ?.match(/[\w\d]+/)
    ?.pop();
}

function removeFileExtension(fileName: string) {
  const fileExtension = extractFileExtension(fileName);

  if (!fileExtension) {
    return fileName;
  }

  return fileName.substr(0, fileName.length - fileExtension.length - 1);
}
