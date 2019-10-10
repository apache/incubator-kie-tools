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
import { shallow } from "enzyme";
import { FADE_OUT_DELAY, LoadingScreen } from "../LoadingScreen";

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("LoadingScreen", () => {
  test("when visible", () => {
    const render = shallow(<LoadingScreen visible={true} />);
    expect(render).toMatchSnapshot();
  });

  test("when just made not visible", () => {
    const render = shallow(<LoadingScreen visible={false} />);
    expect(render).toMatchSnapshot();
  });

  test("when not visible after fadeout delay", async () => {
    const render = shallow(<LoadingScreen visible={false} />);
    render.simulate("transitionEnd");

    await delay(FADE_OUT_DELAY);
    expect(render).toMatchSnapshot();
  });
});
