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

import { useEffect } from "react";

type Callback = (x: number, y: number) => void;

// Module-level singleton: one DOM listener shared across all consumers.
const subscribers = new Set<Callback>();
let rafId: ReturnType<typeof requestAnimationFrame> | undefined;
const latestPos = { x: 0, y: 0 };

function onMouseMove(e: MouseEvent) {
  latestPos.x = e.clientX;
  latestPos.y = e.clientY;
  if (rafId !== undefined) return;
  rafId = requestAnimationFrame(() => {
    rafId = undefined;
    for (const fn of subscribers) {
      fn(latestPos.x, latestPos.y);
    }
  });
}

export function useHoverPosition(active: boolean, callback: Callback) {
  useEffect(() => {
    if (!active) return;
    if (subscribers.size === 0) {
      document.addEventListener("mousemove", onMouseMove);
    }
    subscribers.add(callback);
    return () => {
      subscribers.delete(callback);
      if (subscribers.size === 0) {
        document.removeEventListener("mousemove", onMouseMove);
        if (rafId !== undefined) {
          cancelAnimationFrame(rafId);
          rafId = undefined;
        }
      }
    };
  }, [active, callback]);
}
