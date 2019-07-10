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

    await delay(FADE_OUT_DELAY);
    expect(render).toMatchSnapshot();
  });
});
