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
import { useCallback, useMemo, useState } from "react";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from "@patternfly/react-core/deprecated";
import {
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
} from "@patternfly/react-core/deprecated";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { useNavigate } from "react-router-dom";
import { useApp } from "./AppContext";
import { useDmnFormI18n } from "./i18n";
import { routes } from "./Routes";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

interface Props {
  modelName?: string;
}

function truncateText(text: string) {
  if (text.length >= 35) {
    return `${text.substring(0, 32)}...`;
  }
  return text;
}

export function DmnFormToolbar(props: Props) {
  const app = useApp();
  const navigate = useNavigate();
  const { i18n } = useDmnFormI18n();
  const [isLgKebabOpen, setLgKebabOpen] = useState(false);
  const [isSmKebabOpen, setSmKebabOpen] = useState(false);
  const [modelDropdownOpen, setModelDropdownOpen] = useState(false);

  const onOpenSwaggerUI = useCallback(() => {
    window.open(routes.quarkusApp.swaggerUi.path({}, app.quarkusAppOrigin, app.quarkusAppPath), "_blank");
  }, [app.quarkusAppOrigin, app.quarkusAppPath]);

  const openForm = useCallback(
    (modelName: string) => {
      navigate({
        pathname: routes.form.path({ modelName }),
      });
    },
    [navigate]
  );

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
          <HelpIcon className="pf-v5-u-ml-sm" />
        </Text>
      </Tooltip>
    );
  }, [i18n]);

  const modelDropdownItems = useMemo(() => {
    if (!app.data) {
      return [];
    }

    return app.data.forms
      .map((form) => form.modelName)
      .filter((modelName) => modelName !== props.modelName)
      .sort((a, b) => a.localeCompare(b))
      .map((modelName, idx) => (
        <DropdownItem
          id={`dmn-form-toolbar-model-dropdown-item-${idx}`}
          key={`dmn-form-toolbar-model-dropdown-item-${idx}`}
          component="button"
          onClick={() => openForm(modelName)}
        >
          {truncateText(modelName)}
        </DropdownItem>
      ));
  }, [app.data, openForm, props.modelName]);

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
            &nbsp;
            <ExternalLinkAltIcon className="pf-v5-u-mx-sm" />
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
                <i>Dev Deployments</i>
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
                <Text data-testid={"text-model-name"} className="kogito--dmn-form__toolbar-model-name">
                  {props.modelName && truncateText(props.modelName)}
                </Text>
              )}
              {app.data!.forms.length > 1 && (
                <Dropdown
                  onSelect={() => setModelDropdownOpen(false)}
                  isPlain={true}
                  toggle={
                    <DropdownToggle
                      id="dmn-dev-deployment-form-toolbar-model-dropdown-button"
                      onToggle={(_event, isOpen) => setModelDropdownOpen(isOpen)}
                      data-testid="dmn-dev-deployment-form-toolbar-model-dropdown-button"
                    >
                      {props.modelName && truncateText(props.modelName)}
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
                    onToggle={(_event, isOpen) => setLgKebabOpen(isOpen)}
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
                    onToggle={(_event, isOpen) => setSmKebabOpen(isOpen)}
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
