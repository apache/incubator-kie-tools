import { test, expect } from "../fixtures/boxedExpression";

/**
 * Tests Summary
 * Context Expression
 *
 * set expression name and type
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

  test("add row by using plus sign", async ({ bee }) => {
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

  // // change expression
  // test("Context Expression", async ({ page }) => {});

  // // resize results
  // test("Context Expression", async ({ page }) => {});

  // // add a literal expression
  // test("Context Expression", async ({ page }) => {});

  // // add a context expression
  // test("Context Expression", async ({ page }) => {});

  // // add a decision table expression
  // test("Context Expression", async ({ page }) => {});

  // // add a relation expression
  // test("Context Expression", async ({ page }) => {});

  // // add a function expression
  // test("Context Expression", async ({ page }) => {});

  // // add a invocation expression
  // test("Context Expression", async ({ page }) => {});

  // // add a list expression
  // test("Context Expression", async ({ page }) => {});

  // // reset a expression
  // test("Context Expression", async ({ page }) => {});

  // // paste a expression
  // test("Context Expression", async ({ page }) => {});

  // // resize
  // test("Context Expression", async ({ page }) => {});

  // // reset resize
  // test("Context Expression", async ({ page }) => {});

  // // add a two rows expression and resize
  // test("Context Expression", async ({ page }) => {});

  //   test("Literal Expression editing by double click", async ({ page }) => {
  //     await bee.getExpression().getByTestId("monaco-container").dblclick();
  //     await bee.getExpression().getByRole("textbox", { name: "Editor content" }).fill('"data"');
  //     await bee.getExpression().getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).press("Enter");

  //     const bee.getJson() = bee.getExpression().getByTestId("boxed-expression-bee.getJson()");
  //     await expect(bee.getJson()).toContainText("Literal");
  //     await expect(bee.getJson()).toContainText("false");
  //     await expect(bee.getJson()).toContainText("Expression Name");
  //     await expect(bee.getJson()).toContainText("data");
  //   });

  //   test("Literal Expression change expression name and type", async ({ page }) => {
  //     await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
  //     await bee.getExpression().getByPlaceholder("Expression Name").click();
  //     await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
  //     await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");
  //     await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
  //     await bee.getExpression().getByRole("option", { name: "context" }).click();
  //     await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

  //     const bee.getJson() = bee.getExpression().getByTestId("boxed-expression-bee.getJson()");
  //     await expect(bee.getJson()).toContainText("Literal");
  //     await expect(bee.getJson()).toContainText("context");
  //     await expect(bee.getJson()).toContainText("My Expression");
  //   });

  //   test("Literal Expression cancel expression name and type", async ({ page }) => {
  //     await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
  //     await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
  //     await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");

  //     await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
  //     await bee.getExpression().mouse.wheel(0, 100);
  //     await bee.getExpression().getByRole("option", { name: "date and time" }).click();
  //     await bee.getExpression().getByRole("button", { name: "Options menu" }).press("Escape");

  //     const bee.getJson() = bee.getExpression().getByTestId("boxed-expression-bee.getJson()");
  //     await expect(bee.getJson()).toContainText("Literal");
  //     await expect(bee.getJson()).toContainText("<Undefined>");
  //     await expect(bee.getJson()).toContainText("Expression Name");
  //   });

  //   test("Literal Expression resizing", async ({ page }) => {
  //     const resizerHandle = bee.getExpression().getByTestId("resizer-handle");
  //     const resizerHandleBox = await resizerHandle.boundingBox();

  //     // simulate a drag moviment
  //     await bee.getExpression().mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
  //     await bee.getExpression().mouse.down();
  //     await bee.getExpression().mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
  //     await bee.getExpression().mouse.up();

  //     // drags to width 236, why?
  //     // await resizerHandle.dragTo(resizerHandle, {
  //     //   force: true,
  //     //   targetPosition: { x: 50, y: 0 },
  //     // });

  //     const bee.getJson() = bee.getExpression().getByTestId("boxed-expression-bee.getJson()");
  //     await expect(bee.getJson()).toContainText('"width":int240');
  //   });

  //   test("Literal Expression reset resing after double clicking", async ({ page }) => {
  //     const resizerHandle = bee.getExpression().getByTestId("resizer-handle");
  //     const resizerHandleBox = await resizerHandle.boundingBox();

  //     // simulate a drag moviment
  //     await bee.getExpression().mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
  //     await bee.getExpression().mouse.down();
  //     await bee.getExpression().mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
  //     await bee.getExpression().mouse.up();

  //     const bee.getJson() = bee.getExpression().getByTestId("boxed-expression-bee.getJson()");
  //     await expect(bee.getJson()).toContainText('"width":int240');

  //     resizerHandle.dblclick();
  //     await expect(bee.getJson()).toContainText('"width":int190');
  //   });
});
