import { test, expect } from "../fixtures/boxedExpression";
import { Page } from "@playwright/test";

test.describe.configure({ mode: "serial" });

let page: Page;

test.beforeAll(async ({ browser }) => {
  page = await browser.newPage();
  await page.goto("http://localhost:3015");
});

test.afterAll(async () => {
  await page.close();
});

test.describe("Context Expression", () => {
  test.beforeEach(async ({ expressionSelector }) => {
    await expressionSelector.contextExpression(page);
  });

  test.afterEach(() => {
    page.reload();
  });

  test("set expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee(page).getByText("BUILT-IN").hover();
    await page.mouse.wheel(0, 300);
    await expressionSelector.getBee(page).getByRole("option", { name: "number" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee(page).getByText("My Expression (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "My Expression (number)" })
    ).toBeAttached();
  });

  test("set expression big name", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee(page).getByText("My Expression with a big name (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "My Expression with a big name (<Undefined>)" })
    ).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee(page).getByRole("option", { name: "boolean" }).click();
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(expressionSelector.getBee(page).getByText("Expression Name (Context)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
    ).toBeAttached();
  });

  test("set context entry name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Entry");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(expressionSelector.getBee(page).getByRole("cell", { name: "My Entry (boolean)" })).toBeAttached();
  });

  test("set context entry with a big name", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My entry with a big name");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByRole("option", { name: "context" }).click();
    await page.getByRole("button", { name: "Options menu" }).press("Enter");

    await expect(
      expressionSelector.getBee(page).getByRole("cell", { name: "My entry with a big name (context)" })
    ).toBeAttached();
  });

  test("cancel edit context entry name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click();

    // renders a div outside of the expression-container
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Entry");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(
      expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
    ).toBeAttached();
  });

  test("add row by using context-entry menu insert above", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee(page).getByRole("menuitem", { name: "Insert above" }).click();

    await expect(expressionSelector.getBee(page).getByRole("cell").first()).toContainText(
      "ContextEntry-3(<Undefined>)"
    );
  });

  test("add row by using context-entry menu insert below", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee(page).getByRole("menuitem", { name: "Insert below" }).click();

    await expect(expressionSelector.getBee(page).getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("add row by using plus sign on above", async ({ expressionSelector }) => {
    await expressionSelector
      .getBee(page)
      .getByText("ContextEntry-1", { exact: true })
      .hover({ position: { x: 0, y: 0 } });
    await expressionSelector
      .getBee(page)
      .getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
      .locator("svg")
      .click();

    await expect(expressionSelector.getBee(page).getByRole("cell").first()).toContainText(
      "ContextEntry-3(<Undefined>)"
    );
  });

  test("add row by using plus sign on below", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).hover();
    await expressionSelector
      .getBee(page)
      .getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
      .locator("svg")
      .click();

    await expect(expressionSelector.getBee(page).getByRole("cell").nth(2)).toContainText("ContextEntry-3(<Undefined>)");
  });

  test("delete row", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByText("ContextEntry-1", { exact: true }).click({
      button: "right",
    });
    await expressionSelector.getBee(page).getByRole("menuitem", { name: "Delete" }).click();

    await expect(
      expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })
    ).not.toBeAttached();
    await expect(expressionSelector.getBee(page).getByRole("cell").first()).toContainText(
      "ContextEntry-2(<Undefined>)"
    );
  });

  test("resize results column and reset", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).hover();
    const resizerHandle = expressionSelector.getBee(page).getByTestId("resizer-handle");
    await resizerHandle.dragTo(resizerHandle, {
      force: true,
      sourcePosition: { x: 0, y: 0 },
      targetPosition: { x: 50, y: 0 },
    });
    expect(await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      170
    );
    await resizerHandle.dblclick();
    expect(await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
      "width",
      120
    );
  });

  test("add literal expression", async ({ expressionSelector }) => {
    await expressionSelector.literalExpression(
      expressionSelector.getBee(page).getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );

    await expect(expressionSelector.getBee(page).getByRole("cell", { name: "=" })).toBeAttached();
    await expect(expressionSelector.getBee(page).getByRole("cell", { name: "=" })).toContainText("");

    await expressionSelector.getBee(page).getByTestId("monaco-container").click();
    await expressionSelector.getBee(page).getByTestId("monaco-container").type('"data"');
    await expressionSelector
      .getBee(page)
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee(page).getByRole("cell", { name: "=" })).toContainText('"data"');
  });

  // add a context expression
  test("add a context expression", async ({ expressionSelector }) => {
    await expressionSelector.contextExpression(
      expressionSelector.getBee(page).getByRole("row", { name: "ContextEntry-1 (<Undefined>) Select expression" })
    );
    const nestedContextExpression = expressionSelector
      .getBee(page)
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
    //   .getBee(page)
    //   .getByRole("row", { name: "My Entry (boolean) Select expression", exact: true });
    // await expressionSelector.selectLiteralExpression(newNestedContextExpression);

    // await expressionSelector.page.getByTestId("monaco-container").click();
    // await expressionSelector.page.getByTestId("monaco-container").type('"data"');
    // await expressionSelector.page
    //   .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
    //   .press("Enter");

    // await expect(expressionSelector.getBee(page).getByRole('cell', { name: '= "data" "data"', exact: true })).toBeAttached();
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
