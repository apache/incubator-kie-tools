import { test } from "../../__fixtures__/base";

test.describe("Keyboard", () => {
  test.skip(true, "https://github.com/kiegroup/kie-issues/issues/542");
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
