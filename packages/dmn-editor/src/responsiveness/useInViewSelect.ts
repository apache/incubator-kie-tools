import * as React from "react";

export const IN_VIEW_SELECT_PADDING = 12;
export const TABS_HEIGHT = 40;

export function useInViewSelect(ref: React.RefObject<HTMLElement>, self: React.RefObject<HTMLElement>, factor = 1) {
  const reference = (ref.current ?? document.body).getBoundingClientRect();
  const s = self.current?.getBoundingClientRect();

  if (!s) {
    return { maxHeight: undefined, direction: undefined };
  }

  const below = reference.height - (s.y - reference.y + s.height + IN_VIEW_SELECT_PADDING);
  const above = s.y - reference.y - IN_VIEW_SELECT_PADDING - TABS_HEIGHT;

  if (above > below) {
    return { maxHeight: above, direction: "up" as const };
  } else {
    return { maxHeight: below, direction: "down" as const };
  }
}
