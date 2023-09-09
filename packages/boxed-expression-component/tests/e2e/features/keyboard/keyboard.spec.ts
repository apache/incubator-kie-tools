import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe.skip("Keyboard", () => {
  test.describe("Navigation", () => {
    test("should correctly navigate", async () => {
      // enter, shift+enter, tab, shift+tab, escape
    });

    test.describe("Arrow keys", () => {
      test("should correctly navigate", async () => {
        // arrow up/down/left/right
      });
    });

    test.describe("Nested stories", () => {
      test("should correctly navigate", async () => {
        // enter, shift+enter, tab, shift+tab, escape
      });
    });
  });
});
