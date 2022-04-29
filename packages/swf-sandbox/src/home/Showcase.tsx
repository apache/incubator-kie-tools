import * as React from "react";
import { useState, useCallback } from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { Grid } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Sample, SampleCard } from "./SampleCard";
import { ReactComponent as CheckInboxPeriodicalSvg } from "../../static/samples/check-inbox-periodical/check-inbox-periodical.svg";
import { ReactComponent as GreetingsSvg } from "../../static/samples/greetings/greetings.svg";
import { ReactComponent as FillGlassOfWaterSvg } from "../../static/samples/fill-glass-of-water/fill-glass-of-water.svg";
import { Accordion, AccordionItem, AccordionContent, AccordionToggle } from "@patternfly/react-core";
import ArrowRightIcon from "@patternfly/react-icons/dist/esm/icons/arrow-right-icon";

export const samples: Array<Sample> = [
  {
    name: "Greetings",
    fileName: "greetings",
    svg: GreetingsSvg,
    description: `This example shows a single Operation State with one action that calls the "greeting" function. The workflow data input is assumed to be the name of the person to greet. The results of the action is assumed to be the greeting for the provided persons name, which is added to the states data and becomes the workflow data output.`,
  },
  {
    name: "Check Inbox Periodical",
    fileName: "check-inbox-periodical",
    svg: CheckInboxPeriodicalSvg,
    description:
      "In this example we show the use of scheduled cron-based start event property. The example workflow checks the users inbox every 15 minutes and send them a text message when there are important emails.",
  },
  {
    name: "Fill glass of water",
    fileName: "fill-glass-of-water",
    svg: FillGlassOfWaterSvg,
    description: `Our workflow simulates filling up a glass of water one "count" at a time until "max" count is reached which represents our glass is full. Each time we increment the current count, the workflow checks if we need to keep refilling the glass. If the current count reaches the max count, the workflow execution ends. To increment the current count, the workflow invokes the "IncrementCurrent" expression function. Its results are then merged back into the state data according to the "toStateData" property of the event data filter.`,
  },
];

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
            {samples.map((sample) => (
              <SampleCard sample={sample} key={sample.fileName} />
            ))}
          </Grid>
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
}
