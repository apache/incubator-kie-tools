import * as React from "react";
import { useState, useCallback } from "react";
import { PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { Grid } from "@patternfly/react-core/dist/js/layouts/Grid";
import { SampleCard } from "./SampleCard";
import { ReactComponent as CheckInboxPeriodicalSvg } from "../../static/samples/check-inbox-periodical/check-inbox-periodical.svg";
import { ReactComponent as GreetingsSvg } from "../../static/samples/greetings/greetings.svg";
import { Accordion, AccordionItem, AccordionContent, AccordionToggle } from "@patternfly/react-core";
import ArrowRightIcon from "@patternfly/react-icons/dist/esm/icons/arrow-right-icon";

export function Showcase() {
  const [expanded, setExpanded] = useState(false);
  const onToggle = useCallback(() => {
    setExpanded((expanded) => !expanded);
  }, []);

  return (
    <Accordion
      displaySize="large"
      style={{ backgroundColor: "var(--pf-c-page__main-section--BackgroundColor)" }}
      isBordered={true}
    >
      <AccordionItem>
        <AccordionToggle
          onClick={() => {
            onToggle();
          }}
          isExpanded={expanded}
          id="showcase-accordion"
        >
          <TextContent>
            <Text component="h1">Serverless Workflow Samples</Text>
            {ArrowRightIcon}
          </TextContent>
        </AccordionToggle>
        <AccordionContent isHidden={!expanded}>
          <Grid hasGutter={true}>
            <SampleCard name="greetings" svg={GreetingsSvg} />
            <SampleCard name="check-inbox-periodical" svg={CheckInboxPeriodicalSvg} />
            <SampleCard name="greetings" svg={GreetingsSvg} />
            <SampleCard name="check-inbox-periodical" svg={CheckInboxPeriodicalSvg} />
            <SampleCard name="greetings" svg={GreetingsSvg} />
            <SampleCard name="check-inbox-periodical" svg={CheckInboxPeriodicalSvg} />
          </Grid>
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
}
