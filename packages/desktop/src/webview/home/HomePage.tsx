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
import { Brand, Nav, NavItem, NavList, Page, PageHeader, PageSidebar } from "@patternfly/react-core";
import { useCallback, useState } from "react";
import { FilesPage } from "./FilesPage";
import { LearnMorePage } from "./LearnMorePage";
import { File } from "../../common/File";

interface Props {
  openFile: (file: File) => void;
  openFileByPath: (filePath: string) => void;
}

enum NavItems {
  FILES,
  LEARN_MORE
}

export function HomePage(props: Props) {
  const [activeNavItem, setActiveNavItem] = useState<NavItems>(NavItems.FILES);

  const onNavSelect = useCallback(selectedItem => {
    setActiveNavItem(selectedItem.itemId);
  }, []);

  const header = <PageHeader logo={<Brand src={"images/BusinessModeler_Logo.svg"} alt="Business Modeler Logo" />} />;

  const navigation = (
    <Nav onSelect={onNavSelect} className={"pf-m-dark"}>
      <NavList>
        <NavItem itemId={NavItems.FILES} isActive={activeNavItem === NavItems.FILES}>
          Files
        </NavItem>
        <NavItem itemId={NavItems.LEARN_MORE} isActive={activeNavItem === NavItems.LEARN_MORE}>
          Learn more
        </NavItem>
      </NavList>
    </Nav>
  );

  const sidebar = <PageSidebar nav={navigation} isNavOpen={true} className={"pf-m-dark"} />;

  return (
    <Page header={header} sidebar={sidebar} className={"kogito--editor-landing"}>
      {activeNavItem === NavItems.FILES && (
        <FilesPage openFile={props.openFile} openFileByPath={props.openFileByPath} />
      )}
      {activeNavItem === NavItems.LEARN_MORE && <LearnMorePage />}
    </Page>
  );
}
