/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useState } from "react";

export const FADE_OUT_DELAY = 400;

export function LoadingScreen(props: { visible: boolean }) {
  let cssAnimation;
  const [mustRender, setMustRender] = useState(true);

  if (props.visible) {
    cssAnimation = { opacity: 1 };
  } else {
    cssAnimation = { opacity: 0, transition: `opacity ${FADE_OUT_DELAY}ms` };
  }

  return (
    (mustRender && (
      <div
        style={{
          width: "100vw",
          height: "100vh",
          textAlign: "center",
          backgroundColor: "#1e1e1e",
          padding: "40px 0 0 0",
          ...cssAnimation
        }}
        onTransitionEnd={() => setMustRender(false)}
      >
        <span style={{ fontFamily: "Helvetica", color: "white", fontSize: "12pt" }}>Loading...</span>
      </div>
    )) || <></>
  );
}
