/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Card } from "@patternfly/react-core";
import { AutoField, NestField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

test("<NestField> - renders an <AutoField> for each field", () => {
  const element = <NestField name="x" />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(wrapper.find(AutoField)).toHaveLength(2);
  // expect(
  //   wrapper
  //     .find(AutoField)
  //     .at(0)
  //     .prop('name'),
  // ).toBe('x.a');
  // expect(
  //   wrapper
  //     .find(AutoField)
  //     .at(1)
  //     .prop('name'),
  // ).toBe('x.b');
});

test("<NestField> - renders custom content if given", () => {
  const element = (
    <NestField name="x">
      <article data-test="content" />
    </NestField>
  );
  const wrapper = mount(
    element,
    createContext({
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(wrapper.find(AutoField)).toHaveLength(0);
  expect(wrapper.find("article").at(1)).toHaveLength(1);
  expect(wrapper.find("article").at(1).prop("data-test")).toBe("content");
});

test("<NestField> - renders a label", () => {
  const element = <NestField name="x" label="y" />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(wrapper.find("label")).toHaveLength(3);
  expect(wrapper.find("label").at(0).text()).toBe("y");
});

test("<NestField> - renders a wrapper with unknown props", () => {
  const element = <NestField name="x" data-x="x" data-y="y" data-z="z" />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(wrapper.find(Card).at(0).prop("data-x")).toBe("x");
  expect(wrapper.find(Card).at(0).prop("data-y")).toBe("y");
  expect(wrapper.find(Card).at(0).prop("data-z")).toBe("z");
});
