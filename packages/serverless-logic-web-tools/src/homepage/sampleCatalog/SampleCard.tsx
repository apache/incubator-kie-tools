/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useMemo } from "react";
import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useRoutes } from "../../navigation/Hooks";
import { Link } from "react-router-dom";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { FileTypes } from "../../extension";
import { Sample, tagMap } from "./Types";

export function SampleCard(props: { sample: Sample }) {
  const routes = useRoutes();

  const tag = useMemo(() => tagMap[props.sample.type], [props.sample.type]);

  return (
    <Card isCompact={true} isFullHeight={true}>
      <Grid style={{ height: "100%" }}>
        <GridItem
          md={6}
          style={{ overflow: "hidden", textAlign: "center", verticalAlign: "middle", position: "relative" }}
        >
          <div style={{ position: "absolute", bottom: "16px", right: 0, left: 0, margin: "auto" }}>
            <Label color={tag.color}>
              <tag.icon />
              &nbsp;&nbsp;<b>{tag.label}</b>
            </Label>
          </div>
          <props.sample.svg style={{ height: "100%", maxWidth: "100%", maxHeight: "400px" }} />
        </GridItem>
        <GridItem md={6} style={{ display: "flex", flexDirection: "column" }}>
          <CardTitle data-ouia-component-type="sample-title">{props.sample.name}</CardTitle>
          <CardBody isFilled={true}>
            <Text component="p">{props.sample.description}</Text>
          </CardBody>
          <CardFooter style={{ alignItems: "baseline" }}>
            <Link
              to={{
                pathname: routes.importModel.path({}),
                search: routes.importModel.queryString({
                  url:
                    props.sample.repoUrl ??
                    `${window.location.origin}${window.location.pathname}${routes.static.sample.path({
                      type: props.sample.type || FileTypes.SW_JSON,
                      name: props.sample.fileName,
                    })}`,
                  renameWorkspace: props.sample.name,
                  ...(props.sample.repoUrl ? { removeRemote: "true" } : {}),
                }),
              }}
            >
              <Button variant={ButtonVariant.tertiary} ouiaId={props.sample.fileName + `-try-swf-sample-button`}>
                Try it out!
              </Button>
            </Link>
          </CardFooter>
        </GridItem>
      </Grid>
    </Card>
  );
}
