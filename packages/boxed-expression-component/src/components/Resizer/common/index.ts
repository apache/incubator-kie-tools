/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Returns a valid width value.
 */
import { DEFAULT_MIN_WIDTH } from "../dom";

export const widthValue = (width: number | string | undefined | null): number => {
  return Math.max(Math.round(parseFloat(width + "")), DEFAULT_MIN_WIDTH);
};

/*
 * Generates a global supervisor hash for a given object.
 */
export const hashfy = (obj = {}): string => {
  const getCircularReplacer = () => {
    const seen = new WeakSet();
    return (key: string, value: unknown) => {
      if (typeof value === "object" && value !== null) {
        if (seen.has(value)) {
          return;
        }
        seen.add(value);
      }
      return value;
    };
  };

  return JSON.stringify(obj, getCircularReplacer());
};

/*
 * Propagate Cell width from DOM to React state.
 */
export const notifyCell = (id: string, width: number = DEFAULT_MIN_WIDTH, editorElement: HTMLElement): void => {
  editorElement.dispatchEvent(
    new CustomEvent(id, {
      detail: { width },
    })
  );
};
