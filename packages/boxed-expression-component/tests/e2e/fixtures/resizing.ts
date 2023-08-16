import { Page, Locator } from "@playwright/test";

interface Position {
  x: number;
  y: number;
}

export class Resizing {
  constructor(public page: Page) {}

  public async resizeCell(target: Locator, from: Position = { x: 0, y: 0 }, to: Position = { x: 0, y: 0 }) {
    await target.hover();
    const handle = target.getByTestId("resizer-handle");
    await handle.dragTo(handle, {
      force: true,
      sourcePosition: from,
      targetPosition: to,
    });
  }

  public async reset(target: Locator) {
    await target.hover();
    await target.getByTestId("resizer-handle").hover();
    await target.getByTestId("resizer-handle").dblclick({ delay: 10 });
  }
}
