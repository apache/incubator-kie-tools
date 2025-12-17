/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { PromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceGitStatusType } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import {
  FileModificationStatus,
  UnstagedModifiedFilesStatusEntryType,
} from "@kie-tools-core/workspaces-git-fs/dist/services/GitService";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

import {
  Alert,
  AlertActionCloseButton,
  AlertActionLink,
  AlertVariant,
} from "@patternfly/react-core/dist/js/components/Alert";
import { Dropdown, DropdownItem, DropdownPosition, KebabToggle } from "@patternfly/react-core/deprecated";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Popover, PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { UndoAltIcon } from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";
import * as React from "react";
import { useOnlineI18n } from "../../i18n";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { listDeletedFiles } from "./WorkspaceStatusIndicator";
import { FileLabel } from "../../filesList/FileLabel";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export type GitStatusProps = {
  workspaceDescriptor: WorkspaceDescriptor;
  workspaceGitStatusPromise: PromiseState<WorkspaceGitStatusType>;
};

export enum SupportedActions {
  revert = "revert",
  revertAll = "revertAll",
}

type GitStatusIndicatorActionType = {
  id: SupportedActions;
  titleText: string;
  warningText: string;
  titleIcon: JSX.Element;
  description: JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
};
interface GitStatusIndicatorActionsPropsBase {
  gitStatusProps: GitStatusProps;
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
  currentWorkspaceFile?: WorkspaceFile;
  onDeletedWorkspaceFile?: () => void;
}

interface GitStatusIndicatorActionsSingleFileProps extends GitStatusIndicatorActionsPropsBase {
  workspaceFile: WorkspaceFile;
}
interface GitStatusIndicatorActionsMultipleFilesProps extends GitStatusIndicatorActionsPropsBase {
  workspaceFiles: WorkspaceFile[];
}
const isSingleFile = (
  maybeSingleFile: GitStatusIndicatorActionsSingleFileProps | GitStatusIndicatorActionsMultipleFilesProps
): maybeSingleFile is GitStatusIndicatorActionsSingleFileProps => {
  return "workspaceFile" in maybeSingleFile;
};

