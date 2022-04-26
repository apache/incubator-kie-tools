import * as React from "react";

import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { useRoutes } from "../navigation/Hooks";

export type SampleCardProps = {
  name: string;
};

export function SampleCard(props: SampleCardProps) {
  const routes = useRoutes();

  return (
    <GridItem md={4}>
      <Card isCompact={true}>
        <Grid md={6}>
          <GridItem>
            <div>
              <img src={routes.static.sample.path({ name: props.name, type: "svg" })} style={{ width: "100%" }} />
            </div>
          </GridItem>
          <GridItem>
            <CardTitle>Headline</CardTitle>
            <CardBody>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse arcu purus, lobortis nec euismod eu,
              tristique ut sapien.
            </CardBody>
            <CardFooter>
              <Button variant="tertiary">Call to action</Button>
            </CardFooter>
          </GridItem>
        </Grid>
      </Card>
    </GridItem>
  );
}
