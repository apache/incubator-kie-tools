import { test as base } from "@playwright/test";
import { BoxedExpressionEditor } from "./boxedExpression";
import { Clipboard } from "./clipboard";
import { Stories } from "./stories";
import { Resizing } from "./resizing";
import { UseCases } from "./useCases";
import { Monaco } from "./monaco";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  stories: Stories;
  clipboard: Clipboard;
  resizing: Resizing;
  useCases: UseCases;
  monaco: Monaco;
};

export const test = base.extend<BoxedExpressionFixtures>({
  monaco: async ({ page }, use) => {
    await use(new Monaco(page));
  },
  boxedExpressionEditor: async ({ page, baseURL, monaco }, use) => {
    await use(new BoxedExpressionEditor(page, monaco, baseURL));
  },
  stories: async ({ page, baseURL }, use) => {
    await use(new Stories(page, baseURL));
  },
  clipboard: async ({ browserName, context, page }, use) => {
    const clipboard = new Clipboard(page);
    clipboard.setup(context, browserName);
    await use(clipboard);
  },
  resizing: async ({ page }, use) => {
    await use(new Resizing(page));
  },
  useCases: async ({ page, baseURL }, use) => {
    await use(new UseCases(page, baseURL));
  },
});

export { expect } from "@playwright/test";