export const GitStatusIndicatorActions = (
  props: GitStatusIndicatorActionsSingleFileProps | GitStatusIndicatorActionsMultipleFilesProps
) => {
  const { i18n } = useOnlineI18n();
  const workspaces = useWorkspaces();

  const renderFile = React.useCallback(
    (file: WorkspaceFile) => (
      <Flex direction={{ default: "row" }} flexWrap={{ default: "nowrap" }} spacer={{ default: "spacerMd" }}>
        <FlexItem style={{ minWidth: 0 /* This is to make the flex parent not overflow horizontally */ }}>
          <Tooltip distance={5} position={"top-start"} content={file.nameWithoutExtension}>
            <TextContent>
              <Text component={TextVariants.p}>{file.nameWithoutExtension}</Text>
            </TextContent>
          </Tooltip>
        </FlexItem>
        <FlexItem>
          <FileLabel extension={file.extension} />
        </FlexItem>
        <FlexItem>
          {switchExpression(
            props.gitStatusProps.workspaceGitStatusPromise.data?.unstagedModifiedFilesStatus.find(
              (entry) => entry.path === file.relativePath
            )?.status,
            {
              added: (
                <Tooltip content={"Added"} position={"right"}>
                  <small>
                    <i>A</i>
                  </small>
                </Tooltip>
              ),
              deleted: (
                <Tooltip content={"Deleted"} position={"right"}>
                  <small>
                    <i>D</i>
                  </small>
                </Tooltip>
              ),
              modified: (
                <Tooltip content={"Modified"} position={"right"}>
                  <small>
                    <i>M</i>
                  </small>
                </Tooltip>
              ),
              default: <></>,
            }
          )}
        </FlexItem>
      </Flex>
    ),
    [props.gitStatusProps.workspaceGitStatusPromise.data?.unstagedModifiedFilesStatus]
  );

  const workspaceFilesRendered = React.useMemo(() => {
    return isSingleFile(props) ? (
      <b>{renderFile(props.workspaceFile)}</b>
    ) : (
      <b>
        <List>
          {[...props.workspaceFiles, ...listDeletedFiles(props.gitStatusProps)]
            .filter((file) =>
              props.gitStatusProps.workspaceGitStatusPromise.data?.unstagedModifiedFilesStatus.some(
                (entry) => entry.path === file.relativePath
              )
            )
            .map((file, index) => (
              <ListItem key={index}>{renderFile(file)}</ListItem>
            ))}
        </List>
      </b>
    );
  }, [props, renderFile]);

  const filterAcceptedStageStatuses = React.useCallback(
    (acceptedValues: FileModificationStatus[]) => {
      return (stageStatusEntry: UnstagedModifiedFilesStatusEntryType) => {
        return (
          acceptedValues.includes(stageStatusEntry.status) &&
          (isSingleFile(props)
            ? [props.workspaceFile.relativePath]
            : props.gitStatusProps.workspaceGitStatusPromise!.data!.unstagedModifiedFilesStatus.map(({ path }) => path)
          ).includes(stageStatusEntry.path)
        );
      };
    },
    [props]
  );

  const actionRevert = React.useCallback((): GitStatusIndicatorActionType | undefined => {
    const doRevert = async (filepaths: string[]) => {
      await workspaces.checkoutFilesFromLocalHead({
        workspaceId: props.gitStatusProps.workspaceDescriptor.workspaceId,
        ref: props.gitStatusProps.workspaceDescriptor.origin.branch,
        filepaths,
      });
      if (
        props.currentWorkspaceFile &&
        !(await workspaces.existsFile({
          workspaceId: props.gitStatusProps.workspaceDescriptor.workspaceId,
          relativePath: props.currentWorkspaceFile.relativePath,
        }))
      ) {
        props.onDeletedWorkspaceFile?.();
      }
    };
    const maybeWorkspaceGitStatus = props.gitStatusProps.workspaceGitStatusPromise?.data;

    if (!maybeWorkspaceGitStatus) {
      return;
    }
    const id = isSingleFile(props) ? SupportedActions.revert : SupportedActions.revertAll;

    const filepaths = maybeWorkspaceGitStatus.unstagedModifiedFilesStatus
      .filter(
        filterAcceptedStageStatuses([
          FileModificationStatus.deleted,
          FileModificationStatus.modified,
          FileModificationStatus.added,
        ])
      )
      .map(({ path }) => path);

    if (!filepaths.length) {
      return;
    }
    return {
      onConfirm: async () => await doRevert(filepaths),
      id,
      titleText: i18n.gitStatusIndicatorActions[id].title,
      titleIcon: <UndoAltIcon />,
      alertVariant: AlertVariant.warning,
      warningText: i18n.gitStatusIndicatorActions[id].warning,
      description: (
        <>
          <span>{i18n.gitStatusIndicatorActions[id].description}</span>
          <br />
          <br />
          {workspaceFilesRendered}
          <br />
          <Divider />
        </>
      ),
      confirmButtonText: i18n.gitStatusIndicatorActions[id].confirmButtonText,
    };
  }, [i18n.gitStatusIndicatorActions, props, filterAcceptedStageStatuses, workspaceFilesRendered, workspaces]);

  const actions: GitStatusIndicatorActionType[] = React.useMemo(() => {
    const maybeActions = [
      // list all actions here, which (based on internal state) could be undefined
      actionRevert(),
    ];

    const result: GitStatusIndicatorActionType[] = [];

    maybeActions.forEach((action) => {
      if (action !== undefined) {
        result.push(action);
      }
    });

    return result;
  }, [actionRevert]);

  return isSingleFile(props) ? (
    <MultipleActionsButtonsWithPopover actions={actions} isOpen={props.isOpen} setOpen={props.setOpen} />
  ) : (
    <MultipleActionsPopoverWithDropdown
      title={
        <TextContent>
          <Text component="h3">Local Changes</Text>
        </TextContent>
      }
      description={
        <Flex direction={{ default: "column" }}>
          <Divider style={{ marginBottom: "1.5rem" }} />
          {workspaceFilesRendered}
        </Flex>
      }
      actions={actions}
      isOpen={props.isOpen}
      setOpen={props.setOpen}
    />
  );
};

const MultipleActionsButtonsWithPopover = (props: {
  actions: GitStatusIndicatorActionType[];
  isOpen: boolean;
  setOpen: (value: boolean) => void;
}) => {
  const [openedPopoverId, setOpenedPopoverId] = React.useState<string>();
  return (
    <Flex spaceItems={{ default: "spaceItemsXs" }}>
      {props.actions.map((action) => (
        <SingleActionPopover
          key={action.id}
          action={action}
          isOpen={props.isOpen && openedPopoverId === action.id}
          setOpen={(isOpen: boolean) => {
            setOpenedPopoverId(isOpen ? action.id : undefined);
            props.setOpen(isOpen);
          }}
        />
      ))}
    </Flex>
  );
};

