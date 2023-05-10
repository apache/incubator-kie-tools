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

import { Card, CardBody, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { FileIcon, FolderIcon, MonitoringIcon } from "@patternfly/react-icons/dist/js/icons";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
import { useHistory } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { Sample, SampleCategory } from "./sampleApi";

const tagMap: Record<SampleCategory, { label: string; icon: React.ComponentClass; color: LabelProps["color"] }> = {
  ["serverless-workflow"]: {
    label: "Serverless Workflow",
    icon: FileIcon,
    color: "orange",
  },
  ["serverless-decision"]: {
    label: "Serverless Decision",
    icon: FolderIcon,
    color: "blue",
  },
  ["dashbuilder"]: {
    label: "Dashboard",
    icon: MonitoringIcon,
    color: "purple",
  },
};

export function SampleCard(props: { sample: Sample; cover: string | undefined }) {
  const routes = useRoutes();
  const imgRef = useRef<HTMLImageElement>(null);
  const tag = useMemo(() => tagMap[props.sample.definition.category], [props.sample.definition.category]);
  const history = useHistory();

  const onCardClick = useCallback(() => {
    history.push({
      pathname: routes.sampleShowcase.path({}),
      search: routes.sampleShowcase.queryString({ sampleId: props.sample.sampleId }),
    });
  }, [props.sample, history, routes]);

  useEffect(() => {
    const blob = new Blob([props.cover || ""], { type: "image/svg+xml" });
    const url = URL.createObjectURL(blob);
    imgRef.current!.addEventListener("load", () => URL.revokeObjectURL(url), { once: true });
    imgRef.current!.src = url;
  }, [props.cover]);

  return (
    <Card isCompact={true} isFullHeight={true} onClick={onCardClick} isSelectable>
      <Grid style={{ height: "100%" }}>
        <GridItem
          lg={6}
          style={{ overflow: "hidden", textAlign: "center", verticalAlign: "middle", position: "relative" }}
        >
          <div style={{ position: "absolute", bottom: "16px", right: 0, left: 0, margin: "auto" }}>
            <Label color={tag.color}>
              <tag.icon />
              &nbsp;&nbsp;<b>{tag.label}</b>
            </Label>
          </div>
          <Bullseye style={{ padding: "0px 8px 30px 8px" }}>
            <img
              style={{ height: "370px", maxWidth: "100%" }}
              ref={imgRef}
              alt={`SVG for sample ${props.sample.sampleId}`}
            />
          </Bullseye>
        </GridItem>
        <GridItem lg={6} style={{ display: "flex", flexDirection: "column" }}>
          <CardTitle data-ouia-component-type="sample-title">{props.sample.definition.title}</CardTitle>
          <CardBody isFilled={true}>
            <Tooltip content={<div>{props.sample.definition.description}</div>}>
              <Text
                component="p"
                style={{
                  display: "-webkit-box",
                  WebkitBoxOrient: "vertical",
                  WebkitLineClamp: 5,
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  whiteSpace: "pre-wrap",
                }}
              >
                {props.sample.definition.description}
              </Text>
            </Tooltip>
          </CardBody>
        </GridItem>
      </Grid>
    </Card>
  );
}
