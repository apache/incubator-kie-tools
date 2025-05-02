/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Locator } from "@playwright/test";
import { Monaco } from "../../__fixtures__/monaco";
import { ExpressionCell } from "../expressionContainer";
import { NameAndDataTypeCell } from "../nameAndDataTypeCell";
import { AnnotationCell } from "../annotationCell";

export class DecisionTableExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public async fill(args: { startAtCell: number; tableData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.tableData) {
      for (const cellData of row) {
        if (cellData === "-") {
          cellNumber++;
          continue;
        }
        await this.monaco.fill({ monacoParentLocator: this.locator, content: cellData, nth: cellNumber });
        cellNumber++;
      }
      cellNumber++;
    }
  }

  /**
   * Get the cell at the specific coordinate inside the Decision Table.
   * The first index is 1, like seen in the screen, NOT zero.
   * @param coordinate The coordinate (row x column). The first index is 1.
   */
  public cellAt(coordinate: { row: number; column: number }) {
    return new ExpressionCell(
      this.locator
        .getByTestId(`kie-tools--bee--expression-row-${coordinate.row - 1}`)
        .getByTestId(`kie-tools--bee--expression-column-${coordinate.column}`),
      this.monaco
    );
  }

  get hitTableSelector() {
    return new HitTableSelector(this.locator.getByRole("columnheader").nth(0));
  }

  public async addInputAtStart() {
    await this.addInputAtIndex(0);
  }

  public async addInputAtEnd() {
    await this.addInputAtIndex(await this.locator.getByTestId("kie-tools--bee--table-header-input").count());
  }

  public async addInputAtIndex(index: number) {
    if (index === (await this.locator.getByTestId("kie-tools--bee--table-header-input").count())) {
      const bb = await this.locator
        .getByTestId("kie-tools--bee--table-header-input")
        .nth(index - 1)
        .boundingBox();
      await this.locator
        .getByTestId("kie-tools--bee--table-header-input")
        .nth(index - 1)
        .hover({
          position: {
            x: (bb?.width ?? 0) / 2,
            y: 0,
          },
        });
      await this.locator
        .getByTestId("kie-tools--bee--table-header-input")
        .nth(index - 1)
        .locator("svg")
        .click();
    } else {
      await this.locator
        .getByTestId("kie-tools--bee--table-header-input")
        .nth(index)
        .hover({
          position: {
            x: 0,
            y: 0,
          },
        });
      await this.locator
        .getByTestId("kie-tools--bee--table-header-input")
        .nth(index)
        .locator(".add-column-button svg")
        .click();
    }
  }

  public async addRowAtTop() {
    await this.locator
      .getByRole("cell", { name: "1", exact: true })
      .nth(0)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator.getByRole("cell", { name: "1", exact: true }).nth(0).locator("svg").click();
  }

  public async addRowAtBottomOfIndex(index: number) {
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .hover();
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .locator("svg")
      .click();
  }

  public async addAnnotationAtStart() {
    await this.addAnnotationAtIndex(0);
  }

  public async addAnnotationAtEnd() {
    await this.addAnnotationAtIndex(await this.locator.getByTestId("kie-tools--bee--table-header-annotation").count());
  }

  public async addAnnotationAtIndex(index: number) {
    if (index === (await this.locator.getByTestId("kie-tools--bee--table-header-annotation").count())) {
      const bb = await this.locator
        .getByTestId("kie-tools--bee--table-header-annotation")
        .nth(index - 1)
        .boundingBox();
      await this.locator
        .getByTestId("kie-tools--bee--table-header-annotation")
        .nth(index - 1)
        .hover({
          position: {
            x: (bb?.width ?? 0) / 2,
            y: 0,
          },
        });
      await this.locator
        .getByTestId("kie-tools--bee--table-header-annotation")
        .nth(index - 1)
        .locator("svg")
        .click();
    } else {
      await this.locator
        .getByTestId("kie-tools--bee--table-header-annotation")
        .nth(index)
        .hover({
          position: {
            x: 0,
            y: 0,
          },
        });
      await this.locator.getByTestId("kie-tools--bee--table-header-annotation").nth(index).locator("svg").click();
    }
  }

  public async addOutputAtStart() {
    await this.addOutputAtIndex(0);
  }

  public async addOutputAtEnd() {
    await this.addOutputAtIndex(await this.locator.getByTestId("kie-tools--bee--table-header-output").count());
  }

  // Consider the following scenario:
  //
  //  Expression Name
  //   (<Undefined>)
  // -------------------
  // output-1 | output-2
  //
  // They're all outputs cells. So:
  // locator(".output").count() = 3
  // Expression Name Cell       = [0]
  // output-1 cell              = [1]
  // output-2 cell              = [2]
  //
  // So the header of group (Expression Name) is not considered from the user perspective,
  // that's why we need to do the calculations bellow to find the right place where user really
  // wants to add the output element.
  public async addOutputAtIndex(index: number) {
    if ((await this.locator.getByTestId("kie-tools--bee--table-header-output").count()) === 1) {
      await this.addOutputFromHeaderGroupElementAtIndex(index);
    } else if (index === (await this.locator.getByTestId("kie-tools--bee--table-header-output").count())) {
      // output-1 | output-2 | output-3
      // index = 3
      // user wants:
      // output-1 | output-2 | output-3 | NEW-OUTPUT
      await this.addOutputAtRightOfIndex(index - 1);
    } else if (index + 1 === (await this.locator.getByTestId("kie-tools--bee--table-header-output").count())) {
      // output-1 | output-2 | output-3
      // index = 2
      // user wants:
      // output-1 | output-2 | NEW-OUTPUT | output-3
      await this.addOutputAtLeftOfIndex(index);
    } else {
      // output-1 | output-2 | output-3
      // index = 1
      // user wants:
      // output-1 | NEW-OUTPUT | output-2 | output-3
      await this.addOutputAtLeftOfIndex(index + 1);
      // this is the same thing as await this.addOutputAtRightOfIndex(index);
      // We can not use this.addOutputAtRightOfIndex(index) because index===0
      // internally is the group element (Expression Name Cell).
    }
  }

  public async addOutputAtRightOfIndex(index: number) {
    const bb = await this.locator.getByTestId("kie-tools--bee--table-header-output").nth(index).boundingBox();
    await this.locator
      .getByTestId("kie-tools--bee--table-header-output")
      .nth(index)
      .hover({
        position: {
          x: (bb?.width ?? 0) / 2,
          y: 0,
        },
      });
    await this.locator.getByTestId("kie-tools--bee--table-header-output").nth(index).locator("svg").click();
  }

  private async addOutputAtLeftOfIndex(index: number) {
    await this.locator
      .getByTestId("kie-tools--bee--table-header-output")
      .nth(index)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator.getByTestId("kie-tools--bee--table-header-output").nth(index).locator("svg").click();
  }

  private async addOutputFromHeaderGroupElementAtIndex(index: number) {
    if (index > 0) {
      const bb = await this.locator.getByTestId("kie-tools--bee--table-header-output").nth(0).boundingBox();
      await this.locator
        .getByTestId("kie-tools--bee--table-header-output")
        .nth(0)
        .hover({
          position: {
            x: (bb?.width ?? 0) / 2,
            y: 0,
          },
        });
      await this.locator.getByTestId("kie-tools--bee--table-header-output").nth(0).locator("svg").click();
    } else {
      await this.locator
        .getByTestId("kie-tools--bee--table-header-output")
        .nth(0)
        .hover({
          position: {
            x: 0,
            y: 0,
          },
        });
      await this.locator
        .getByTestId("kie-tools--bee--table-header-output")
        .nth(0)
        .locator(".add-column-button svg")
        .click();
    }
  }

  public inputHeaderAt(index: number) {
    return new NameAndDataTypeCell(
      this.locator.getByTestId("kie-tools--bee--table-header-input").nth(index),
      this.monaco
    );
  }

  public outputHeaderAt(index: number) {
    return new NameAndDataTypeCell(this.locator.getByTestId("kie-tools--bee--table-header-output").nth(index));
  }

  public annotationHeaderAt(index: number) {
    return new AnnotationCell(this.locator.getByTestId("kie-tools--bee--table-header-annotation").nth(index));
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByTestId("kie-tools--bee--table-header-output").nth(0));
  }
}

export class HitTableSelector {
  constructor(private locator: Locator) {}

  get cell() {
    return this.locator.nth(0);
  }

  get selectedHitTable() {
    return this.locator.getByTestId("kie-tools--bee--selected-hit-policy").nth(0);
  }

  get menu() {
    return new HitTableMenu(this.locator);
  }
}

export class HitTableMenu {
  constructor(private locator: Locator) {}

  public async availableOptions() {
    return this.locator.page().getByTestId("kie-tools--bee--hit-policy-header").getByRole("menuitem");
  }

  public option(optionName: string) {
    return this.locator.page().getByTestId("kie-tools--bee--hit-policy-header").getByRole("menuitem", {
      name: optionName,
      exact: true,
    });
  }

  public button(buttonName: string) {
    return this.locator.page().getByTestId("kie-tools--bee--hit-policy-header").getByRole("button", {
      name: buttonName,
      exact: true,
    });
  }
}
