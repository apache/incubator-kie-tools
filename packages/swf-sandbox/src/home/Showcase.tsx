import * as React from "react";
import { useState, useCallback } from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { Grid } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Sample, SampleCard } from "./SampleCard";
import { ReactComponent as CheckInboxPeriodicalSvg } from "../../static/samples/check-inbox-periodical/check-inbox-periodical.svg";
import { ReactComponent as GreetingsSvg } from "../../static/samples/greetings/greetings.svg";
import { Accordion, AccordionItem, AccordionContent, AccordionToggle } from "@patternfly/react-core";
import ArrowRightIcon from "@patternfly/react-icons/dist/esm/icons/arrow-right-icon";

export const samples: Array<Sample> = [
  {
    name: "Greetings",
    fileName: "greetings",
    svg: GreetingsSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    name: "Check Inbox Periodical",
    fileName: "check-inbox-periodical",
    svg: CheckInboxPeriodicalSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    name: "Greetings",
    fileName: "greetings",
    svg: GreetingsSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    name: "Check Inbox Periodical",
    fileName: "check-inbox-periodical",
    svg: CheckInboxPeriodicalSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    name: "Greetings",
    fileName: "greetings",
    svg: GreetingsSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    name: "Check Inbox Periodical",
    fileName: "check-inbox-periodical",
    svg: CheckInboxPeriodicalSvg,
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
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
