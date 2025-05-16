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

import * as React from "react";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { extractExtension } from "@kie-tools-core/workspaces-git-fs/dist/relativePath/WorkspaceFileRelativePathParser";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { PageHeaderToolsItem } from "@patternfly/react-core/deprecated";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { basename } from "path";
import { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppI18n } from "../i18n";
import { routes } from "../routes";
import { Dashboard } from "../data";
import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from "@patternfly/react-core/deprecated";
import { useApp } from "../context/AppContext";

interface Props {
  dashboard: Dashboard;
  showDisclaimer: boolean;
}

export function AppToolbar(props: Props) {
  const app = useApp();
  const navigate = useNavigate();
  const { i18n } = useAppI18n();
  const [modelDropdownOpen, setModelDropdownOpen] = useState(false);

  const openDashboard = useCallback(
    (uri: string) => {
      navigate({
        pathname: routes.dashboard.path({ filePath: uri }),
      });
    },
    [navigate]
  );

  const modelDropdownItems = useMemo(() => {
    return app.dashboards
      .map((db) => db.uri)
      .filter((uri) => uri !== props.dashboard.uri)
      .sort((a, b) => a.localeCompare(b))
      .map((uri, idx) => (
        <DropdownItem
          key={`dashboard-toolbar-dropdown-item-${idx}`}
          component="button"
          onClick={() => openDashboard(uri)}
        >
          {basename(uri)}
        </DropdownItem>
      ));
  }, [app.dashboards, openDashboard, props.dashboard.uri]);

  const filename = useMemo(() => {
    const fullFilename = basename(props.dashboard.uri);
    const maxSize = 35;
    const extension = extractExtension(fullFilename);
    const name = fullFilename.replace(`.${extension}`, "");

    if (fullFilename.length < maxSize) {
      return fullFilename;
    }

    return `${name.substring(0, maxSize - extension.length)}...${extension}`;
  }, [props.dashboard.uri]);

  return (
    <Masthead aria-label={"Page header"} display={{ default: "stack" }} className="app--masthead">
      <MastheadMain style={{ justifyContent: "space-between" }}>
        <PageHeaderToolsItem className={"pf-v5-l-flex"}>
          <MastheadBrand
            component="a"
            onClick={() => navigate({ pathname: routes.root.path({}) })}
            style={{ textDecoration: "none" }}
          >
            <TextContent>
              <Text component={TextVariants.h1}>Dashbuilder Deployment</Text>
            </TextContent>
          </MastheadBrand>
        </PageHeaderToolsItem>
        {props.showDisclaimer && (
          <PageHeaderToolsItem>
            <Tooltip
              className="app--masterhead__disclaimer"
              position="bottom-end"
              key="disclaimer-tooltip"
              content={<I18nHtml>{i18n.masthead.disclaimer.description}</I18nHtml>}
            >
              <TextContent>
                <Text component={TextVariants.h5}>
                  {i18n.masthead.disclaimer.title}
                  <HelpIcon className="app--masterhead__disclaimer-icon" />
                </Text>
              </TextContent>
            </Tooltip>
          </PageHeaderToolsItem>
        )}
        <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
          <FlexItem>
            <PageHeaderToolsItem>
              {app.dashboards.length > 1 ? (
                <Dropdown
                  onSelect={() => setModelDropdownOpen(false)}
                  toggle={
                    <DropdownToggle
                      className="app--toolbar-dropdown-hoverable-dark"
                      onToggle={(_event, isOpen) => setModelDropdownOpen(isOpen)}
                    >
                      {filename}
                    </DropdownToggle>
                  }
                  isOpen={modelDropdownOpen}
                  position={DropdownPosition.right}
                  dropdownItems={modelDropdownItems}
                />
              ) : (
                <TextContent>
                  <Text component={TextVariants.h5}>{filename}</Text>
                </TextContent>
              )}
            </PageHeaderToolsItem>
          </FlexItem>
        </Flex>
      </MastheadMain>
    </Masthead>
  );
}
