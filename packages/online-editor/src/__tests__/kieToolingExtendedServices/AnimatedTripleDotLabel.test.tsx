/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { render, waitFor } from "@testing-library/react";
import { AnimatedTripleDotLabel } from "../../extendedServices/AnimatedTripleDotLabel";

describe("AnimatedTripleDotLabel", () => {
  test("should be valid", async () => {
    const label = "label";
    const validLabels = [
      ["label", ""],
      ["label", "."],
      ["label", ".."],
      ["label", "..."],
      ["label", ""],
    ];
    const { getByText, getByTestId } = render(<AnimatedTripleDotLabel label={label} interval={50} />);
    for (const [validLabel, dots] of validLabels) {
      expect(getByText(validLabel)).toBeInTheDocument();
      await waitFor(() => expect(getByTestId("animated-triple-dot-label")).toHaveTextContent(dots), {
        interval: 20,
        timeout: 500,
      });
    }
  });
});
