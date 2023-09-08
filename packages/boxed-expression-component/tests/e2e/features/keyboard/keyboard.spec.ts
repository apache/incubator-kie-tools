import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe.skip("Keyboard", () => {
  test.describe("Navigation", () => {
    test("should navigate correctly with enter key", async ({ page, resizing }) => {});

    test("should navigate correctly with shift+enter key", async ({ page, resizing }) => {});

    test("should navigate correctly with tab key", async ({ page, resizing }) => {});

    test("should navigate correctly with shift+tab key", async ({ page, resizing }) => {});

    test("should navigate correctly with esc", async ({ page, resizing }) => {});

    test.describe("Arrow keys", () => {
      test("should navigate correctly with arrow left", async ({ page, resizing }) => {});

      test("should navigate correctly with arrow right", async ({ page, resizing }) => {});

      test("should navigate correctly with arrow down", async ({ page, resizing }) => {});

      test("should navigate correctly with arrow up", async ({ page, resizing }) => {});
    });

    test.describe("Nested expressions", () => {
      test("should navigate correctly with enter key", async ({ page, resizing }) => {});

      test("should navigate correctly with shift+enter key", async ({ page, resizing }) => {});

      test("should navigate correctly with tab key", async ({ page, resizing }) => {});

      test("should navigate correctly with shift+tab key", async ({ page, resizing }) => {});

      test("should navigate correctly with esc", async ({ page, resizing }) => {});
    });
  });
});
