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

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ expressionSelector }) => {
    await expressionSelector.literalExpression(page);
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
    await expressionSelector.getBee(page).getByRole("option", { name: "context" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee(page).getByText("My Expression (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "My Expression (context)" })
    ).toBeAttached();
  });

  test("set expression big name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Choose...").fill("number");
    await expressionSelector.getBee(page).getByRole("option", { name: "number" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Enter");

    await expect(expressionSelector.getBee(page).getByText("My Expression with a big name (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "My Expression with a big name (number)" })
    ).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").press("Control+a");
    await expressionSelector.getBee(page).getByPlaceholder("Expression Name").fill("My Expression");
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).click();
    await page.mouse.wheel(0, 100);
    await expressionSelector.getBee(page).getByRole("option", { name: "date and time" }).click();
    await expressionSelector.getBee(page).getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(expressionSelector.getBee(page).getByText("Expression Name (Literal)")).toBeAttached();
    await expect(
      expressionSelector.getBee(page).getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
    ).toBeAttached();
  });

  test("resing and reseting after double clicking", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByTestId("monaco-container").hover();
    const resizerHandle = expressionSelector.getBee(page).getByTestId("resizer-handle");
    await resizerHandle.dragTo(resizerHandle, {
      force: true,
      sourcePosition: { x: 0, y: 0 },
      targetPosition: { x: 50, y: 0 },
    });
    expect(await expressionSelector.getBee(page).getByRole("cell").boundingBox()).toHaveProperty("width", 240);
    await resizerHandle.dblclick();
    expect(await expressionSelector.getBee(page).getByRole("cell").boundingBox()).toHaveProperty("width", 190);
  });

  test("editing by select context", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByTestId("monaco-container").click();
    await expressionSelector.getBee(page).getByTestId("monaco-container").type('"data"');
    await expressionSelector
      .getBee(page)
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee(page).getByRole("cell")).toContainText('"data"');
  });

  test("editing by double click", async ({ expressionSelector }) => {
    await expressionSelector.getBee(page).getByTestId("monaco-container").dblclick();
    await expressionSelector.getBee(page).getByRole("textbox", { name: "Editor content" }).fill('"data"');
    await expressionSelector
      .getBee(page)
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    await expect(expressionSelector.getBee(page).getByRole("cell")).toContainText('"data"');
  });
});
