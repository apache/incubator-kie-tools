import * as React from "react";

import { PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { Grid } from "@patternfly/react-core/dist/js/layouts/Grid";
import { SampleCard } from "./SampleCard";

export function Showcase() {
  return (
    <PageSection variant={PageSectionVariants.dark}>
      <Grid>
        <SampleCard name="greetings" />
        <SampleCard name="check-inbox-periodical" />
      </Grid>
    </PageSection>
  );
}
