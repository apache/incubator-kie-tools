/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ValidationEntry, ValidationLevel, ValidationStore } from "@kogito-tooling/pmml-editor/dist/editor/validation";

let store: ValidationStore;
beforeEach(() => {
  store = new ValidationStore();
});

const asPath = (segment: string) => {
  return { path: segment };
};

describe("ValidationStore", () => {
  test("get::non-existent", () => {
    expect(store.get(asPath("root")).length).toBe(0);
  });

  test("get::root::Object", () => {
    store.set(asPath("root"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::root::JSON", () => {
    store.set(asPath("root"), { level: ValidationLevel.WARNING });

    expect(store.get(asPath("root"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child", () => {
    store.set(asPath("root.child1"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root.child1"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array", () => {
    store.set(asPath("root.child1.0"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root.child1.0"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array::nested", () => {
    store.set(asPath("root.child.1.child.1"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root.child.1"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array::non-existent", () => {
    store.set(asPath("root.child1.0"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root.child1.1")).length).toBe(0);
  });

  test("get::child::deep", () => {
    store.set(asPath("root.leaf.child1"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root.leaf"))[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::multiple", () => {
    store.set(asPath("root.child1"), new ValidationEntry(ValidationLevel.WARNING));
    store.set(asPath("root.child2"), new ValidationEntry(ValidationLevel.ERROR));

    const entries: ValidationEntry[] = store.get(asPath("root"));

    expect(entries.length).toBe(2);
    expect(entries[0].level).toBe(ValidationLevel.WARNING);
    expect(entries[1].level).toBe(ValidationLevel.ERROR);
  });

  test("clear::root", () => {
    store.set(asPath("root"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root")).length).toBe(1);

    store.clear(asPath("root"));

    expect(store.get(asPath("root")).length).toBe(0);
  });

  test("clear::child", () => {
    store.set(asPath("root.child1"), new ValidationEntry(ValidationLevel.WARNING));

    expect(store.get(asPath("root")).length).toBe(1);

    store.clear(asPath("root"));

    expect(store.get(asPath("root")).length).toBe(0);
  });
  test("clear::child::multiple", () => {
    store.set(asPath("root.child1"), new ValidationEntry(ValidationLevel.WARNING));
    store.set(asPath("root.child2"), new ValidationEntry(ValidationLevel.ERROR));

    expect(store.get(asPath("root")).length).toBe(2);

    store.clear(asPath("root.child1"));

    expect(store.get(asPath("root")).length).toBe(1);

    store.clear(asPath("root"));

    expect(store.get(asPath("root")).length).toBe(0);
  });
});
