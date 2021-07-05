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

import { I18nHtml } from "@kogito-tooling/i18n/dist/react-components";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
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
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useDmnFormI18n } from "./i18n";

interface Props {
  filename: string;
  onOpenSwaggerUI: () => void;
  onOpenOnlineEditor: () => void;
}

export function DMNFormToolbar(props: Props) {
  const { i18n } = useDmnFormI18n();
  const [isLgKebabOpen, setLgKebabOpen] = useState(false);
  const [isSmKebabOpen, setSmKebabOpen] = useState(false);

  const filename = useMemo(() => {
    const maxSize = 25;
    const extension = props.filename.substring(props.filename.lastIndexOf(".") + 1);
    const name = props.filename.replace(`.${extension}`, "");

    if (name.length < maxSize) {
      return props.filename;
    }

    return `${name.substring(0, maxSize)}... .${extension}`;
  }, [props.filename]);

  const disclaimer = useMemo(() => {
    return (
      <Text className={"kogito--dmn-form__toolbar-title"}>
        {i18n.formToolbar.disclaimer.title}
        <Tooltip
          className="kogito--dmn-form__deploy-dropdown-tooltip"
          key="disclaimer-tooltip"
          distance={20}
          content={<I18nHtml>{i18n.formToolbar.disclaimer.description}</I18nHtml>}
        >
          <HelpIcon className="pf-u-ml-sm" />
        </Tooltip>
      </Text>
    );
  }, [i18n.formToolbar.disclaimer.description, i18n.formToolbar.disclaimer.title]);

  const viewItems = useCallback(
    (dropdownId: string) => [
      <React.Fragment key={`dropdown-${dropdownId}-close`}>
        <DropdownItem
          key={`dropdown-${dropdownId}-save`}
          component={"button"}
          onClick={props.onOpenOnlineEditor}
          className={"pf-u-display-none-on-xl"}
          data-testid={"open-online-editor-button"}
          ouiaId="open-online-editor-dropdown-button"
          description={filename}
        >
          <Text component={TextVariants.a} className="kogito--dmn-form__toolbar a">
            {i18n.terms.open}
            <ExternalLinkAltIcon className="pf-u-mx-sm" />
          </Text>
        </DropdownItem>
        <DropdownItem
          key={`dropdown-${dropdownId}-swagger-ui`}
          component={"button"}
          onClick={props.onOpenSwaggerUI}
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
    [props.onOpenOnlineEditor, props.onOpenSwaggerUI, filename, i18n.terms.open, i18n.names.swaggerUI]
  );

  return (
    <PageHeader
      logo={
        <Brand src="images/dmn_kogito_logo.svg" alt={`dmn kogito logo`} className="kogito--dmn-form__toolbar-logo" />
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
              <Text className="kogito--dmn-form__toolbar-filename">{filename}</Text>
            </PageHeaderToolsItem>
          </PageHeaderToolsGroup>
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
              <Button
                variant="tertiary"
                data-testid="open-online-editor"
                aria-label={"Open Online Editor"}
                ouiaId="open-online-editor"
                icon={<ExternalLinkAltIcon />}
                iconPosition="right"
                onClick={props.onOpenOnlineEditor}
              >
                {i18n.terms.open}
              </Button>
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
                dropdownItems={viewItems("lg")}
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
                dropdownItems={[...viewItems("sm")]}
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
