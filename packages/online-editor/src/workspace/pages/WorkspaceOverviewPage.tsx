/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useState } from "react";
import { WorkspaceFile } from "../WorkspacesContext";
import { Link } from "react-router-dom";
import { SUPPORTED_FILES_EDITABLE } from "../SupportedFiles";
import { OnlineEditorPage } from "../../home/pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useGlobals } from "../../common/GlobalContext";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useHistory } from "react-router";
import { useWorkspacePromise } from "../hooks/WorkspaceHooks";
import { Gallery, GalleryItem } from "@patternfly/react-core/dist/js/layouts/Gallery";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";

import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { Dropdown, DropdownPosition, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { NewFileDropdownItems } from "../../editor/NewFileDropdownItems";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { FileLabel } from "./FileLabel";
import { PromiseStateWrapper } from "../hooks/PromiseState";

export interface Props {
  workspaceId: string;
}

export function WorkspaceOverviewPage(props: Props) {
  const history = useHistory();
  const globals = useGlobals();
  const { workspacePromise, addEmptyWorkspaceFile } = useWorkspacePromise(props.workspaceId);
  const [isNewFileDropdownOpen, setNewFileDropdownOpen] = useState(false);

  return (
    <OnlineEditorPage>
      <PageSection isFilled={true}>
        <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
          <Button variant="link" isInline={true} icon={<AngleLeftIcon />} onClick={() => history.goBack()}>
            Back
          </Button>
          <br />
          <br />
          <PromiseStateWrapper
            promise={workspacePromise}
            pending={<div>Loading......</div>}
            rejected={() => <></>}
            resolved={(workspace) => (
              <>
                <Gallery maxWidths={{ default: "100%", lg: "50%", md: "100%" }}>
                  <GalleryItem>
                    <TextContent>
                      <Text component={TextVariants.h1}>{workspace.descriptor.name}</Text>
                    </TextContent>
                    <br />
                    <DescriptionList>
                      <DescriptionListGroup>
                        <DescriptionListTerm>Id</DescriptionListTerm>
                        <DescriptionListDescription>{workspace.descriptor.workspaceId}</DescriptionListDescription>
                      </DescriptionListGroup>
                      <DescriptionListGroup>
                        <DescriptionListTerm>Type</DescriptionListTerm>
                        <DescriptionListDescription>{workspace.descriptor.origin.kind}</DescriptionListDescription>
                      </DescriptionListGroup>
                      <DescriptionListGroup>
                        <DescriptionListTerm>Created in</DescriptionListTerm>
                        <DescriptionListDescription>{workspace.descriptor.createdIn}</DescriptionListDescription>
                      </DescriptionListGroup>
                    </DescriptionList>
                  </GalleryItem>
                  <GalleryItem>
                    <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                      <FlexItem>
                        <TextContent>
                          <Text component={TextVariants.h2}>Files</Text>
                        </TextContent>
                      </FlexItem>
                      <FlexItem>
                        {workspace.files.length > 0 && (
                          <Dropdown
                            onSelect={() => setNewFileDropdownOpen(false)}
                            toggle={
                              <DropdownToggle
                                toggleIndicator={null}
                                onToggle={(isOpen) => setNewFileDropdownOpen(isOpen)}
                              >
                                <Button
                                  variant="link"
                                  isInline={true}
                                  icon={<PlusIcon />}
                                  onClick={() => {
                                    /**/
                                  }}
                                >
                                  New file
                                </Button>
                              </DropdownToggle>
                            }
                            isPlain={true}
                            isOpen={isNewFileDropdownOpen}
                            dropdownItems={[
                              <NewFileDropdownItems
                                key={"new-file-dropdown-items"}
                                workspace={workspace}
                                addEmptyWorkspaceFile={addEmptyWorkspaceFile}
                              />,
                            ]}
                            position={DropdownPosition.right}
                          />
                        )}
                      </FlexItem>
                    </Flex>
                    <br />
                    {workspace.files.length <= 0 && (
                      <EmptyState>
                        <EmptyStateIcon icon={CubesIcon} />
                        <Title headingLevel="h4" size="lg">
                          {`You currently don't have any files.`}
                        </Title>
                        <EmptyStateBody>{`Create a new file`}</EmptyStateBody>
                        <EmptyStateSecondaryActions>
                          <Flex grow={{ default: "grow" }}>
                            <FlexItem>
                              <Button
                                isLarge
                                variant={ButtonVariant.secondary}
                                onClick={() =>
                                  addEmptyWorkspaceFile("bpmn").then((file) => {
                                    history.push({
                                      pathname: globals.routes.workspaceWithFilePath.path({
                                        workspaceId: file.workspaceId,
                                        filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                                        extension: file.extension,
                                      }),
                                    });
                                  })
                                }
                              >
                                BPMN
                              </Button>
                            </FlexItem>
                            <FlexItem>
                              <Button
                                isLarge
                                variant={ButtonVariant.secondary}
                                onClick={() =>
                                  addEmptyWorkspaceFile("dmn").then((file) => {
                                    history.push({
                                      pathname: globals.routes.workspaceWithFilePath.path({
                                        workspaceId: file.workspaceId,
                                        filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                                        extension: file.extension,
                                      }),
                                    });
                                  })
                                }
                              >
                                DMN
                              </Button>
                            </FlexItem>
                            <FlexItem>
                              <Button
                                isLarge
                                variant={ButtonVariant.secondary}
                                onClick={() =>
                                  addEmptyWorkspaceFile("pmml").then((file) => {
                                    history.push({
                                      pathname: globals.routes.workspaceWithFilePath.path({
                                        workspaceId: file.workspaceId,
                                        filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                                        extension: file.extension,
                                      }),
                                    });
                                  })
                                }
                              >
                                PMML
                              </Button>
                            </FlexItem>
                          </Flex>
                        </EmptyStateSecondaryActions>
                      </EmptyState>
                    )}

                    {workspace.files.length > 0 && (
                      <>
                        <DataList aria-label="draggable data list example" isCompact>
                          {workspace.files.map((file: WorkspaceFile) => (
                            <React.Fragment key={file.path}>
                              <DataListItem aria-labelledby="simple-item1" id="data1" key="1">
                                <DataListItemRow>
                                  <DataListItemCells
                                    dataListCells={[
                                      <DataListCell key={"label"} isIcon={true} style={{ minWidth: "60px" }}>
                                        <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
                                          <FileLabel extension={file.extension} />
                                        </Flex>
                                      </DataListCell>,
                                      <DataListCell key="link" isFilled={false}>
                                        {SUPPORTED_FILES_EDITABLE.includes(file.extension) ? (
                                          <Link
                                            to={globals.routes.workspaceWithFilePath.path({
                                              workspaceId: workspace.descriptor.workspaceId,
                                              filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                                              extension: file.extension,
                                            })}
                                          >
                                            {file.pathRelativeToWorkspaceRoot}
                                          </Link>
                                        ) : (
                                          <Text component={TextVariants.p}>{file.pathRelativeToWorkspaceRoot}</Text>
                                        )}
                                      </DataListCell>,
                                    ]}
                                  />
                                </DataListItemRow>
                              </DataListItem>
                            </React.Fragment>
                          ))}
                        </DataList>
                      </>
                    )}
                  </GalleryItem>
                </Gallery>
              </>
            )}
          />
        </PageSection>
      </PageSection>
    </OnlineEditorPage>
  );
}
