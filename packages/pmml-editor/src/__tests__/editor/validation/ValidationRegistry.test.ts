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

import { ValidationEntry, ValidationLevel, ValidationRegistry } from "../../../editor/validation";

let registry: ValidationRegistry;
beforeEach(() => {
  registry = new ValidationRegistry();
});

describe("ValidationRegistry", () => {
  test("get::non-existent", () => {
    expect(registry.get("root").length).toBe(0);
  });

  test("get::root::Object", () => {
    registry.set("root", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::root::JSON", () => {
    registry.set("root", { level: ValidationLevel.WARNING });

    expect(registry.get("root")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child", () => {
    registry.set("root.child1", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root.child1")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array", () => {
    registry.set("root.child1.0", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root.child1.0")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array::nested", () => {
    registry.set("root.child.1.child.1", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root.child.1")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::array::non-existent", () => {
    registry.set("root.child1.0", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root.child1.1").length).toBe(0);
  });

  test("get::child::deep", () => {
    registry.set("root.leaf.child1", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root.leaf")[0].level).toBe(ValidationLevel.WARNING);
  });

  test("get::child::multiple", () => {
    registry.set("root.child1", new ValidationEntry(ValidationLevel.WARNING));
    registry.set("root.child2", new ValidationEntry(ValidationLevel.ERROR));

    const entries: ValidationEntry[] = registry.get("root");

    expect(entries.length).toBe(2);
    expect(entries[0].level).toBe(ValidationLevel.WARNING);
    expect(entries[1].level).toBe(ValidationLevel.ERROR);
  });

  test("clear::root", () => {
    registry.set("root", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root").length).toBe(1);

    registry.clear("root");

    expect(registry.get("root").length).toBe(0);
  });

  test("clear::child", () => {
    registry.set("root.child1", new ValidationEntry(ValidationLevel.WARNING));

    expect(registry.get("root").length).toBe(1);

    registry.clear("root");

    expect(registry.get("root").length).toBe(0);
  });
  test("clear::child::multiple", () => {
    registry.set("root.child1", new ValidationEntry(ValidationLevel.WARNING));
    registry.set("root.child2", new ValidationEntry(ValidationLevel.ERROR));

    expect(registry.get("root").length).toBe(2);

    registry.clear("root.child1");

    expect(registry.get("root").length).toBe(1);

    registry.clear("root");

    expect(registry.get("root").length).toBe(0);
  });
});
