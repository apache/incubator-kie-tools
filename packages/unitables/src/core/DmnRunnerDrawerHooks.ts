/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";

function useIntervalUntil(callback: () => Promise<{ shouldStop: boolean; cleanup?: () => void }>, ms: number) {
  useEffect(() => {
    let canceled = false;
    let effectCleanup = () => {};
    const interval = setInterval(() => {
      if (canceled) {
        return;
      }

      callback().then(({ cleanup, shouldStop }) => {
        if (canceled) {
          return;
        }

        if (shouldStop) {
          effectCleanup = cleanup ?? effectCleanup;
          clearInterval(interval);
        }
      });
    }, ms);

    return () => {
      canceled = true;
      clearInterval(interval);
      effectCleanup();
    };
  }, [ms, callback]);
}

export function useAnchoredUnitablesDrawerPanel(args: {
  inputsContainerRef: React.RefObject<HTMLDivElement>;
  outputsContainerRef: React.RefObject<HTMLDivElement>;
}) {
  const [scrollbarWidth, setScrollbarWidth] = useState(0); // Default size on Chrome.
  const [drawerPanelMinSize, setDrawerPanelMinSize] = useState<string>();
  const [drawerPanelDefaultSize, setDrawerPanelDefaultSize] = useState<string>();

  const refreshDrawerPanelDefaultSize = useCallback(() => {
    if (!args.inputsContainerRef.current) {
      return { didRefresh: false };
    }

    const children = Object.values(args.inputsContainerRef.current.childNodes);
    const newWidth = children.reduce((acc, child: HTMLElement) => acc + child.offsetWidth, 0);
    const newDefaultSize = `calc(100vw - ${newWidth + scrollbarWidth}px)`;

    setDrawerPanelDefaultSize((prev) => {
      // This is a nasty trick to force refreshing even when the value is the same.
      // Alternate with a space at the end of the state.
      return prev?.endsWith(" ") ? newDefaultSize : newDefaultSize + " ";
    });

    return { didRefresh: true };
  }, [args.inputsContainerRef, scrollbarWidth]);

  const refreshDrawerPanelMinSize = useCallback(() => {
    const outputsTable = args.outputsContainerRef.current?.querySelector(".expression-container-box");
    if (!outputsTable) {
      return { didRefresh: false };
    }

    const ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX = 59;
    const newTotalWidth = (outputsTable as HTMLElement).offsetWidth - ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX;
    const newDrawerPanelMinSize = `min(50%, ${newTotalWidth + scrollbarWidth}px)`;
    setDrawerPanelMinSize((prev) => {
      // This is a nasty trick to force refreshing even when the value is the same.
      // Alternate with a space at the end of the state.
      return prev?.endsWith(" ") ? newDrawerPanelMinSize : newDrawerPanelMinSize + " ";
    });
    return { didRefresh: true };
  }, [args.outputsContainerRef, scrollbarWidth]);

  // Keep panel minimally "glued"
  useIntervalUntil(
    useCallback(async () => {
      const { didRefresh } = refreshDrawerPanelDefaultSize();
      return { shouldStop: didRefresh };
    }, [refreshDrawerPanelDefaultSize]),
    100
  );
  useIntervalUntil(
    useCallback(async () => {
      const { didRefresh } = refreshDrawerPanelMinSize();
      return { shouldStop: didRefresh };
    }, [refreshDrawerPanelMinSize]),
    100
  );

  // Recalculate panels position when double-clicking on the resize handle.
  useIntervalUntil(
    useCallback(async () => {
      const resizer = document.querySelector(
        ".unitables--drawer .pf-c-drawer__panel .pf-c-drawer__splitter.pf-m-vertical"
      ) as HTMLElement | undefined;

      if (!resizer) {
        return { shouldStop: false };
      }

      resizer.addEventListener("dblclick", refreshDrawerPanelDefaultSize);
      resizer.addEventListener("dblclick", refreshDrawerPanelMinSize);

      return {
        shouldStop: true,
        cleanup: () => {
          resizer.removeEventListener("dblclick", refreshDrawerPanelDefaultSize);
          resizer.removeEventListener("dblclick", refreshDrawerPanelMinSize);
        },
      };
    }, [refreshDrawerPanelDefaultSize, refreshDrawerPanelMinSize]),
    100
  );

  // Keep scrolls in sync and set scrollbarWidth
  useEffect(() => {
    const content = document.querySelector(".unitables--drawer .pf-c-drawer__content") as HTMLElement | undefined;

    const panel = document.querySelector(".unitables-drawer .pf-c-drawer__panel-main") as HTMLElement | undefined;

    if (!panel || !content) {
      return;
    }

    setScrollbarWidth(content.offsetWidth - content.clientWidth);

    const syncContentScroll = () => (panel.scrollTop = content.scrollTop);
    const syncPanelScroll = () => (content.scrollTop = panel.scrollTop);

    content.addEventListener("scroll", syncContentScroll);
    panel.addEventListener("scroll", syncPanelScroll);

    return () => {
      content.removeEventListener("scroll", syncContentScroll);
      panel.removeEventListener("scroll", syncPanelScroll);
    };
  }, [drawerPanelDefaultSize, drawerPanelMinSize]);

  const drawerPanelMaxSize = useMemo(() => {
    return `max(50%, ${drawerPanelDefaultSize})`;
  }, [drawerPanelDefaultSize]);

  const forceRefresh = useCallback(() => {
    refreshDrawerPanelMinSize();
    refreshDrawerPanelDefaultSize();
  }, [refreshDrawerPanelMinSize, refreshDrawerPanelDefaultSize]);

  return {
    drawerPanelDefaultSize,
    drawerPanelMinSize,
    drawerPanelMaxSize,
    forceDrawerPanelRefresh: forceRefresh,
  };
}
