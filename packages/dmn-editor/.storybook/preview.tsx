import * as React from "react";
import type { Preview } from "@storybook/react";

const preview: Preview = {
  decorators: [
    (Story) => (
      <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0px", left: "0px" }}>{Story()}</div>
    ),
  ],

  parameters: {
    actions: { argTypesRegex: "^on[A-Z].*" },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
    },
  },
};

export default preview;
