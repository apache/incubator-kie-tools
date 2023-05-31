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

import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import {
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import {
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
} from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { basename } from "path";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { useApp } from "./AppContext";
import { useDmnFormI18n } from "./i18n";
import { routes } from "./Routes";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

interface Props {
  uri: string;
}

export function DmnFormToolbar(props: Props) {
  const app = useApp();
  const history = useHistory();
  const { i18n } = useDmnFormI18n();
  const [isLgKebabOpen, setLgKebabOpen] = useState(false);
  const [isSmKebabOpen, setSmKebabOpen] = useState(false);
  const [modelDropdownOpen, setModelDropdownOpen] = useState(false);

  const onOpenSwaggerUI = useCallback(() => {
    window.open(routes.swaggerUi.path({}), "_blank");
  }, []);

  const openForm = useCallback(
    (uri: string) => {
      history.push({
        pathname: routes.form.path({ filePath: uri.slice(1) }),
      });
    },
    [history]
  );

  const filename = useMemo(() => {
    const fullFilename = basename(props.uri);
    const maxSize = 25;
    const extension = fullFilename.substring(fullFilename.lastIndexOf(".") + 1);
    const name = fullFilename.replace(`.${extension}`, "");

    if (name.length < maxSize) {
      return fullFilename;
    }

    return `${name.substring(0, maxSize)}... .${extension}`;
  }, [props.uri]);

  const disclaimer = useMemo(() => {
    return (
      <Tooltip
        className="kogito--dmn-form__toolbar-tooltip"
        key="disclaimer-tooltip"
        maxWidth={"250px"}
        content={<I18nHtml>{i18n.formToolbar.disclaimer.description}</I18nHtml>}
      >
        <Text className={"kogito--dmn-form__toolbar-title"}>
          {i18n.formToolbar.disclaimer.title}
          &nbsp; &nbsp;
          <HelpIcon className="pf-u-ml-sm" />
        </Text>
      </Tooltip>
    );
  }, [i18n]);

  const modelDropdownItems = useMemo(() => {
    if (!app.data) {
      return [];
    }

    return app.data.forms
      .map((form) => form.uri)
      .filter((uri) => uri !== props.uri)
      .sort((a, b) => a.localeCompare(b))
      .map((uri, idx) => (
        <DropdownItem
          id={`dmn-form-toolbar-model-dropdown-item-${idx}`}
          key={`dmn-form-toolbar-model-dropdown-item-${idx}`}
          component="button"
          onClick={() => openForm(uri)}
        >
          {basename(uri)}
        </DropdownItem>
      ));
  }, [app.data, openForm, props.uri]);

  const dropdownItems = useCallback(
    (dropdownId: string) => [
      <React.Fragment key={`dropdown-${dropdownId}-close`}>
        <DropdownItem
          id="dmn-dev-deployment-form-toolbar-kebab-open-swagger-ui-button"
          key={`dropdown-${dropdownId}-swagger-ui`}
          component={"button"}
          onClick={onOpenSwaggerUI}
          aria-label={"Swagger UI"}
          data-testid={"open-swagger-ui-button"}
          ouiaId="open-swagger-ui-button"
        >
          <Text component={TextVariants.a} className="kogito--dmn-form__toolbar a">
            {i18n.names.swaggerUI}
            <ExternalLinkAltIcon className="pf-u-mx-sm" />
          </Text>
        </DropdownItem>
      </React.Fragment>,
    ],
    [onOpenSwaggerUI, i18n]
  );

  return (
    <PageHeader
      logo={
        <Flex alignItems={{ default: "alignItemsCenter" }}>
          <FlexItem style={{ display: "flex", alignItems: "center" }}>
            <Brand src={routes.static.images.appLogoReverse.path({})} alt={"Logo"} heights={{ default: "38px" }}>
              <source srcSet={routes.static.images.appLogoReverse.path({})} />
            </Brand>
          </FlexItem>
          <FlexItem style={{ display: "flex", alignItems: "center" }}>
            <TextContent>
              &nbsp;&nbsp;
              <Text component={TextVariants.small} style={{ display: "inline" }}>
                <i>Dev deployments</i>
              </Text>
            </TextContent>
          </FlexItem>
        </Flex>
      }
      headerTools={
        <PageHeaderTools>
          <PageHeaderToolsGroup>
            <PageHeaderToolsItem
              visibility={{
                default: "hidden",
                "2xl": "visible",
                xl: "visible",
                lg: "hidden",
                md: "hidden",
                sm: "hidden",
              }}
            >
              {app.data!.forms.length === 1 && (
                <Text data-testid={"text-filename"} className="kogito--dmn-form__toolbar-filename">
                  {filename}
                </Text>
              )}
              {app.data!.forms.length > 1 && (
                <Dropdown
                  onSelect={() => setModelDropdownOpen(false)}
                  toggle={
                    <DropdownToggle
                      id="dmn-dev-deployment-form-toolbar-model-dropdown-button"
                      onToggle={(isOpen) => setModelDropdownOpen(isOpen)}
                      data-testid="dmn-dev-deployment-form-toolbar-model-dropdown-button"
                    >
                      {filename}
                    </DropdownToggle>
                  }
                  isOpen={modelDropdownOpen}
                  position={DropdownPosition.right}
                  dropdownItems={modelDropdownItems}
                />
              )}
            </PageHeaderToolsItem>
          </PageHeaderToolsGroup>
          <PageHeaderToolsGroup>
            <PageHeaderToolsItem
              visibility={{
                default: "visible",
                "2xl": "hidden",
                xl: "hidden",
                lg: "visible",
                md: "visible",
                sm: "visible",
              }}
            >
              {disclaimer}
            </PageHeaderToolsItem>
            <PageHeaderToolsItem
              visibility={{
                default: "hidden",
                "2xl": "visible",
                xl: "visible",
                lg: "hidden",
                md: "hidden",
                sm: "hidden",
              }}
            >
              <Dropdown
                onSelect={() => setLgKebabOpen(false)}
                toggle={
                  <DropdownToggle
                    data-testid={"view-kebab"}
                    className={"kogito--dmn-form__toolbar-icon-button"}
                    id={"view-id-lg"}
                    toggleIndicator={null}
                    onToggle={(isOpen) => setLgKebabOpen(isOpen)}
                    ouiaId="toolbar-button"
                  >
                    <EllipsisVIcon />
                  </DropdownToggle>
                }
                isOpen={isLgKebabOpen}
                isPlain={true}
                dropdownItems={dropdownItems("lg")}
                position={DropdownPosition.right}
              />
            </PageHeaderToolsItem>
            <PageHeaderToolsItem
              visibility={{
                default: "visible",
                "2xl": "hidden",
                xl: "hidden",
                lg: "visible",
                md: "visible",
                sm: "visible",
              }}
            >
              <Dropdown
                onSelect={() => setSmKebabOpen(false)}
                toggle={
                  <DropdownToggle
                    data-testid={"kebab-sm"}
                    className={"kogito--dmn-form__toolbar-icon-button"}
                    id={"kebab-id-sm"}
                    toggleIndicator={null}
                    onToggle={(isOpen) => setSmKebabOpen(isOpen)}
                    ouiaId="small-toolbar-button"
                  >
                    <EllipsisVIcon />
                  </DropdownToggle>
                }
                isOpen={isSmKebabOpen}
                isPlain={true}
                dropdownItems={[...dropdownItems("sm")]}
                position={DropdownPosition.right}
              />
            </PageHeaderToolsItem>
          </PageHeaderToolsGroup>
        </PageHeaderTools>
      }
      topNav={disclaimer}
      className={"kogito--dmn-form__toolbar"}
      aria-label={"Page header"}
    />
  );
}
