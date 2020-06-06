/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { SpacesScreen } from "../SpacesScreen";
import { shallow } from "enzyme";

describe("snapshot", () => {
  function newSpacesScreen() {
    return shallow(<SpacesScreen exposing={jest.fn()} />, {
      disableLifecycleMethods: true
    });
  }

  beforeEach(() => {
    jest.resetAllMocks();
    SpacesScreen.prototype.canCreateSpace = () => true;
  });

  test("of spaces screen with no space but loading", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: true,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with some spaces but no permission to create spaces", () => {
    SpacesScreen.prototype.canCreateSpace = () => false;
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: [{ name: "Foo", description: "This is test space", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with some spaces", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: [{ name: "Foo", description: "This is test space", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space but loading and popup open", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: true,
      loading: true,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space and popup open", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: true,
      loading: false,
      spaces: [{ name: "Foo", description: "This is test space", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });
});
