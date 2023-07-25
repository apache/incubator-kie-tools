import { test, expect } from "../fixtures/boxedExpression";
import { LiteralExpressionDefinition } from "@kie-tools/boxed-expression-component/dist/api";

/**
 * Tests Summary
 * Literal Expression
 *
 * set expression name and type
 * set expression big name
 * cancel edit expression name and type
 * resing and reseting after double clicking
 * editing by select context
 * editing by double click
 *
 */

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ bee }) => {
    await bee.open();
    await bee.selectLiteralExpression();
  });

  test("set expression name and type", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");
    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.getExpression().getByRole("option", { name: "context" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.logicType).toBe("Literal");
    expect(json.dataType).toBe("context");
    expect(json.name).toBe("My Expression");
  });

  test("set expression big name", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression with a big name");
    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.getExpression().getByRole("option", { name: "context" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Enter");

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.logicType).toBe("Literal");
    expect(json.dataType).toBe("context");
    expect(json.name).toBe("My Expression with a big name");
  });

  test("cancel edit expression name and type", async ({ bee }) => {
    await bee.getExpression().getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await bee.getExpression().getByPlaceholder("Expression Name").press("Control+a");
    await bee.getExpression().getByPlaceholder("Expression Name").fill("My Expression");

    await bee.getExpression().getByRole("button", { name: "Options menu" }).click();
    await bee.page.mouse.wheel(0, 100);
    await bee.getExpression().getByRole("option", { name: "date and time" }).click();
    await bee.getExpression().getByRole("button", { name: "Options menu" }).press("Escape");

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.logicType).toBe("Literal");
    expect(json.dataType).toBe("<Undefined>");
    expect(json.name).toBe("Expression Name");
  });

  test("resing and reseting after double clicking", async ({ bee }) => {
    await bee.getExpression().getByTestId("monaco-container").hover();
    const resizerHandle = bee.getExpression().getByTestId("resizer-handle");
    const resizerHandleBox = await resizerHandle.boundingBox();

    await bee.page.mouse.move(resizerHandleBox!.x, resizerHandleBox!.y);
    await bee.page.mouse.down();
    await bee.page.mouse.move(resizerHandleBox!.x + 50, resizerHandleBox!.y);
    await bee.page.mouse.up();

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.width).toBe(240);

    resizerHandle.dblclick();
    expect(json.width).toBe(190);
  });

  test("editing by select context", async ({ bee }) => {
    await bee.getExpression().getByTestId("monaco-container").click();
    await bee.getExpression().getByTestId("monaco-container").type('"data"');
    await bee
      .getExpression()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.logicType).toBe("Literal");
    expect(json.dataType).toBe("<Undefined>");
    expect(json.name).toBe("Expression Name");
    expect(json.content).toBe('"data"');
  });

  test("editing by double click", async ({ bee }) => {
    await bee.getExpression().getByTestId("monaco-container").dblclick();
    await bee.getExpression().getByRole("textbox", { name: "Editor content" }).fill('"data"');
    await bee
      .getExpression()
      .getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." })
      .press("Enter");

    const json = await bee.getJson<LiteralExpressionDefinition>();
    expect(json.logicType).toBe("Literal");
    expect(json.dataType).toBe("<Undefined>");
    expect(json.name).toBe("Expression Name");
    expect(json.content).toBe('"data"');
  });
});
