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

import { EditorType } from "@kogito-tooling/embedded-editor";
import { Alert, AlertActionCloseButton, AlertVariant, Bullseye, Button, Card, CardBody, CardFooter, CardHeader, Form, FormGroup, Gallery, InputGroup, PageSection, Select, SelectOption, Text, TextContent, TextInput, TextVariants, Title, Toolbar, ToolbarGroup, ToolbarItem, Tooltip } from "@patternfly/react-core";
import { SortAlphaDownIcon } from "@patternfly/react-icons";
import * as electron from "electron";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { File, UNSAVED_FILE_NAME } from "../../common/File";
import { RecentOpenedFile } from "../../common/RecentOpenedFile";
import { extractFileExtension, removeDirectories } from "../../common/utils";
import { GlobalContext } from "../common/GlobalContext";
import IpcRendererEvent = Electron.IpcRendererEvent;

interface Props {
  openFile: (file: File) => void;
  openFileByPath: (filePath: string) => void;
}

enum InputFileUrlState {
  VALID,
  INITIAL,
  INVALID_URL,
  NO_FILE_URL,
  INVALID_EXTENSION
}

enum ImportFileErrorType {
  NONE,
  FETCH,
  RESPONSE
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

enum FileTypeFilter {
  ALL = "All",
  BPMN = "BPMN",
  DMN = "DMN"
}

const typeFilterOptions = [{ value: FileTypeFilter.ALL }, { value: FileTypeFilter.BPMN }, { value: FileTypeFilter.DMN }];

export function FilesPage(props: Props) {
  const context = useContext(GlobalContext);
  const [lastOpenedFiles, setLastOpenedFiles] = useState<RecentOpenedFile[]>([]);

  const [url, setURL] = useState("");
  const [importFileErrorDetails, setImportFileErrorDetails] = useState<{
    type: ImportFileErrorType;
    statusCode?: number;
    description?: string;
  }>({ type: ImportFileErrorType.NONE });

  const [typeFilterSelect, setTypeFilterSelect] = useState({ isExpanded: false, value: FileTypeFilter.ALL });
  const [searchFilter, setSearchFilter] = useState("");
  const [sortAlphaFilter, setSortAlphaFilter] = useState(false);

  const [inputFileUrlState, setInputFileUrlState] = useState(InputFileUrlState.INITIAL);

  const isInputUrlValid = useMemo(
    () => inputFileUrlState === InputFileUrlState.VALID || inputFileUrlState === InputFileUrlState.INITIAL,
    [inputFileUrlState]
  );

  const messageForStateInputUrl = useMemo(() => {
    switch (inputFileUrlState) {
      case InputFileUrlState.INITIAL:
        return "http://";
      case InputFileUrlState.INVALID_EXTENSION:
        return "File type is not supported";
      case InputFileUrlState.INVALID_URL:
        return "Enter a valid URL";
      case InputFileUrlState.NO_FILE_URL:
        return "File URL is not valid";
      default:
        return "";
    }
  }, [inputFileUrlState]);

  const closeImportFileErrorAlert = useCallback(
    () => setImportFileErrorDetails({ type: ImportFileErrorType.NONE }),
    []
  );

  const filteredLastOpenedFiles = useMemo(() => {
    // condition used to avoid MacOS issue during first load
    if (lastOpenedFiles.filter === undefined) {
      return [];
    }

    const filteredFiles = lastOpenedFiles
      .filter(file =>
        removeDirectories(file.filePath)
          ?.toUpperCase()
          .includes(searchFilter.toUpperCase())
      )
      .filter(file => {
        const fileExtension = extractFileExtension(file.filePath)!;
        return (
          typeFilterSelect.value === FileTypeFilter.ALL ||
          fileExtension?.toLowerCase().includes(typeFilterSelect.value.toLowerCase())
        );
      });

    if (sortAlphaFilter) {
      return filteredFiles.sort((file1, file2) => {
        const f1 = removeDirectories(file1.filePath)!.toLowerCase();
        const f2 = removeDirectories(file2.filePath)!.toLowerCase();

        if (f1 < f2) {
          return -1;
        } else if (f1 > f2) {
          return 1;
        }

        return 0;
      });
    }

    return filteredFiles;
  }, [lastOpenedFiles, searchFilter, typeFilterSelect, sortAlphaFilter]);

  const onSelectTypeFilter = useCallback((event, selection) => {
    setTypeFilterSelect({
      isExpanded: false,
      value: selection
    });
  }, []);

  const onToggleTypeFilter = useCallback(
    isExpanded => {
      setTypeFilterSelect({
        isExpanded: isExpanded,
        value: typeFilterSelect.value
      });
    },
    [typeFilterSelect]
  );

  const onChangeSearchFilter = useCallback(newValue => {
    setSearchFilter(newValue);
  }, []);

  const validateFileInput = useCallback((fileUrl: string) => {
    let urlObject: URL;
    try {
      urlObject = new URL(fileUrl);
    } catch (e) {
      setInputFileUrlState(InputFileUrlState.INVALID_URL);
      return;
    }
    const fileType = extractFileExtension(urlObject.pathname);
    if (!fileType) {
      setInputFileUrlState(InputFileUrlState.NO_FILE_URL);
    } else if (!context.router.getLanguageData(fileType)) {
      setInputFileUrlState(InputFileUrlState.INVALID_EXTENSION);
    } else {
      setInputFileUrlState(InputFileUrlState.VALID);
    }
  }, []);

  const inputFileChanged = useCallback((fileUrl: string) => {
    setURL(fileUrl);
    validateFileInput(fileUrl);
  }, []);

  const importFileByUrl = useCallback(() => {
    if (isInputUrlValid && inputFileUrlState !== InputFileUrlState.INITIAL) {
      fetch(url)
        .then(response => {
          if (response.ok) {
            response.text().then(content => {
              const file = {
                filePath: UNSAVED_FILE_NAME,
                fileType: extractFileExtension(url)!,
                fileContent: content
              };

              electron.ipcRenderer.send("setFileMenusEnabled", { enabled: true });
              props.openFile(file);
            });
          } else {
            setImportFileErrorDetails({
              type: ImportFileErrorType.RESPONSE,
              statusCode: response.status,
              description: response.statusText
            });
          }
        })
        .catch(error => {
          setImportFileErrorDetails({ type: ImportFileErrorType.FETCH, description: error.toString() });
        });
    }
  }, [isInputUrlValid, inputFileUrlState, url, props.openFile]);

  const importFileByUrlFormSubmit = useCallback(
    e => {
      e.preventDefault();
      e.stopPropagation();
      importFileByUrl();
    },
    [url]
  );

  const onInputFileUrlBlur = useCallback(() => {
    if (url.trim() === "") {
      setInputFileUrlState(InputFileUrlState.INITIAL);
    }
  }, [url]);

  useEffect(() => {
    electron.ipcRenderer.on(
      "returnLastOpenedFiles",
      (event: electron.IpcRendererEvent, data: { lastOpenedFiles: RecentOpenedFile[] }) => {
        setLastOpenedFiles(data.lastOpenedFiles);
      }
    );

    electron.ipcRenderer.send("requestLastOpenedFiles");

    return () => {
      electron.ipcRenderer.removeAllListeners("returnLastOpenedFiles");
    };
  }, []);

  useEffect(() => {
    if (importFileErrorDetails.type !== ImportFileErrorType.NONE) {
      const autoCloseImportFileErrorAlert = setTimeout(closeImportFileErrorAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseImportFileErrorAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [importFileErrorDetails, closeImportFileErrorAlert]);

  return (
    <>
      <PageSection>
        {importFileErrorDetails.type === ImportFileErrorType.RESPONSE && (
          <div className={"kogito--alert-container"}>
            <Alert
              variant={AlertVariant.danger}
              title="An error happened while fetching your file"
              action={<AlertActionCloseButton onClose={closeImportFileErrorAlert} />}
            >
              <br />
              <b>Error details: </b>
              {importFileErrorDetails.statusCode}
              {importFileErrorDetails.statusCode && importFileErrorDetails.description && " - "}
              {importFileErrorDetails.description}
            </Alert>
          </div>
        )}
        {importFileErrorDetails.type === ImportFileErrorType.FETCH && (
          <div className={"kogito--alert-container"}>
            <Alert
              variant={AlertVariant.danger}
              title="An unexpected error happened while trying to fetch your file"
              action={<AlertActionCloseButton onClose={closeImportFileErrorAlert} />}
            >
              <br />
              <b>Error details: </b>
              {importFileErrorDetails.description}
            </Alert>
          </div>
        )}
        <TextContent>
          <Title size={"2xl"} headingLevel={"h2"}>
            {"Create new file"}
          </Title>
        </TextContent>
        <Gallery gutter="md" className="kogito--desktop__actions-gallery">
          <Card
            className={"kogito--desktop__actions-card"}
            component={"article"}
            isHoverable={false}
            isCompact={true}
            onClick={() => electron.ipcRenderer.send("createNewFile", { type: EditorType.BPMN })}
          >
            <CardHeader>
              {
                <Title size={"xl"} headingLevel={"h3"} className="pf-u-mb-md">
                  {"Blank Workflow (.BPMN)"}
                </Title>
              }
            </CardHeader>
            <CardBody component={"div"} isFilled={true} className="kogito--desktop__actions-card-body">
              {<img src={"images/file_icon_regular.svg"} alt="file icon" className="kogito--desktop__actions-img" />}
            </CardBody>
          </Card>
          <Card
            className={"kogito--desktop__actions-card"}
            component={"article"}
            isHoverable={false}
            isCompact={true}
            onClick={() => electron.ipcRenderer.send("createNewFile", { type: EditorType.DMN })}
          >
            <CardHeader>
              {
                <Title size={"xl"} headingLevel={"h3"} className="pf-u-mb-md">
                  {"Blank Decision Model (.DMN)"}
                </Title>
              }
            </CardHeader>
            <CardBody component={"div"} isFilled={true} className="kogito--desktop__actions-card-body">
              {<img src={"images/file_icon_regular.svg"} alt="file icon" className="kogito--desktop__actions-img" />}
            </CardBody>
          </Card>
          <Card
            className={"kogito--desktop__actions-card"}
            component={"article"}
            isHoverable={false}
            isCompact={true}
            onClick={() => electron.ipcRenderer.send("openSample", { type: EditorType.BPMN })}
          >
            <CardHeader>
              {
                <Title size={"xl"} headingLevel={"h3"} className="pf-u-mb-md">
                  {"Sample Workflow (.BPMN)"}
                </Title>
              }
            </CardHeader>
            <CardBody component={"div"} isFilled={true} className="kogito--desktop__actions-card-body">
              {
                <img
                  src={"images/sample_file_icon_regular.svg"}
                  alt="file icon"
                  className="kogito--desktop__actions-img"
                />
              }
            </CardBody>
          </Card>
          <Card
            className={"kogito--desktop__actions-card"}
            component={"article"}
            isHoverable={false}
            isCompact={true}
            onClick={() => electron.ipcRenderer.send("openSample", { type: EditorType.DMN })}
          >
            <CardHeader>
              {
                <Title size={"xl"} headingLevel={"h3"} className="pf-u-mb-md">
                  {"Sample Decision Model (.DMN)"}
                </Title>
              }
            </CardHeader>
            <CardBody component={"div"} isFilled={true} className="kogito--desktop__actions-card-body">
              {
                <img
                  src={"images/sample_file_icon_regular.svg"}
                  alt="file icon"
                  className="kogito--desktop__actions-img"
                />
              }
            </CardBody>
          </Card>
          <Card className="kogito--desktop__actions-card--wide">
            <CardHeader>
              <Title size={"xl"} headingLevel={"h3"}>
                Open from source
              </Title>
            </CardHeader>
            <CardBody>
              <TextContent>
                <Text component={TextVariants.p}>Paste a URL to a source code link (GitHub, Dropbox, etc.)</Text>
                <Form onSubmit={importFileByUrlFormSubmit} disabled={!isInputUrlValid}>
                  <FormGroup
                    label="URL"
                    fieldId="url-text-input"
                    isValid={isInputUrlValid}
                    helperText=""
                    helperTextInvalid={messageForStateInputUrl}
                  >
                    <TextInput
                      isRequired={true}
                      onBlur={onInputFileUrlBlur}
                      isValid={isInputUrlValid}
                      value={url}
                      onChange={inputFileChanged}
                      type="url"
                      id="url-text-input"
                      name="urlText"
                      aria-describedby="url-text-input-helper"
                      placeholder="URL"
                    />
                  </FormGroup>
                </Form>
              </TextContent>
            </CardBody>
            <CardFooter>
              <Button variant="secondary" onClick={importFileByUrl}>
                Open from source
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
      </PageSection>
      <PageSection variant="light">
        <Title size={"2xl"} headingLevel={"h3"}>
          Recent Files
        </Title>
        <Toolbar>
          {
            <>
              <ToolbarGroup>
                {
                  <Select
                    onSelect={onSelectTypeFilter}
                    onToggle={onToggleTypeFilter}
                    isExpanded={typeFilterSelect.isExpanded}
                    selections={typeFilterSelect.value}
                    width={"7em"}
                  >
                    {typeFilterOptions.map((option, index) => (
                      <SelectOption key={index} value={option.value} />
                    ))}
                  </Select>
                }
              </ToolbarGroup>
              <ToolbarGroup>
                <InputGroup>
                  <TextInput
                    name={"searchInput"}
                    id={"searchInput"}
                    type={"search"}
                    aria-label={"search input example"}
                    placeholder={"Search"}
                    onChange={onChangeSearchFilter}
                  />
                </InputGroup>
              </ToolbarGroup>
              <ToolbarItem>
                <Button
                  data-testid="orderAlphabeticallyButton"
                  variant="plain"
                  aria-label="sort file view"
                  className={sortAlphaFilter ? "kogito--filter-btn-pressed" : "kogito--filter-btn"}
                  onClick={() => setSortAlphaFilter(!sortAlphaFilter)}
                >
                  <SortAlphaDownIcon />
                </Button>
              </ToolbarItem>
              {/* TODO: Implement grid view */}
              {/*<ToolbarGroup className="pf-u-ml-auto">
                <ToolbarItem>
                  <Button variant="plain" aria-label="tiled file view">
                    <ThIcon />
                  </Button>
                </ToolbarItem>
                <ToolbarItem>
                  <Button variant="plain" aria-label="list file view">
                    <ThListIcon />
                  </Button>
                </ToolbarItem>
              </ToolbarGroup>*/}
            </>
          }
        </Toolbar>
      </PageSection>
      <PageSection isFilled={true}>
        {filteredLastOpenedFiles.length === 0 && <Bullseye>No files were opened yet.</Bullseye>}

        {filteredLastOpenedFiles.length > 0 && (
          <Gallery gutter="lg" className="kogito-desktop__file-gallery">
            {filteredLastOpenedFiles.map(file => (
              <Tooltip content={<div>{file.filePath}</div>} key={file.filePath}>
                <Card
                  isCompact={true}
                  onClick={() => props.openFileByPath(file.filePath)}
                  className={"kogito--desktop__files-card"}
                >
                  <CardBody>
                    <Bullseye>
                      <div
                        className={"kogito--desktop__file-img"}
                        style={{
                          backgroundImage: `url("data:image/svg+xml,${encodeURIComponent(file.preview)}")`
                        }}
                      />
                    </Bullseye>
                  </CardBody>
                  <CardFooter>
                    <Title headingLevel="h3" size="xs" className="kogito--desktop__filename">
                      {removeDirectories(file.filePath)}
                    </Title>
                  </CardFooter>
                </Card>
              </Tooltip>
            ))}
          </Gallery>
        )}
      </PageSection>
    </>
  );
}
