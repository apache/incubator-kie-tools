import * as React from "react";
import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useRoutes } from "../navigation/Hooks";
import { Link } from "react-router-dom";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";

export type Sample = {
  name: string;
  fileName: string;
  svg: React.FunctionComponent<React.SVGProps<SVGSVGElement>>;
  description: string;
};

export function SampleCard({ sample }: { sample: Sample }) {
  const routes = useRoutes();

  return (
    <GridItem md={4}>
      <Card isCompact={true} isFullHeight={true}>
        <Grid style={{ height: "100%" }}>
          <GridItem md={6} style={{ overflow: "hidden", textAlign: "center", verticalAlign: "middle" }}>
            <sample.svg style={{ height: "100%", maxWidth: "100%", maxHeight: "400px" }} />
          </GridItem>
          <GridItem md={6} style={{ display: "flex", flexDirection: "column" }}>
            <CardTitle>{sample.name}</CardTitle>
            <CardBody isFilled={true}>
              <Text component="p">{sample.description}</Text>
            </CardBody>
            <CardFooter style={{ alignItems: "baseline" }}>
              <Link
                to={{
                  pathname: routes.importModel.path({}),
                  search: routes.importModel.queryString({
                    url: `${window.location.origin}${window.location.pathname}${routes.static.sample.path({
                      type: "sw.json",
                      name: sample.fileName,
                    })}`,
                  }),
                }}
              >
                <Button variant={ButtonVariant.tertiary} ouiaId={`try-swf-sample-button`}>
                  Try it out!
                </Button>
              </Link>
            </CardFooter>
          </GridItem>
        </Grid>
      </Card>
    </GridItem>
  );
}
