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
import { KeyBinding, KeyBindingService } from "../DefaultKeyBindingService";

export function KeyBindingsHelpOverlay(props: { keyBindingService: KeyBindingService }) {
  const [showing, setShowing] = useState(false);

  useEffect(() => {
    const id = props.keyBindingService.registerKeyPress(
      "shift+/",
      "Show keyboard shortcuts",
      async () => setShowing(true),
      { element: window }
    );
    return () => props.keyBindingService.deregister(id);
  }, []);

  useEffect(() => {
    let id: number;
    if (showing) {
      id = props.keyBindingService.registerKeyPressOnce("esc", async () => setShowing(false), { element: window });
    }
    return () => {
      if (showing) {
        props.keyBindingService.deregister(id);
      }
    };
  }, [showing]);

  function formatKeyBindingCombination(keyBinding: KeyBinding) {
    return keyBinding.combination
      .split("+")
      .map(w => w.replace(/^\w/, c => c.toUpperCase()))
      .join(" + ");
  }

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
          {props.keyBindingService.registered().map(keyBinding => (
            <h5 key={keyBinding.combination}>
              {formatKeyBindingCombination(keyBinding)} - {keyBinding.label}
            </h5>
          ))}
        </div>
      )}
    </>
  );
}
