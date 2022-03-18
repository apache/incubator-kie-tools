/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { render } from "@testing-library/react";
import * as React from "react";
import { EmptyStateWidget } from "../../components/EmptyStateWidget";
import CubesIcon from "@patternfly/react-icons/dist/js/icons/cubes-icon";

describe("EmptyStateWidget component tests", () => {
  test("EmptyStateWidget component tests", () => {
    const { container } = render(
      <EmptyStateWidget
        emptyStateTitleHeading={"h6"}
        emptyStateTitleSize={"md"}
        emptyStateIcon={CubesIcon}
        emptyStateBodyText={"Body Text"}
        emptyStateTitleText={"Title Text"}
      />
    );

    expect(container).toMatchSnapshot();
  });
});
