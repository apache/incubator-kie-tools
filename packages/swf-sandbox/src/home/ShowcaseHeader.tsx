import React, { useCallback, useState } from "react";
import {
  Avatar,
  Brand,
  Button,
  ButtonVariant,
  Dropdown,
  DropdownGroup,
  DropdownToggle,
  DropdownItem,
  KebabToggle,
  Masthead,
  MastheadToggle,
  MastheadMain,
  MastheadBrand,
  MastheadContent,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
  PageToggleButton,
  Divider,
} from "@patternfly/react-core";
import BarsIcon from "@patternfly/react-icons/dist/esm/icons/bars-icon";
import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import HelpIcon from "@patternfly/react-icons/dist/esm/icons/help-icon";
import QuestionCircleIcon from "@patternfly/react-icons/dist/esm/icons/question-circle-icon";
import AttentionBellIcon from "@patternfly/react-icons/dist/esm/icons/attention-bell-icon";

export function ShowcaseHeader() {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isKebabDropdownOpen, setIsKebabDropdownOpen] = useState(false);
  const [isFullKebabDropdownOpen, setIsFullKebabDropdownOpen] = useState(false);

  const onDropdownToggle = useCallback((isOpen: boolean) => {
    setIsDropdownOpen(isOpen);
  }, []);

  const onDropdownSelect = useCallback(() => {
    setIsDropdownOpen((isDropdownOpen) => !isDropdownOpen);
  }, []);

  const onKebabDropdownToggle = useCallback((isOpen: boolean) => {
    setIsKebabDropdownOpen(isOpen);
  }, []);

  const onKebabDropdownSelect = useCallback(() => {
    setIsKebabDropdownOpen((isKebabDropdownOpen) => !isKebabDropdownOpen);
  }, []);

  const onFullKebabToggle = useCallback((isOpen: boolean) => {
    setIsFullKebabDropdownOpen(isOpen);
  }, []);

  const onFullKebabSelect = useCallback(() => {
    setIsFullKebabDropdownOpen((isFullKebabDropdownOpen) => !isFullKebabDropdownOpen);
  }, []);

  const kebabDropdownItems = [
    <DropdownItem key="kebab-1">
      <CogIcon /> Settings
    </DropdownItem>,
    <DropdownItem key="kebab-2">
      <HelpIcon /> Help
    </DropdownItem>,
  ];
  const userDropdownItems = [
    <DropdownGroup key="group 2">
      <DropdownItem key="group 2 profile">My profile</DropdownItem>
      <DropdownItem key="group 2 user" component="button">
        User management
      </DropdownItem>
      <DropdownItem key="group 2 logout">Logout</DropdownItem>
    </DropdownGroup>,
  ];

  const fullKebabItems = [
    <DropdownGroup key="group 2">
      <DropdownItem key="group 2 profile">My profile</DropdownItem>
      <DropdownItem key="group 2 user" component="button">
        User management
      </DropdownItem>
      <DropdownItem key="group 2 logout">Logout</DropdownItem>
    </DropdownGroup>,
    <Divider key="divider" />,
    <DropdownItem key="kebab-1">
      <CogIcon /> Settings
    </DropdownItem>,
    <DropdownItem key="kebab-2">
      <HelpIcon /> Help
    </DropdownItem>,
  ];

  const headerToolbar = (
    <Toolbar id="toolbar" isFullHeight isStatic>
      <ToolbarContent>
        <ToolbarGroup
          variant="icon-button-group"
          alignment={{ default: "alignRight" }}
          spacer={{ default: "spacerNone", md: "spacerMd" }}
        >
          <ToolbarItem>
            <Button aria-label="Notifications" variant={ButtonVariant.plain}>
              <AttentionBellIcon />
            </Button>
          </ToolbarItem>
          <ToolbarGroup variant="icon-button-group" visibility={{ default: "hidden", lg: "visible" }}>
            <ToolbarItem>
              <Button aria-label="Settings actions" variant={ButtonVariant.plain}>
                <CogIcon />
              </Button>
            </ToolbarItem>
            <ToolbarItem>
              <Button aria-label="Help actions" variant={ButtonVariant.plain}>
                <QuestionCircleIcon />
              </Button>
            </ToolbarItem>
          </ToolbarGroup>
        </ToolbarGroup>
        <ToolbarItem visibility={{ default: "hidden", md: "visible", lg: "hidden" }}>
          <Dropdown
            isPlain
            position="right"
            onSelect={onKebabDropdownSelect}
            toggle={<KebabToggle onToggle={onKebabDropdownToggle} />}
            isOpen={isKebabDropdownOpen}
            dropdownItems={kebabDropdownItems}
          />
        </ToolbarItem>
        <ToolbarItem visibility={{ default: "visible", md: "hidden", lg: "hidden", xl: "hidden", "2xl": "hidden" }}>
          <Dropdown
            isPlain
            position="right"
            onSelect={onFullKebabSelect}
            toggle={<KebabToggle onToggle={onFullKebabToggle} />}
            isOpen={isFullKebabDropdownOpen}
            dropdownItems={fullKebabItems}
          />
        </ToolbarItem>
        <ToolbarItem visibility={{ default: "hidden", md: "visible" }}>
          <Dropdown
            position="right"
            onSelect={onDropdownSelect}
            isOpen={isDropdownOpen}
            toggle={
              <DropdownToggle icon={<Avatar src={"bla"} alt="Avatar" />} onToggle={onDropdownToggle}>
                John Smith
              </DropdownToggle>
            }
            dropdownItems={userDropdownItems}
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );

  const masthead = (
    <Masthead>
      <MastheadToggle>
        <PageToggleButton variant="plain" aria-label="Global navigation">
          <BarsIcon />
        </PageToggleButton>
      </MastheadToggle>
      <MastheadMain>
        <MastheadBrand>
          <Brand src={"bla"} alt="Patternfly logo" />
        </MastheadBrand>
      </MastheadMain>
      <MastheadContent>{headerToolbar}</MastheadContent>
    </Masthead>
  );

  return masthead;
}
