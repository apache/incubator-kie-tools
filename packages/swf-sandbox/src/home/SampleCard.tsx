import * as React from "react";

import { Card, CardTitle, CardFooter, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

export function SampleCard() {
  return (
    <Card id="card-demo-horizontal-split-example" isFlat>
      <Grid md={6}>
        <GridItem
          style={{
            minHeight: "200px",
            backgroundPosition: "center",
            backgroundSize: "cover",
            backgroundImage: "url(/assets/images/pfbg_992@2x.jpg)",
          }}
        />
        <GridItem>
          <CardTitle>Headline</CardTitle>
          <CardBody>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse arcu purus, lobortis nec euismod eu,
            tristique ut sapien. Nullam turpis lectus, aliquet sit amet volutpat eu, semper eget quam. Maecenas in
            tempus diam. Aenean interdum velit sed massa aliquet, sit amet malesuada nulla hendrerit. Aenean non
            faucibus odio. Etiam non metus turpis. Praesent sollicitudin elit neque, id ullamcorper nibh faucibus eget.
          </CardBody>
          <CardFooter>
            <Button variant="tertiary">Call to action</Button>
          </CardFooter>
        </GridItem>
      </Grid>
    </Card>
  );
}
