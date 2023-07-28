import { test, expect } from "../fixtures/boxedExpression";

test.describe("Context Expression", () => {
  test.beforeEach(async ({ expressionSelector }) => {
    await expressionSelector.contextExpression();
  });

  test("set expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee().getByText("BUILT-IN").hover();
    await expressionSelector.page.mouse.wheel(0, 300);
    await expressionSelector.getBee().getByRole("option", { name: "number" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee().getByText("My Expression (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "My Expression (number)" })
    ).toBeAttached();
  });

  test("set expression big name", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee().getByText("My Expression with a big name (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "My Expression with a big name (<Undefined>)" })
    ).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee().getByRole("option", { name: "boolean" }).click();
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(expressionSelector.getBee().getByText("Expression Name (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
    ).toBeAttached();
  });

  test("set context entry name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await expressionSelector.page.getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.page.getByPlaceholder("Expression Name").fill("My Entry");
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.page.getByRole("option", { name: "boolean" }).click();
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(expressionSelector.getBee().getByRole("cell", { name: "My Entry (boolean)" })).toBeAttached();
  });

  test("set context entry with a big name", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await expressionSelector.page.getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.page.getByPlaceholder("Expression Name").fill("My entry with a big name");
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.page.getByRole("option", { name: "context" }).click();
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(
      expressionSelector.getBee().getByRole("cell", { name: "My entry with a big name (context)" })
    ).toBeAttached();
  });

  test("cancel edit context entry name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await expressionSelector.page.getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.page.getByPlaceholder("Expression Name").fill("My Entry");
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.page.getByRole("option", { name: "boolean" }).click();
    await expressionSelector.page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(
      expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
    ).toBeAttached();
  });

  test("add row by using context-entry menu insert above", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee().getByRole("menuitem", { name: "Insert above" }).click();

    await expect(expressionSelector.getBee().getByRole("cell").first()).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using context-entry menu insert below", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee().getByRole("menuitem", { name: "Insert below" }).click();

    await expect(expressionSelector.getBee().getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using plus sign on above", async ({ expressionSelector }) => {
    await expressionSelector
      .getBee()
      .getByText("ContextEntry-1", { exact: true })
      .hover({ position: { x: 0, y: 0 } });
    await expressionSelector
      .getBee()
      .getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
      .locator("svg")
      .click();

    await expect(expressionSelector.getBee().getByRole("cell").first()).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using plus sign on below", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).hover();
    await expressionSelector
      .getBee()
      .getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
      .locator("svg")
      .click();

    await expect(expressionSelector.getBee().getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("delete row", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee().getByRole("menuitem", { name: "Delete" }).click();

    await expect(
      expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
    ).not.toBeAttached();
    await expect(expressionSelector.getBee().getByRole("cell").first()).toContainText("ContextEntry-2(<Undefined>)");
  });

  test("resize results column and reset", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).hover();
    const resizerHandle = expressionSelector.getBee().getByTestId("resizer-handle");
    const resizerHandleBox = await resizerHandle.boundingBox();

    await expressionSelector.page.mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
    await expressionSelector.page.mouse.down();
    await expressionSelector.page.mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
    await expressionSelector.page.mouse.up();

    expect(await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      170
    );
    await resizerHandle.dblclick();
    expect(await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      120
    );
  });

  test("add literal expression", async ({ expressionSelector }) => {
    await expressionSelector.literalExpression(
      expressionSelector.getBee().getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );

    await expect(expressionSelector.getBee().getByRole("cell", { name: "=" })).toBeAttached();
    await expect(expressionSelector.getBee().getByRole("cell", { name: "=" })).toContainText("");

    await expressionSelector.getBee().getByTestId("monaco-container").click();
    await expressionSelector.getBee().getByTestId("monaco-container").type('"data"');
    await expressionSelector
      .getBee()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee().getByRole("cell", { name: "=" })).toContainText('"data"');
  });

  // add a context expression
  test("add a context expression", async ({ expressionSelector }) => {
    await expressionSelector.contextExpression(
      expressionSelector.getBee().getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );
    const nestedContextExpression = expressionSelector
      .getBee()
      .getByRole("row", { name: "ContextEntry-1 (<Undefined>)" });

    // await expect(nestedContextExpression.getByRole("cell", { name: "{} Context" })).toBeAttached();
    // await expect(nestedContextExpression.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();

    // await nestedContextExpression.getByText("ContextEntry-1", { exact: true }).click();
    // await expressionSelector.page.getByPlaceholder("Expression Name").press("Control+a");
    // await expressionSelector.page.getByPlaceholder("Expression Name").fill("My Entry");
    // await expressionSelector.page.getByRole("button", { name: "Options menu" }).click();
    // await expressionSelector.page.getByRole("option", { name: "boolean" }).click();
    // await expressionSelector.page.getByRole("button", { name: "Options menu" }).press("Enter");

    // const newNestedContextExpression = expressionSelector
    //   .getBee()
    //   .getByRole("row", { name: "My Entry (boolean) Select expression", exact: true });
    // await expressionSelector.selectLiteralExpression(newNestedContextExpression);

    // await expressionSelector.page.getByTestId("monaco-container").click();
    // await expressionSelector.page.getByTestId("monaco-container").type('"data"');
    // await expressionSelector.page
    //   .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
    //   .press("Enter");

    // await expect(expressionSelector.getBee().getByRole('cell', { name: '= "data" "data"', exact: true })).toBeAttached();
  });

  // // add a decision table expression
  // test("add a decision table expression", async ({ expressionSelector }) => {});

  // // add a relation expression
  // test("add a relation expression", async ({ expressionSelector }) => {});

  // // add a function expression
  // test("add a function expression", async ({ expressionSelector }) => {});

  // // add a invocation expression
  // test("add an invocation expression", async ({ expressionSelector }) => {});

  // // add a list expression
  // test("add a list expression", async ({ expressionSelector }) => {});

  // // add a result expression
  // test("Context Expression", async ({ expressionSelector }) => {});

  // // reset a expression
  // test("Context Expression", async ({ expressionSelector }) => {});

  // // paste a expression ONLY FOR chromiumn
  // test("Context Expression", async ({ expressionSelector }) => {});

  // // resize
  // test("Context Expression", async ({ expressionSelector }) => {});

  // // reset resize
  // test("Context Expression", async ({ expressionSelector }) => {});

  // // add a two rows expression and resize
  // test("Context Expression", async ({ expressionSelector }) => {});
});
