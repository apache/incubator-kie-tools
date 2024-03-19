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

import { test, expect } from "../../__fixtures__/base";
import { AddColumnPosition } from "../../__fixtures__/table";

test.describe("Populate Decision Background table", () => {
  test("should correctly populate a decision-based background table", async ({
    editor,
    resizing,
    table,
    backgroundTable,
  }) => {
    await editor.openBackgroundTableDecision();
    await backgroundTable.fillBackgroundTableCell({ content: "{foo}", column: 0 });

    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await backgroundTable.fillBackgroundTableCell({ content: "[foo]", column: 1 });
    await backgroundTable.fillBackgroundTableCell({ content: '"foo"', column: 2 });
    await backgroundTable.fillBackgroundTableCell({ content: ",./123981275980172957129517", column: 3 });
    await backgroundTable.fillBackgroundTableCell({ content: "{foo}{foo}f", column: 4 });
    await backgroundTable.fillBackgroundTableCell({ content: "=1-205=-1205=-0125-0215215", column: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-decision.png");
  });
});

test.describe("Populate Rule Background table", () => {
  test("should correctly populate a rule-based background table", async ({
    editor,
    resizing,
    table,
    backgroundTable,
  }) => {
    await editor.openBackgroundTableRule();
    await backgroundTable.fillBackgroundTableCell({ content: "{foo}", column: 0 });

    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCell: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await backgroundTable.fillBackgroundTableCell({ content: "[foo]", column: 1 });
    await backgroundTable.fillBackgroundTableCell({ content: '"foo"', column: 2 });
    await backgroundTable.fillBackgroundTableCell({ content: ",./123981275980172957129517", column: 3 });
    await backgroundTable.fillBackgroundTableCell({ content: "{foo}{foo}f", column: 4 });
    await backgroundTable.fillBackgroundTableCell({ content: "=1-205=-1205=-0125-0215215", column: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-rule.png");
  });
});
