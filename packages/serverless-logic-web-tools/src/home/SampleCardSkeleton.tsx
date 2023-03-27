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
import "./css/SampleCardSkeleton.css";

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
        className="card-skeleton-gallery"
      >
        {Array(4)
          .fill(undefined)
          .map((item, index) => (
            <Card isCompact={true} key={index} isFullHeight={true}>
              <Grid className="grid-style">
                <GridItem md={6} className="card-grid-item">
                  <div className="card-label-skeleton">
                    <Skeleton width="90%" />
                  </div>
                  <Skeleton shape="square" width="90%" screenreaderText="Loading svg" className="svg-skeleton" />
                </GridItem>
                <GridItem md={6} className="grid-content-item">
                  <CardTitle data-ouia-component-type="sample-title">
                    <Skeleton width="90%" />
                  </CardTitle>
                  <CardBody isFilled={true}>
                    <Skeleton shape="square" width="90%" screenreaderText="Loading description" />
                  </CardBody>
                  <CardFooter className="card-footer-style">
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
