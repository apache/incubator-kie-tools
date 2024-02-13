import type { Meta, StoryObj } from "@storybook/react";

import { DevWebApp, EMPTY_DMN_15 } from "../dev-webapp/src/DevWebApp";
import { DEFAULT_DEV_WEBAPP_DMN } from "../dev-webapp/src/DefaultDmn";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories#default-export
const meta: Meta<typeof DevWebApp> = {
  title: "Example/DmnDevWebApp",
  component: DevWebApp,
  parameters: {},
  // This component will have an automatically generated Autodocs entry: https://storybook.js.org/docs/writing-docs/autodocs
  tags: ["autodocs"],
  // More on argTypes: https://storybook.js.org/docs/api/argtypes
  // argTypes: {
  //   backgroundColor: { control: 'color' },
  // },
};

export default meta;

type Story = StoryObj<typeof DevWebApp>;

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const GettingStartedModel: Story = {
  args: {
    initialModel: DEFAULT_DEV_WEBAPP_DMN,
  },
};

export const EmptyModel: Story = {
  args: {
    initialModel: EMPTY_DMN_15(),
  },
};
