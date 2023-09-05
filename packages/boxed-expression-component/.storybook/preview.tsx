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
    options: {
      storySort: {
        order: [
          "_dev",
          "Boxed Expressions",
          ["Overview", "*", ["Overview", "*", ["Overview", "*"]]],
          "Features",
          ["*", ["Overview", "*", ["Overview", "*"]]],
          "Use cases",
          ["*", ["Overview", "*", ["Overview", "*"]]],
        ],
      },
    },
    docs: {
      toc: {
        headingSelector: "h2, h3",
      },
    },
  },

  // It should be Story() to be possible to use "preview-api" inside stories; (https://github.com/storybookjs/storybook/issues/22132)
  decorators: [(Story) => <div style={{ margin: "1em", width: "fit-content" }}>{Story()}</div>],
};

export default preview;
