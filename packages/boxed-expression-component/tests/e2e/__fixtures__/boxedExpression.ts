import { Locator, Page, test as base } from "@playwright/test";
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

class BoxedExpressionEditor {
  constructor(public page: Page, private monaco: Monaco, public baseURL?: string) {
    this.page = page;
  }

  public async select(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
  }

  public async selectBoxedLiteral(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Literal" }).click();
  }

  public async selectBoxedContext(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Context" }).click();
  }

  public async selectDecisionTable(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Decision" }).click();
  }

  public async fillDecisionTable(args: { startAtCell: number; tableData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.tableData) {
      for (const cellData of row) {
        if (cellData === "-") {
          cellNumber++;
          continue;
        }
        await this.monaco.fill({ monacoParentLocator: this.page, content: cellData, nth: cellNumber });
        cellNumber++;
      }
      cellNumber++;
    }
  }

  public async selectRelation(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Relation" }).click();
  }

  public async fillRelation(args: { startAtCell: number; relationData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.relationData) {
      for (const cellData of row) {
        await this.monaco.fill({ monacoParentLocator: this.page, content: cellData, nth: cellNumber });
        cellNumber++;
      }
    }
  }

  public async selectBoxedInvocation(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Invocation" }).click();
  }

  public async selectBoxedList(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "List" }).click();
  }

  public async selectBoxedFunction(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Function" }).click();
  }

  public async goto() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=misc-empty-boxed-expression--base` ?? "");
  }

  public getContainer() {
    return this.page.locator(".boxed-expression-provider");
  }
}

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
