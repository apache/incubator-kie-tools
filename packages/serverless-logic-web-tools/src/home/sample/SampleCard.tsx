import * as React from "react";
import { useMemo } from "react";
import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useRoutes } from "../../navigation/Hooks";
import { Link } from "react-router-dom";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { FolderIcon, FileIcon, MonitoringIcon } from "@patternfly/react-icons/dist/js/icons";
import { SampleCategory } from "./sampleApi";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

export enum SamplesCategory {
  SWF = "serverless-workflow",
  SD = "serverless-decision",
  DASH = "dashbuilder",
}

export type Sample = {
  name: string;
  sampleId: string;
  svg: string;
  description: string;
  category: SampleCategory;
};

const tagMap: Record<SamplesCategory, { label: string; icon: React.ComponentClass; color: LabelProps["color"] }> = {
  [SamplesCategory.SWF]: {
    label: "Serverless Workflow",
    icon: FileIcon,
    color: "orange",
  },
  [SamplesCategory.SD]: {
    label: "Serverless Decision",
    icon: FolderIcon,
    color: "blue",
  },
  [SamplesCategory.DASH]: {
    label: "Dashboard",
    icon: MonitoringIcon,
    color: "purple",
  },
};

export function RenderSvg(props: { svg: string }) {
  const modifiedContent = useMemo(() => {
    try {
      const parser = new DOMParser();
      const xml = parser.parseFromString(props.svg, "image/svg+xml");
      const svg = xml.getElementsByTagName("svg")[0];
      if (svg) {
        svg.setAttribute("width", "400px");
        svg.setAttribute("height", "300px");
      }
      const serializer = new XMLSerializer();
      return serializer.serializeToString(xml);
    } catch (e) {
      console.log("SVG render error", e);
    }
  }, [props.svg]);

  return (
    <div
      style={{ height: "100%", maxWidth: "100%", maxHeight: "400px", paddingTop: "30px" }}
      dangerouslySetInnerHTML={{ __html: modifiedContent! }}
    />
  );
}

export function SampleCard(props: { sample: Sample }) {
  const routes = useRoutes();

  const tag = useMemo(() => tagMap[props.sample.category], [props.sample.category]);

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
          <RenderSvg svg={props.sample.svg} />
        </GridItem>
        <GridItem md={6} style={{ display: "flex", flexDirection: "column" }}>
          <CardTitle data-ouia-component-type="sample-title">{props.sample.name}</CardTitle>
          <CardBody isFilled={true}>
            <Tooltip content={<div>{props.sample.description}</div>}>
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
                {props.sample.description}
              </Text>
            </Tooltip>
          </CardBody>
          <CardFooter style={{ alignItems: "baseline" }}>
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
          </CardFooter>
        </GridItem>
      </Grid>
    </Card>
  );
}
