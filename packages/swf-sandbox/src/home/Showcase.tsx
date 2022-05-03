import * as React from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { Sample, SampleCard } from "./SampleCard";
import { ReactComponent as CheckInboxPeriodicalSvg } from "../../static/samples/check-inbox-periodical/check-inbox-periodical.svg";
import { ReactComponent as GreetingsSvg } from "../../static/samples/greetings/greetings.svg";
import { ReactComponent as FillGlassOfWaterSvg } from "../../static/samples/fill-glass-of-water/fill-glass-of-water.svg";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";

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
    description: `Our workflow simulates filling up a glass of water one "count" at a time until "max" count is reached which represents our glass is full. Each time we increment the current count, the workflow checks if we need to keep refilling the glass. If the current count reaches the max count, the workflow execution ends.`,
  },
];

export function Showcase() {
  return (
    <>
      <TextContent>
        <Text component="h1">Samples Showcase</Text>
      </TextContent>
      <br />
      <Gallery hasGutter={true} minWidths={{ sm: "calc(100%/3 - 16px)", default: "100%" }}>
        {samples.map((sample) => (
          <SampleCard sample={sample} key={sample.fileName} />
        ))}
      </Gallery>
    </>
  );
}
