/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { PromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceGitStatusType } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import {
  FileModificationStatus,
  UnstagedModifiedFilesStatusEntryType,
} from "@kie-tools-core/workspaces-git-fs/dist/services/GitService";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

import { Alert, AlertActionLink, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import {
  Dropdown,
  DropdownDirection,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { CaretLeftIcon } from "@patternfly/react-icons/dist/js/icons/caret-left-icon";
import { CaretRightIcon } from "@patternfly/react-icons/dist/js/icons/caret-right-icon";
import { CaretUpIcon } from "@patternfly/react-icons/dist/js/icons/caret-up-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { UndoAltIcon } from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/js/icons/warning-triangle-icon";
import * as React from "react";
import { useOnlineI18n } from "../../i18n";
import { switchExpression } from "../../switchExpression/switchExpression";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip";

type Directions = "up" | "down" | "right" | "left";

export type GitStatusProps = {
  workspaceDescriptor: WorkspaceDescriptor;
  workspaceGitStatusPromise: PromiseState<WorkspaceGitStatusType>;
};

export enum GitStatusIndicatorActionVariant {
  dropdown = "dropdown",
  popover = "popover",
}

export enum SupportedActions {
  revert = "revert",
  revertAll = "revertAll",
}

type GitStatusIndicatorActionType = {
  id: SupportedActions;
  title: string;
  titleIcon: JSX.Element;
  description: JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
};

type GitStatusIndicatorActionsPropsBase = {
  gitStatusProps: GitStatusProps;
  variant: string;
  workspaceFile?: WorkspaceFile;
  currentWorkspaceFile?: WorkspaceFile;
  onDeletedWorkspaceFile?: () => void;
};
type GitStatusIndicatorActionsDropdownProps = GitStatusIndicatorActionsPropsBase & {
  variant: GitStatusIndicatorActionVariant.dropdown;
  expandDirection: "right" | "left";
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
  toggle?: JSX.Element;
};
type GitStatusIndicatorActionsPopoverProps = GitStatusIndicatorActionsPropsBase & {
  variant: GitStatusIndicatorActionVariant.popover;
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
};
export type GitStatusIndicatorActionsProps =
  | GitStatusIndicatorActionsDropdownProps
  | GitStatusIndicatorActionsPopoverProps;

export const GitStatusIndicatorActions = (props: GitStatusIndicatorActionsProps) => {
  const { i18n } = useOnlineI18n();
  const workspaces = useWorkspaces();

  const isPopoverVariant = (
    maybePopoverProps: GitStatusIndicatorActionsProps
  ): maybePopoverProps is GitStatusIndicatorActionsPopoverProps => {
    return maybePopoverProps.variant === GitStatusIndicatorActionVariant.popover;
  };

  const isDropdownVariant = (
    maybeDropdownProps: GitStatusIndicatorActionsProps
  ): maybeDropdownProps is GitStatusIndicatorActionsDropdownProps => {
    return maybeDropdownProps.variant === GitStatusIndicatorActionVariant.dropdown;
  };

  const stageStatusFilter = React.useCallback(
    (applicableFor: FileModificationStatus[]) => {
      return (stageStatusEntry: UnstagedModifiedFilesStatusEntryType) => {
        return (
          applicableFor.includes(stageStatusEntry.status) &&
          (props.workspaceFile
            ? [props.workspaceFile.relativePath]
            : props.gitStatusProps.workspaceGitStatusPromise!.data!.unstagedModifiedFilesStatus.map(({ path }) => path)
          ).includes(stageStatusEntry.path)
        );
      };
    },
    [props.workspaceFile, props.gitStatusProps.workspaceGitStatusPromise]
  );

  const doRevert = React.useCallback(
    async (filepaths: string[]) => {
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
    },
    [props, workspaces]
  );

  const actionRevert = React.useCallback((): GitStatusIndicatorActionType | undefined => {
    const maybeWorkspaceGitStatus = props.gitStatusProps.workspaceGitStatusPromise?.data;
    if (!maybeWorkspaceGitStatus) {
      return;
    }
    const id = props.workspaceFile ? SupportedActions.revert : SupportedActions.revertAll;

    const filepaths = maybeWorkspaceGitStatus.unstagedModifiedFilesStatus
      .filter(
        stageStatusFilter([
          FileModificationStatus.deleted,
          FileModificationStatus.modified,
          FileModificationStatus.added,
        ])
      )
      .map((it) => it.path);

    if (!filepaths.length) {
      return;
    }
    return {
      onConfirm: async () => await doRevert(filepaths),
      id,
      title: i18n.gitStatusIndicatorActions[id].title,
      titleIcon: <UndoAltIcon />,
      alertVariant: AlertVariant.warning,
      description: (
        <>
          <span>{i18n.gitStatusIndicatorActions[id].description}</span>
          <List>
            {filepaths.map((it, index) => (
              <ListItem key={id + index}>
                <Text component={TextVariants.small}>
                  <Truncate
                    content={it}
                    position={"middle"}
                    trailingNumChars={Math.max(it.length - it.lastIndexOf("/"), 10)}
                    tooltipPosition={TooltipPosition.leftStart}
                  />
                </Text>
              </ListItem>
            ))}
          </List>
        </>
      ),
      confirmButtonText: i18n.gitStatusIndicatorActions[id].confirmButtonText,
    };
  }, [
    doRevert,
    i18n.gitStatusIndicatorActions,
    props.gitStatusProps.workspaceGitStatusPromise?.data,
    props.workspaceFile,
    stageStatusFilter,
  ]);

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

  return switchExpression(props.variant, {
    dropdown: isDropdownVariant(props) ? (
      <ActionsDropdown
        isOpen={props.isOpen}
        setOpen={props.setOpen}
        dropdownPosition={DropdownPosition.left}
        toggle={
          props.toggle ?? (
            <DropdownToggle
              isPlain
              onToggle={(value, ev) => {
                ev.stopPropagation();
                ev.preventDefault();
                props.setOpen(value);
              }}
              title={"Git Status related operations."}
              className={"kie-tools--no-whitespace-dropdown-toggle"}
            />
          )
        }
        dropdownItems={actions.map((it) => (
          <ActionsDropdownItem
            key={it.id}
            titleText={it.title}
            titleIcon={it.titleIcon}
            description={it.description}
            confirmButtonText={it.confirmButtonText}
            alertVariant={it.alertVariant}
            expandDirection={props.expandDirection}
            onConfirm={() => {
              it.onConfirm();
              props.setOpen(false);
            }}
          />
        ))}
      />
    ) : (
      <></>
    ),
    popover: isPopoverVariant(props) ? (
      <ActionsPopoverButtons actions={actions} isOpen={props.isOpen} setOpen={props.setOpen} />
    ) : (
      <></>
    ),
  });
};

const ActionsDropdown = (props: {
  dropdownPosition: DropdownPosition;
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
  toggle: JSX.Element;
  dropdownItems: JSX.Element[];
}) => {
  return (
    <Dropdown
      position={props.dropdownPosition}
      direction={DropdownDirection.down}
      isOpen={props.isOpen}
      isPlain
      isFullHeight={false}
      toggle={props.toggle}
      dropdownItems={props.dropdownItems}
      className={switchExpression(props.dropdownPosition, {
        right: "kie-tools--alert-dropdown-right",
        left: "kie-tools--alert-dropdown-left",
      })}
    />
  );
};

const ActionsDropdownItem = (props: {
  titleText: string;
  titleIcon: React.ReactNode;
  description: string | JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
  expandDirection: "left" | "right";
}) => {
  const [isExpanded, setIsExpanded] = React.useState(false);
  return (
    <DropdownItem
      key={props.titleText}
      component={"span"}
      onClick={(ev) => {
        setIsExpanded(!isExpanded);
        ev.stopPropagation();
      }}
      className={"kie-tools--alert-dropdown-item"}
    >
      <ActionsAlert
        alertVariant={props.alertVariant}
        isExpanded={isExpanded}
        setIsExpanded={setIsExpanded}
        expandDirection={props.expandDirection}
        titleText={props.titleText}
        titleIcon={props.titleIcon}
        description={props.description}
        confirmButtonText={props.confirmButtonText}
        onConfirm={() => {
          props.onConfirm();
          setIsExpanded(false);
        }}
      />
    </DropdownItem>
  );
};

const ActionsPopoverButtons = (props: {
  actions: GitStatusIndicatorActionType[];
  isOpen: boolean;
  setOpen: (value: boolean) => void;
}) => {
  const [openedPopoverId, setOpenedPopoverId] = React.useState<string>();
  return (
    <Flex spaceItems={{ default: "spaceItemsXs" }}>
      {props.actions.map((action) => (
        <ActionsPopover
          key={action.id}
          titleText={action.title}
          titleIcon={action.titleIcon}
          description={action.description}
          confirmButtonText={action.confirmButtonText}
          alertVariant={action.alertVariant}
          onConfirm={() => {
            action.onConfirm();
            setOpenedPopoverId(undefined);
            props.setOpen(false);
          }}
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

const ActionsPopover = (props: {
  titleText: string;
  titleIcon: JSX.Element;
  description: string | JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
  isOpen: boolean;
  setOpen: (isOpen: boolean) => void;
}) => {
  return (
    <Popover
      id={`actions-popover-${props.titleText.replace(/\s/g, "_")}`}
      bodyContent={
        <ActionsAlert
          titleText={props.titleText}
          titleIcon={props.titleIcon}
          description={props.description}
          alertVariant={props.alertVariant}
          confirmButtonText={props.confirmButtonText}
          onConfirm={props.onConfirm}
          contentDirection={"right"}
          isExpanded={true}
        />
      }
      isVisible={props.isOpen}
    >
      <Button
        className={"kie-tools--masthead-hoverable"}
        variant={"plain"}
        onClick={(ev) => {
          props.setOpen(!props.isOpen);
          ev.stopPropagation();
          ev.preventDefault();
        }}
        title={props.titleText}
      >
        {props.titleIcon}
      </Button>
    </Popover>
  );
};

const ActionsAlert = (props: {
  titleText: string;
  titleIcon: React.ReactNode;
  description: string | JSX.Element;
  alertVariant: AlertVariant;
  confirmButtonText: string;
  onConfirm: () => void;
  expandDirection?: Directions;
  contentDirection?: Directions;
  isExpanded: boolean;
  setIsExpanded?: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  return (
    <Alert
      isInline
      variant={props.alertVariant}
      isPlain={true}
      customIcon={<></>}
      title={
        <AdjustableAlertToggle
          isExpanded={props.isExpanded}
          onToggle={(isExpanded: boolean) => {
            props.setIsExpanded && props.setIsExpanded(isExpanded);
          }}
          contentId={props.titleText}
          direction={"down"}
          closedCaretDirection={props.expandDirection}
          contentDirection={props.contentDirection}
          title={props.titleText}
          icon={props.titleIcon}
        >
          {props.titleText}
        </AdjustableAlertToggle>
      }
      actionLinks={
        props.isExpanded && (
          <>
            <AlertActionLink
              onClick={(ev) => {
                props.onConfirm();
                ev.stopPropagation();
                ev.preventDefault();
              }}
              title={props.confirmButtonText}
            >
              {props.confirmButtonText}
            </AlertActionLink>
          </>
        )
      }
      className={props.isExpanded ? "kie-tools--git-status-indicator-actions-alert-expanded" : ""}
    >
      {props.isExpanded && (
        <Flex direction={{ default: "row" }} flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            {switchExpression(props.alertVariant, {
              warning: <WarningTriangleIcon />,
              danger: <ExclamationCircleIcon />,
              success: <CheckCircleIcon />,
              default: <InfoCircleIcon />,
            })}
          </FlexItem>
          <FlexItem>{props.description}</FlexItem>
        </Flex>
      )}
    </Alert>
  );
};

const AdjustableAlertToggle = (props: {
  children: React.ReactNode;
  className?: string;
  isExpanded: boolean;
  onToggle?: (isOpen: boolean) => void;
  contentId: string;
  closedCaretDirection?: Directions;
  contentDirection?: Directions;
  direction?: Directions;
  icon?: React.ReactNode;
  title?: string;
}) => {
  const iconForDirection = React.useCallback((direction: Directions) => {
    return switchExpression(direction, {
      down: <CaretDownIcon />,
      up: <CaretUpIcon />,
      left: <CaretLeftIcon />,
      right: <CaretRightIcon />,
      default: <CaretDownIcon />,
    });
  }, []);

  const currentCaretIcon = React.useMemo(() => {
    if (!props.direction || !props.closedCaretDirection) {
      return;
    }
    return props.isExpanded ? iconForDirection(props.direction) : iconForDirection(props.closedCaretDirection);
  }, [iconForDirection, props.closedCaretDirection, props.direction, props.isExpanded]);

  const caretIconPosition = React.useMemo(() => {
    const position: "left" | "right" = switchExpression(props.contentDirection ?? props.closedCaretDirection, {
      down: "right",
      up: "left",
      left: "left",
      right: "right",
      default: "right",
    });
    return position;
  }, [props.closedCaretDirection, props.contentDirection]);

  return (
    <Flex
      direction={{ default: caretIconPosition === "left" ? "row" : "rowReverse" }}
      alignItems={{ default: "alignItemsFlexEnd" }}
      spaceItems={{ default: "spaceItemsXs" }}
      aria-expanded={props.isExpanded}
      aria-controls={props.contentId}
      onClick={(ev) => {
        props.onToggle?.(!props.isExpanded);
        ev.stopPropagation();
      }}
      flexWrap={{ default: "nowrap" }}
      title={props.title}
    >
      {currentCaretIcon && <FlexItem grow={{ default: "grow" }}>{currentCaretIcon}</FlexItem>}
      <FlexItem>{props.children}</FlexItem>
      <FlexItem>{props.icon}</FlexItem>
    </Flex>
  );
};
