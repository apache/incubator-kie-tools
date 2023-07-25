import { test, expect } from "../fixtures/boxedExpression";

/**
 * Tests Summary
 * Context Expression
 *
 * set expression name and type
 * set expression big name
 * cancel edit expression name and type
 * set context entry name and type
 * set context entry with a big name
 * cancel edit context entry name and type
 * add row by using context-entry menu
 * add row by using plus sign
 * delete row
 *
 * resizing
 * reset resing after double clicking
 * editing by select context
 * editing by double click
 *
 */

test.describe("Context Expression", () => {
  test.beforeEach(async ({ bee }) => {
    await bee.open();
    await bee.selectContextExpression();
  });

  test("set expression name and type", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");
    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.getExpression().getByText("BUILT-IN").hover();
    await bee.page.mouse.wheel(0, 300);
    await bee.getExpression().getByRole("option", { name: "number" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

    await expect(bee.getJson()).toContainText("number");
    await expect(bee.getJson()).toContainText("Context");
    await expect(bee.getJson()).toContainText("false");
    await expect(bee.getJson()).toContainText("My Expression");
  });

  test("set expression big name", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

    await expect(bee.getExpression().getByText("My Expression with a big name", { exact: true })).toBeAttached();
    await expect(bee.getJson()).toContainText("My Expression with a big name");
  });

  test("cancel edit expression name and type", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");
    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.getExpression().getByRole("option", { name: "boolean" }).click();
    await bee.getExpression().getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(bee.getJson()).toContainText("Undefined");
    await expect(bee.getJson()).toContainText("Context");
    await expect(bee.getJson()).toContainText("false");
    await expect(bee.getJson()).toContainText("Expression Name");
  });

  test("set context entry name and type", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My Entry");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "boolean" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(bee.getJson()).not.toContainText("ContextEntry-1");
    await expect(bee.getJson()).toContainText('"name":string"My Entry"');
    await expect(bee.getJson()).toContainText('"dataType":string"boolean"');
  });

  test("set context entry with a big name", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My entry with a big name");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "boolean" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(bee.getExpression().getByText("My entry with a big name", { exact: true })).toBeAttached();
    await expect(bee.getJson()).not.toContainText("ContextEntry-1");
    await expect(bee.getJson()).toContainText('"name":string"My entry with a big name"');
    await expect(bee.getJson()).toContainText('"dataType":string"boolean"');
  });

  test("cancel edit context entry name and type", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My Entry");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "boolean" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(bee.getJson()).toContainText("ContextEntry-1");
  });

  test("add row by using context-entry menu", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await bee.getExpression().getByRole("menuitem", { name: "Insert below" }).click();

    await expect(bee.getJson()).toContainText("ContextEntry-3");
  });

  test("add row by using plus sign on top", async ({ bee }) => {
    await bee
      .getExpression()
      .getByText("ContextEntry-1", { exact: true })
      .hover({ position: { x: 1, y: 1 } });
    await bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).locator("svg").click();

    await expect(bee.getJson()).toContainText("ContextEntry-3");
  });

  test("add row by using plus sign on bottom", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).hover();
    await bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).locator("svg").click();

    await expect(bee.getJson()).toContainText("ContextEntry-3");
  });

  test("delete row", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await bee.getExpression().getByRole("menuitem", { name: "Delete" }).click();

    await expect(bee.getJson()).not.toContainText("ContextEntry-1");
  });

  test("resize results column and reset", async ({ bee }) => {
    await bee.getExpression().getByRole("cell", { name: "<result>" }).hover();
    const resizerHandle = bee.getExpression().getByTestId("resizer-handle");
    const resizerHandleBox = await resizerHandle.boundingBox();

    await bee.page.mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
    await bee.page.mouse.down();
    await bee.page.mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
    await bee.page.mouse.up();

    await expect(bee.getJson()).toContainText('"entryInfoWidth":int170');

    resizerHandle.dblclick();
    await expect(bee.getJson()).toContainText('"entryInfoWidth":int120');
  });

  // // change expression
  // test("Context Expression", async ({ bee }) => {

  // });

  // // add a literal expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a context expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a decision table expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a relation expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a function expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a invocation expression
  // test("Context Expression", async ({ bee }) => {});

  // // add a list expression
  // test("Context Expression", async ({ bee }) => {});

  // // reset a expression
  // test("Context Expression", async ({ bee }) => {});

  // // paste a expression
  // test("Context Expression", async ({ bee }) => {});

  // // resize
  // test("Context Expression", async ({ bee }) => {});

  // // reset resize
  // test("Context Expression", async ({ bee }) => {});

  // // add a two rows expression and resize
  // test("Context Expression", async ({ bee }) => {});
});
