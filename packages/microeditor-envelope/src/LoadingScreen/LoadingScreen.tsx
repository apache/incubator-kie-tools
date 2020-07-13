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
import { useLayoutEffect, useState } from "react";
import "./styles.scss";
import "./spinner.scss";

export const FADE_OUT_DELAY = 400;

export function LoadingScreen(props: { visible: boolean }) {
  let cssAnimation;
  const [mustRender, setMustRender] = useState(true);

  if (props.visible) {
    cssAnimation = { opacity: 1 };
  } else {
    cssAnimation = { opacity: 0, transition: `opacity ${FADE_OUT_DELAY}ms` };
  }

  useLayoutEffect(
    () => {
      if (props.visible) {
        setMustRender(true);
      }
    },
    [props.visible]
  );

  return (
    (mustRender && (
      <div
        className="kogito-tooling--loading-screen"
        style={{ ...cssAnimation }}
        data-testid={"loading-screen-div"}
        onTransitionEnd={() => setMustRender(false)}
      >
        <div className="pf-c-page">
          <main role="main" className="pf-c-page__main" tabIndex={-1}>
            <div className="pf-l-bullseye">
              <div className="pf-c-empty-state pf-m-lg">
                <div className="pf-u-mb-lg">
                  <div className="pf-c-spinner" role="progressbar" aria-valuetext="Loading...">
                    <div className="pf-c-spinner__clipper" />
                    <div className="pf-c-spinner__lead-ball" />
                    <div className="pf-c-spinner__tail-ball" />
                  </div>
                </div>
                <h5 className="pf-c-title pf-m-lg">Loading...</h5>
                <div className="pf-c-empty-state__body" />
              </div>
            </div>
          </main>
        </div>
      </div>
    )) || <></>
  );
}