const MultipleActionsPopoverWithDropdown = (props: {
  actions: GitStatusIndicatorActionType[];
  title: React.ReactNode;
  description: JSX.Element;
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
}) => {
  const [selectedActionId, setSelectedActionId] = React.useState<string>();
  const [isDropdownOpen, setDropdownOpen] = React.useState<boolean>(false);

  const selectedAction = React.useMemo(() => {
    return props.actions.find((action) => action.id === selectedActionId);
  }, [props.actions, selectedActionId]);

  return (
    <Popover
      id={"popover-with-actions-dropdown"}
      className={selectedActionId !== undefined ? " kie-tools--git-status-indicator__popover-no-padding" : ""} // noPadding prop not working nice for the ActionsAlert
      position={PopoverPosition.bottom}
      showClose={false}
      isVisible={props.isOpen}
      shouldOpen={() => props.setOpen(true)}
      shouldClose={() => {
        props.setOpen(false);
        setSelectedActionId(undefined);
      }}
      minWidth={"500px"}
      bodyContent={
        selectedAction ? (
          <ActionsAlert
            alertVariant={selectedAction.alertVariant}
            description={selectedAction.description}
            titleText={selectedAction.warningText}
            onConfirm={() => {
              selectedAction.onConfirm();
              setSelectedActionId(undefined);
              props.setOpen(false);
            }}
            confirmButtonText={selectedAction.confirmButtonText}
            onClose={() => setSelectedActionId(undefined)}
          />
        ) : (
          <Flex direction={{ default: "column" }} spacer={{ default: "spacerLg" }}>
            <Flex direction={{ default: "row" }} justifyContent={{ default: "justifyContentSpaceBetween" }}>
              <FlexItem>{props.title}</FlexItem>
              <FlexItem>
                <Dropdown
                  id="dd-popover-with-actions-dropdown"
                  dropdownItems={props.actions.map((action) => (
                    <DropdownItem key={action.id} onClick={() => setSelectedActionId(action.id)}>
                      <Flex
                        direction={{ default: "row" }}
                        spacer={{ default: "spacerMd" }}
                        flexWrap={{ default: "nowrap" }}
                      >
                        <FlexItem>{action.titleIcon}</FlexItem>
                        <FlexItem>
                          <TextContent>
                            <Text component="h4">{action.titleText}</Text>
                          </TextContent>
                        </FlexItem>
                      </Flex>
                    </DropdownItem>
                  ))}
                  isOpen={isDropdownOpen}
                  isPlain
                  position={DropdownPosition.right}
                  onSelect={() => {
                    setDropdownOpen(false);
                  }}
                  toggle={
                    <KebabToggle
                      onToggle={(_event, value) => {
                        setDropdownOpen(value);
                      }}
                    />
                  }
                />
              </FlexItem>
            </Flex>
            {props.description}
          </Flex>
        )
      }
    >
      <Button
        variant="link"
        icon={<CaretDownIcon />}
        onClick={(ev) => {
          ev.stopPropagation();
          ev.preventDefault();
        }}
      />
    </Popover>
  );
};

const SingleActionPopover = (props: {
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
  action: GitStatusIndicatorActionType;
}) => {
  return (
    <Popover
      id={`actions-popover`}
      bodyContent={
        <ActionsAlert
          titleText={props.action.warningText}
          description={props.action.description}
          confirmButtonText={props.action.confirmButtonText}
          alertVariant={props.action.alertVariant}
          onConfirm={props.action.onConfirm}
          onClose={() => props.setOpen(false)}
        />
      }
      className={"kie-tools--git-status-indicator__popover-no-padding"}
      isVisible={props.isOpen}
      showClose={false}
      shouldClose={() => props.setOpen(false)}
      shouldOpen={() => props.setOpen(true)}
    >
      <Button
        className={"kie-tools--masthead-hoverable"}
        variant={"plain"}
        onClick={(ev) => {
          ev.stopPropagation();
          ev.preventDefault();
        }}
        title={props.action.titleText}
      >
        {props.action.titleIcon}
      </Button>
    </Popover>
  );
};

const ActionsAlert = (props: {
  titleText: string;
  description: string | JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
  onClose: () => void;
}) => {
  return (
    <Alert
      isInline
      variant={props.alertVariant}
      title={props.titleText}
      actionClose={<AlertActionCloseButton onClose={props.onClose} />}
      actionLinks={
        <>
          <AlertActionLink
            className={"kie-tools--masthead-hoverable"}
            onClick={(ev) => {
              props.onConfirm();
              ev.stopPropagation();
              ev.preventDefault();
            }}
            title={props.confirmButtonText}
            variant={"link"}
            style={{ color: "var(--pf-v5-global--danger-color--200)", fontWeight: "bold", padding: "8px" }}
          >
            {props.confirmButtonText}
          </AlertActionLink>
        </>
      }
    >
      <Flex direction={{ default: "row" }} flexWrap={{ default: "nowrap" }}>
        <FlexItem grow={{ default: "grow" }}>{props.description}</FlexItem>
      </Flex>
    </Alert>
  );
};
