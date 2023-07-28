import { test, expect } from "../fixtures/boxedExpression";

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ expressionSelector }) => {
    await expressionSelector.literalExpression();
  });

  test("set expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee().getByRole("option", { name: "context" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee().getByText("My Expression (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "My Expression (context)" })
    ).toBeAttached();
  });

  test("set expression big name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee().getByPlaceholder("Choose...").fill("number");
    await expressionSelector.getBee().getByRole("option", { name: "number" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee().getByText("My Expression with a big name (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "My Expression with a big name (number)" })
    ).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee().getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee().getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.page.mouse.wheel(0, 100);
    await expressionSelector.getBee().getByRole("option", { name: "date and time" }).click();
    await expressionSelector.getBee().getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(expressionSelector.getBee().getByText("Expression Name (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
    ).toBeAttached();
  });

  test("resing and reseting after double clicking", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByTestId("monaco-container").hover();
    const resizerHandle = expressionSelector.getBee().getByTestId("resizer-handle");
    const resizerHandleBox = await resizerHandle.boundingBox();

    await expressionSelector.page.mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
    await expressionSelector.page.mouse.down();
    await expressionSelector.page.mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
    await expressionSelector.page.mouse.up();

    expect(await expressionSelector.getBee().getByRole("cell").boundingBox()).toHaveProperty("width", 240);
    await resizerHandle.dblclick();
    expect(await expressionSelector.getBee().getByRole("cell").boundingBox()).toHaveProperty("width", 190);
  });

  test("editing by select context", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByTestId("monaco-container").click();
    await expressionSelector.getBee().getByTestId("monaco-container").type('"data"');
    await expressionSelector
      .getBee()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee().getByRole("cell")).toContainText('"data"');
  });

  test("editing by double click", async ({ expressionSelector }) => {
    await expressionSelector.getBee().getByTestId("monaco-container").dblclick();
    await expressionSelector.getBee().getByRole("textbox", { name: "Editor content" }).fill('"data"');
    await expressionSelector
      .getBee()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee().getByRole("cell")).toContainText('"data"');
  });
});
