import { Page, Locator } from "@playwright/test";
import { expect } from "./boxedExpression";

// export class Helper {
//   constructor(public page: Page) {}

//   public async populate(target: Locator, from: Position = { x: 0, y: 0 }, to: Position = { x: 0, y: 0 }) {
//     await target.hover();
//     const handle = target.getByTestId("resizer-handle");
//     await handle.dragTo(handle, {
//       force: true,
//       sourcePosition: from,
//       targetPosition: to,
//     });
//   }

//   public async checkEmptyness(rows: number, columns: number) {
//     for (let i = 1; i < rows; i++) {
//       for (let j = 1; j < columns; j++) {
//         await expect(
//           this.page
//             .getByRole("row", { name: `${i}`, exact: true })
//             .getByRole("cell")
//             .nth(j)
//         ).toContainText("");
//       }
//     }
//   }
// }
