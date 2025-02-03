/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import type { Preview } from "@storybook/react";

const preview: Preview = {
  parameters: {
    actions: { argTypesRegex: "^on[A-Z].*" },
    options: {
      storySort: {
        order: [
          "Dev",
          "SceSim Editor",
          ["Overview", "*", ["Overview", "*", ["Overview", "*"]]],
          "Features",
          ["Overview", "*", ["Overview", "*", ["Overview", "*"]]],
          "Use Cases",
          ["Overview", "*", ["Overview", "*", ["Overview", "*"]]],
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
  decorators: [
    (Story) => (
      <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0px", left: "0px" }}>{Story()}</div>
    ),
  ],
};

export default preview;
