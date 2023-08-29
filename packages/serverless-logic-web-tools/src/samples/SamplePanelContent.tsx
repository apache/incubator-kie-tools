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
import React from "react";
import { Chip } from "@patternfly/react-core/dist/js/components/Chip";
import { ChipGroup } from "@patternfly/react-core/dist/js/components/ChipGroup";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExternalLinkAltIcon, LinkIcon, TagIcon, TopologyIcon, UserIcon } from "@patternfly/react-icons/dist/js";
import { useMemo } from "react";
import { ExpandableAuthor } from "./ExpandableAuthor";
import { SampleCategoryComponent } from "./SampleCategoryComponent";
import { Sample } from "./types";
import { Link } from "react-router-dom";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useRoutes } from "../navigation/Hooks";
import { When } from "react-if";

type SamplePanelContentProps = { sample: Sample; onPanelToggle?: () => void };

export function SamplesPanelContent(props: SamplePanelContentProps) {
  const { category, title, description, tags, dependencies, related_to, resources, authors } = props.sample.definition;
  const routes = useRoutes();

  const tagList = useMemo(
    () =>
      tags.map((tag, index) => (
        <Chip key={index} isReadOnly>
          {tag}
        </Chip>
      )),
    [tags]
  );
  const dependencyList = useMemo(() => dependencies.map(capitalizeString).join(", "), [dependencies]);
  const relatedToList = useMemo(() => related_to.map(capitalizeString).join(", "), [related_to]);

  const resourceList = useMemo(
    () =>
      resources.map((resource, index) => (
        <span key={index}>
          <a style={{ textTransform: "capitalize" }} href={resource} target="_blank" rel="noopener noreferrer">
            {resource}
          </a>
          {index < resources.length - 1 ? ", " : ""}
        </span>
      )),
    [resources]
  );

  return (
    <DrawerPanelContent isResizable minSize="400px" defaultSize="567px">
      <DrawerHead
        style={{ borderBottom: "var(--pf-global--BorderWidth--sm) solid var(--pf-global--BorderColor--100)" }}
      >
        <TextContent>
          <Text component={TextVariants.h2}>{title}</Text>
        </TextContent>
        <DrawerActions>
          <DrawerCloseButton onClick={props.onPanelToggle} />
        </DrawerActions>
        <br />
      </DrawerHead>

      <DrawerPanelBody>
        <TextContent>{description}</TextContent>
        <br />
        <DescriptionList>
          <DescriptionListGroup>
            <DescriptionListTerm icon={<TopologyIcon />}>Category</DescriptionListTerm>
            <DescriptionListDescription>
              <SampleCategoryComponent category={category} />
            </DescriptionListDescription>
          </DescriptionListGroup>
          <When condition={dependencies.length > 0}>
            <DescriptionListGroup>
              <DescriptionListTerm icon={<LinkIcon />}>Dependencies</DescriptionListTerm>
              <DescriptionListDescription>{dependencyList}</DescriptionListDescription>
            </DescriptionListGroup>
          </When>
          <When condition={related_to.length > 0}>
            <DescriptionListGroup>
              <DescriptionListTerm icon={<LinkIcon />}>Related To</DescriptionListTerm>
              <DescriptionListDescription>{relatedToList}</DescriptionListDescription>
            </DescriptionListGroup>
          </When>
          <When condition={resources.length > 0}>
            <DescriptionListGroup>
              <DescriptionListTerm icon={<ExternalLinkAltIcon />}>Resources</DescriptionListTerm>
              <DescriptionListDescription>{resourceList}</DescriptionListDescription>
            </DescriptionListGroup>
          </When>
          <When condition={tags.length > 0}>
            <DescriptionListGroup>
              <DescriptionListTerm icon={<TagIcon />}>Tags</DescriptionListTerm>
              <DescriptionListDescription>
                <ChipGroup numChips={10}>{tagList}</ChipGroup>
              </DescriptionListDescription>
            </DescriptionListGroup>
          </When>
          <When condition={authors.length > 0}>
            <DescriptionListGroup>
              <DescriptionListTerm icon={<UserIcon />}>Authors</DescriptionListTerm>
              <DescriptionListDescription>
                {authors.map((author, index) => (
                  <ExpandableAuthor key={index} author={author} />
                ))}
              </DescriptionListDescription>
            </DescriptionListGroup>
          </When>
        </DescriptionList>
        <br />
        <TextContent>
          <Link
            to={{
              pathname: routes.sampleShowcase.path({}),
              search: routes.sampleShowcase.queryString({ sampleId: props.sample.sampleId }),
            }}
          >
            <Button variant={ButtonVariant.tertiary} ouiaId={props.sample.sampleId + `-try-swf-sample-button`}>
              Try it out!
            </Button>
          </Link>
        </TextContent>
      </DrawerPanelBody>
    </DrawerPanelContent>
  );
}

const capitalizeString = (value: string) => value.charAt(0).toUpperCase() + value.slice(1);
