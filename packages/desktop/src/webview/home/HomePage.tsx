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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Page, PageHeader, PageSidebar } from "@patternfly/react-core/dist/js/components/Page";
import { FilesPage } from "./FilesPage";
import { LearnMorePage } from "./LearnMorePage";
import { ElectronFile } from "../../common/ElectronFile";
import { useDesktopI18n } from "../common/i18n";

interface Props {
  openFile: (file: ElectronFile) => void;
  openFileByPath: (filePath: string) => void;
}

enum NavItems {
  FILES,
  LEARN_MORE,
}

enum NavState {
  MANUAL_OPEN,
  MANUAL_CLOSE,
  RESIZED_OPEN,
  RESIZED_CLOSE,
}

export function HomePage(props: Props) {
  const [activeNavItem, setActiveNavItem] = useState(NavItems.FILES);
  const [navState, setNavState] = useState(NavState.RESIZED_OPEN);
  const { i18n } = useDesktopI18n();

  const onNavSelect = useCallback((selectedItem) => {
    setActiveNavItem(selectedItem.itemId);
  }, []);

  const onPageResize = useCallback(
    ({ mobileView }) => {
      if (mobileView && navState !== NavState.MANUAL_OPEN && navState !== NavState.MANUAL_CLOSE) {
        setNavState(NavState.RESIZED_CLOSE);
      } else if (!mobileView && navState !== NavState.MANUAL_CLOSE && navState !== NavState.MANUAL_OPEN) {
        setNavState(NavState.RESIZED_OPEN);
      }
    },
    [navState]
  );

  const onNavToggle = useCallback(() => {
    switch (navState) {
      case NavState.RESIZED_CLOSE:
        setNavState(NavState.MANUAL_OPEN);
        break;
      case NavState.RESIZED_OPEN:
        setNavState(NavState.MANUAL_CLOSE);
        break;
      case NavState.MANUAL_CLOSE:
        setNavState(NavState.RESIZED_OPEN);
        break;
      case NavState.MANUAL_OPEN:
        setNavState(NavState.RESIZED_CLOSE);
        break;
    }
  }, [navState]);

  const isNavOpen = useMemo(() => {
    return navState === NavState.RESIZED_OPEN || navState === NavState.MANUAL_OPEN;
  }, [navState]);

  const header = (
    <PageHeader
      showNavToggle={true}
      onNavToggle={onNavToggle}
      logo={<Brand src={"images/BusinessModeler_Logo.svg"} alt="Business Modeler Logo" />}
    />
  );

  const navigation = (
    <Nav onSelect={onNavSelect} theme={"dark"} ouiaId="nav-buttons">
      <NavList>
        <NavItem itemId={NavItems.FILES} isActive={activeNavItem === NavItems.FILES} ouiaId="files-nav">
          {i18n.terms.files}
        </NavItem>
        <NavItem itemId={NavItems.LEARN_MORE} isActive={activeNavItem === NavItems.LEARN_MORE} ouiaId="learn-more-nav">
          {i18n.homePage.learnMore}
        </NavItem>
      </NavList>
    </Nav>
  );

  const sidebar = <PageSidebar nav={navigation} isNavOpen={isNavOpen} theme={"dark"} />;

  return (
    <Page header={header} sidebar={sidebar} className={"kogito--editor-landing"} onPageResize={onPageResize}>
      {activeNavItem === NavItems.FILES && (
        <FilesPage openFile={props.openFile} openFileByPath={props.openFileByPath} />
      )}
      {activeNavItem === NavItems.LEARN_MORE && <LearnMorePage />}
    </Page>
  );
}
