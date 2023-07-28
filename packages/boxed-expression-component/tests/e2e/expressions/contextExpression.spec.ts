import { test, expect } from "../fixtures/boxedExpression";

test.describe("Context Expression", () => {
  test.beforeEach(async ({ bee }) => {
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

    await expect(bee.getExpression().getByText("My Expression (Context)")).toBeAttached();
    await expect(bee.getExpression().getByRole("columnheader", { name: "My Expression (number)" })).toBeAttached();
  });

  test("set expression big name", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

    await expect(bee.getExpression().getByText("My Expression with a big name (Context)")).toBeAttached();
    await expect(
      bee.getExpression().getByRole("columnheader", { name: "My Expression with a big name (<Undefined>)" })
    ).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");
    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.getExpression().getByRole("option", { name: "boolean" }).click();
    await bee.getExpression().getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(bee.getExpression().getByText("Expression Name (Context)")).toBeAttached();
    await expect(
      bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
    ).toBeAttached();
  });

  test("set context entry name and type", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My Entry");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "boolean" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(bee.getExpression().getByRole("cell", { name: "My Entry (boolean)" })).toBeAttached();
  });

  test("set context entry with a big name", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My entry with a big name");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "context" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(bee.getExpression().getByRole("cell", { name: "My entry with a big name (context)" })).toBeAttached();
  });

  test("cancel edit context entry name and type", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    await bee.page.getByPlaceholder("Expression Name").fill("My Entry");
    await bee.page.getByRole("button", { name: "Options menu" }).click();
    await bee.page.getByRole("option", { name: "boolean" }).click();
    await bee.page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();
  });

  test("add row by using context-entry menu insert above", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await bee.getExpression().getByRole("menuitem", { name: "Insert above" }).click();

    await expect(bee.getExpression().getByRole("cell").first()).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using context-entry menu insert below", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await bee.getExpression().getByRole("menuitem", { name: "Insert below" }).click();

    await expect(bee.getExpression().getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using plus sign on above", async ({ bee }) => {
    await bee
      .getExpression()
      .getByText("ContextEntry-1", { exact: true })
      .hover({ position: { x: 0, y: 0 } });
    await bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).locator("svg").click();

    await expect(bee.getExpression().getByRole("cell").first()).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using plus sign on below", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).hover();
    await bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).locator("svg").click();

    await expect(bee.getExpression().getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("delete row", async ({ bee }) => {
    await bee.getExpression().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await bee.getExpression().getByRole("menuitem", { name: "Delete" }).click();

    await expect(bee.getExpression().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).not.toBeAttached();
    await expect(bee.getExpression().getByRole("cell").first()).toContainText("ContextEntry-2(<Undefined>)");
  });

  test("resize results column and reset", async ({ bee }) => {
    await bee.getExpression().getByRole("cell", { name: "<result>" }).hover();
    const resizerHandle = bee.getExpression().getByTestId("resizer-handle");
    const resizerHandleBox = await resizerHandle.boundingBox();

    await bee.page.mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
    await bee.page.mouse.down();
    await bee.page.mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
    await bee.page.mouse.up();

    expect(await bee.getExpression().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      170
    );
    await resizerHandle.dblclick();
    expect(await bee.getExpression().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      120
    );
  });

  test("add literal expression", async ({ bee }) => {
    await bee.selectLiteralExpression(
      bee.getExpression().getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );

    await expect(bee.getExpression().getByRole("cell", { name: "=" })).toBeAttached();
    await expect(bee.getExpression().getByRole("cell", { name: "=" })).toContainText("");

    await bee.getExpression().getByTestId("monaco-container").click();
    await bee.getExpression().getByTestId("monaco-container").type('"data"');
    await bee
      .getExpression()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(bee.getExpression().getByRole("cell", { name: "=" })).toContainText('"data"');
  });

  // add a context expression
  test("add a context expression", async ({ bee }) => {
    await bee.selectContextExpression(
      bee.getExpression().getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );
    const nestedContextExpression = bee.getExpression().getByRole("row", { name: "ContextEntry-1 (<Undefined>)" });

    // await expect(nestedContextExpression.getByRole("cell", { name: "{} Context" })).toBeAttached();
    // await expect(nestedContextExpression.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();

    // await nestedContextExpression.getByText("ContextEntry-1", { exact: true }).click();
    // await bee.page.getByPlaceholder("Expression Name").press("Control+a");
    // await bee.page.getByPlaceholder("Expression Name").fill("My Entry");
    // await bee.page.getByRole("button", { name: "Options menu" }).click();
    // await bee.page.getByRole("option", { name: "boolean" }).click();
    // await bee.page.getByRole("button", { name: "Options menu" }).press("Enter");

    // const newNestedContextExpression = bee
    //   .getExpression()
    //   .getByRole("row", { name: "My Entry (boolean) Select expression", exact: true });
    // await bee.selectLiteralExpression(newNestedContextExpression);

    // await bee.page.getByTestId("monaco-container").click();
    // await bee.page.getByTestId("monaco-container").type('"data"');
    // await bee.page
    //   .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
    //   .press("Enter");

    // await expect(bee.getExpression().getByRole('cell', { name: '= "data" "data"', exact: true })).toBeAttached();
  });

  // // add a decision table expression
  // test("add a decision table expression", async ({ bee }) => {});

  // // add a relation expression
  // test("add a relation expression", async ({ bee }) => {});

  // // add a function expression
  // test("add a function expression", async ({ bee }) => {});

  // // add a invocation expression
  // test("add an invocation expression", async ({ bee }) => {});

  // // add a list expression
  // test("add a list expression", async ({ bee }) => {});

  // // add a result expression
  // test("Context Expression", async ({ bee }) => {});

  // // reset a expression
  // test("Context Expression", async ({ bee }) => {});

  // // paste a expression ONLY FOR chromiumn
  // test("Context Expression", async ({ bee }) => {});

  // // resize
  // test("Context Expression", async ({ bee }) => {});

  // // reset resize
  // test("Context Expression", async ({ bee }) => {});

  // // add a two rows expression and resize
  // test("Context Expression", async ({ bee }) => {});
});
