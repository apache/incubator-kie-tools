import { ReactElement } from "react";

export interface Renderer {
  render(element: ReactElement, container: HTMLElement, callback: () => void): void;
}
