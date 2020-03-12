/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useEffect, useState } from "react";
import { KeyboardShortcutsApi } from "../api/keyboardShortcuts";

export function KeyBindingsHelpOverlay(props: { keyboardShortcuts: KeyboardShortcutsApi }) {
  const [showing, setShowing] = useState(false);

  const keyBindings = removeDuplicatesByAttr(props.keyboardShortcuts.registered(), "combination")
    .filter(k => !k.opts?.hidden)
    .map(k => {
      console.info("Mapping: " + k.label);
      return {
        combination: k.combination,
        category: k.label.split("|")[0]?.trim(),
        label: k.label.split("|")[1]?.trim()
      };
    })
    .reduce((lhs, rhs) => {
      if (!lhs.has(rhs.category)) {
        lhs.set(rhs.category, new Set([{ label: rhs.label, combination: rhs.combination }]));
      } else {
        lhs.get(rhs.category)!.add({ label: rhs.label, combination: rhs.combination });
      }
      return lhs;
    }, new Map<string, Set<{ label: string; combination: string }>>());

  useEffect(() => {
    const id = props.keyboardShortcuts.registerKeyPress(
      "shift+/",
      "Help | Show keyboard shortcuts",
      async () => setShowing(true),
      { element: window }
    );
    return () => props.keyboardShortcuts.deregister(id);
  }, []);

  useEffect(() => {
    let id: number;
    if (showing) {
      id = props.keyboardShortcuts.registerKeyPressOnce("esc", async () => setShowing(false), { element: window });
    }
    return () => {
      if (showing) {
        props.keyboardShortcuts.deregister(id);
      }
    };
  }, [showing]);

  return (
    <>
      <div
        onClick={() => setShowing(!showing)}
        style={{
          userSelect: "none",
          zIndex: 999,
          right: 0,
          bottom: 0,
          position: "fixed",
          padding: "7px",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          width: "35px",
          height: "35px",
          fontSize: "1.2em",
          cursor: "pointer"
        }}
      >
        <b>{showing ? "x" : "?"}</b>
      </div>
      {showing && (
        <div
          style={{
            userSelect: "none",
            zIndex: 998,
            top: 0,
            left: 0,
            position: "fixed",
            width: "100vw",
            height: "100vh",
            padding: "40px",
            backdropFilter: "blur(5px)",
            background: "#cacacaa6"
          }}
        >
          <h1>Keyboard shortcuts</h1>
          {Array.from(keyBindings.keys()).map(category => (
            <p key={category}>
              <h3>{category}</h3>
              {Array.from(keyBindings.get(category)!).map(keyBinding => (
                <div key={keyBinding.combination}>
                  <span
                    style={{
                      color: "black",
                      backgroundColor: "white",
                      borderRadius: "4px",
                      fontFamily: "monospace",
                      fontSize: "0.8em",
                      padding: "1px 5px 1px 5px",
                      marginRight: "4px"
                    }}
                  >
                    {formatKeyBindingCombination(keyBinding.combination)}
                  </span>
                  <span>{keyBinding.label}</span>
                </div>
              ))}
            </p>
          ))}
        </div>
      )}
    </>
  );
}

function removeDuplicatesByAttr<T>(myArr: T[], prop: keyof T) {
  return myArr.filter((obj, pos, arr) => {
    return arr.map(mapObj => mapObj[prop]).indexOf(obj[prop]) === pos;
  });
}

function formatKeyBindingCombination(combination: string) {
  return combination
    .split("+")
    .map(w => w.replace(/^\w/, c => c.toUpperCase()))
    .join(" + ");
}
