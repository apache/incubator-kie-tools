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

import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { SearchPlusIcon } from "@patternfly/react-icons/dist/js/icons";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useRoutes } from "../navigation/Hooks";
import { SampleCategoryComponent } from "./SampleCategoryComponent";
import { Sample } from "./types";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";

function SampleSvgImg(props: {
  sample: Sample;
  svgBlob: string | undefined;
  height?: string;
  width?: string;
  maxWidth?: string;
  maxHeight?: string;
}) {
  const [svgUrl, setSvgUrl] = useState("");
  const { height, width, maxWidth = "100%", maxHeight } = props;

  useEffect(() => {
    const blob = new Blob([props.svgBlob || ""], { type: "image/svg+xml" });
    const url = URL.createObjectURL(blob);
    setSvgUrl(url);
    return () => {
      URL.revokeObjectURL(url);
    };
  }, [props.svgBlob]);

  if (!svgUrl || !props.svgBlob) {
    return <Skeleton height={height} width="100%" style={{ maxHeight: "80%" }} />;
  }

  return (
    <img style={{ height, width, maxWidth, maxHeight }} src={svgUrl} alt={`SVG for sample ${props.sample.sampleId}`} />
  );
}

type SampleCardProps = { sample: Sample; cover?: string; onClick?: (sample: Sample) => void };

export function SampleCard(props: SampleCardProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const routes = useRoutes();

  const handleModalToggle = useCallback((e?) => {
    e?.stopPropagation();
    setIsModalOpen((prevState) => !prevState);
  }, []);

  return (
    <>
      <Card
        isCompact={true}
        isFullHeight={true}
        onClick={() => {
          props.onClick?.(props.sample);
        }}
        isSelectable
      >
        <Grid style={{ height: "100%" }}>
          <GridItem
            lg={6}
            style={{ overflow: "hidden", textAlign: "center", verticalAlign: "middle", position: "relative" }}
          >
            <div style={{ position: "absolute", bottom: "16px", right: 0, left: 0, margin: "auto" }}>
              {props.cover && (
                <Button
                  type="button"
                  onClick={handleModalToggle}
                  isLarge
                  variant="plain"
                  style={
                    {
                      "--pf-c-button--PaddingLeft": "0",
                      "--pf-c-button--PaddingRight": "0",
                      marginTop: "-16px",
                      marginLeft: "80%",
                    } as React.CSSProperties
                  }
                >
                  <SearchPlusIcon size="sm" />
                </Button>
              )}
              <SampleCategoryComponent category={props.sample.definition.category} />
            </div>
            <Bullseye style={{ padding: "0px 8px 30px 8px" }}>
              <SampleSvgImg sample={props.sample} svgBlob={props.cover} height="370px" />
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
            <CardFooter>
              <Link
                to={{
                  pathname: routes.sampleShowcase.path({}),
                  search: routes.sampleShowcase.queryString({ sampleId: props.sample.sampleId }),
                }}
              >
                <Button variant={ButtonVariant.tertiary} ouiaId={props.sample.sampleId + `-try-sample-button`}>
                  Try it out!
                </Button>
              </Link>
            </CardFooter>
          </GridItem>
        </Grid>
      </Card>
      <Modal
        title={props.sample.definition.title}
        variant={ModalVariant.large}
        isOpen={isModalOpen}
        onClose={handleModalToggle}
        style={{ textAlign: "center" }}
      >
        <SampleSvgImg sample={props.sample} svgBlob={props.cover} height="670px" />
      </Modal>
    </>
  );
}
