/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const AfComponentAttr = "af-js-component";

export function findChildContainers(container: HTMLElement): HTMLElement[] {
  const searchResult = searchChildren({
    root: container,
    stopWhen: (elem: any) => Boolean(getAfComponentAttr(elem)),
    accept: (elem: any) => Boolean(getAfComponentAttr(elem))
  });

  return searchResult.accepted;
}

const flatten = <T extends any>(arr: T[][]) => ([] as T[]).concat(...arr);

function searchParents(args: {
  accept: (elem: HTMLElement) => boolean;
  stop: (elem: HTMLElement) => boolean;
  element: HTMLElement;
}) {
  let parent = args.element.parentElement;
  while (parent) {
    if (args.stop(parent)) {
      return args.accept(parent);
    }
    parent = parent.parentElement;
  }
  return false;
}

function searchChildren(args: {
  root: HTMLElement;
  stopWhen: (elem: HTMLElement) => boolean;
  accept: (elem: HTMLElement) => boolean;
}) {
  const { root, stopWhen, accept } = args;
  let node: any;

  const stack = [root];
  stack.push(root);

  const accepted = new Set();
  const visited = new Set();

  while (stack.length > 0) {
    node = stack.pop()!;
    if (node !== root && node instanceof HTMLElement && stopWhen(node)) {
      if (accept(node)) {
        accepted.add(node);
      }
      visited.add(node);
    } else if (node.children && node.children.length) {
      for (const child of node.children) {
        stack.push(child);
      }
    }
  }

  return {
    visited: Array.from(visited),
    accepted: Array.from(accepted)
  };
}

function getAfComponentAttr(container: HTMLElement) {
  return container.getAttribute(AfComponentAttr);
}
