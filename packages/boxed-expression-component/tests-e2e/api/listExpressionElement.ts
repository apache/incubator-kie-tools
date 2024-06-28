import { Locator } from "@playwright/test";
import { Monaco } from "../__fixtures__/monaco";
import { ExpressionElementEntry } from "./expressionContainer";

export class ListExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  async addEntry() {
    await this.locator
      .getByRole("cell", { name: "1" })
      .nth(0)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator.getByRole("cell", { name: "1" }).nth(0).locator("svg").click();
  }

  public expressionsContainers() {
    return this.locator.getByTestId("expression-container");
  }

  public entry(index: number) {
    return new ExpressionElementEntry(this.expressionsContainers().nth(index), this.monaco);
  }
}
