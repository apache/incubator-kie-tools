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

import React from "react";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";

export const SampleCardSkeleton = () => {
  return (
    <>
      <TextContent>
        <Text component="h1">Samples Showcase</Text>
      </TextContent>
      <br />
      <Gallery
        hasGutter={true}
        minWidths={{ sm: "calc(100%/3.1 - 16px)", default: "100%" }}
        style={{
          overflowX: "auto",
          gridAutoFlow: "column",
          gridAutoColumns: "minmax(calc(100%/3.1 - 16px),1fr)",
          paddingBottom: "8px",
          paddingRight: "var(--pf-c-page__main-section--xl--PaddingRight)",
        }}
      >
        {Array(4)
          .fill(undefined)
          .map((item, index) => (
            <Card isCompact={true} key={index} isFullHeight={true}>
              <Grid style={{ height: "100%" }}>
                <GridItem
                  md={6}
                  style={{ overflow: "hidden", textAlign: "center", verticalAlign: "middle", position: "relative" }}
                >
                  <div style={{ position: "absolute", bottom: "16px", right: 0, left: 0, margin: "auto" }}>
                    <Skeleton width="90%" style={{ marginLeft: "20px" }} />
                  </div>
                  <Skeleton shape="square" width="90%" screenreaderText="Loading svg" style={{ margin: "20px" }} />
                </GridItem>
                <GridItem md={6} style={{ display: "flex", flexDirection: "column" }}>
                  <CardTitle data-ouia-component-type="sample-title">
                    <Skeleton width="90%" />
                  </CardTitle>
                  <CardBody isFilled={true}>
                    <Skeleton shape="square" width="90%" screenreaderText="Loading description" />
                  </CardBody>
                  <CardFooter style={{ alignItems: "baseline" }}>
                    <Skeleton width="90%" />
                  </CardFooter>
                </GridItem>
              </Grid>
            </Card>
          ))}
      </Gallery>
    </>
  );
};
