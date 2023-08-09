import * as React from "react";
import type { Preview } from "@storybook/react";
import "../src/expressions/BoxedExpressionEditor/base-no-reset-wrapped.css";

const preview: Preview = {
  parameters: {
    actions: { argTypesRegex: "^on[A-Z].*" },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
    },
  },

  // It should be Story() to be possible to use "preview-api" inside stories; (https://github.com/storybookjs/storybook/issues/22132)
  decorators: [(Story) => <div style={{ margin: "1em" }}>{Story()}</div>],
};

export default preview;
