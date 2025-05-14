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

import React, { useEffect } from "react";
import { v4 as uuidv4 } from "uuid";
import { transform } from "@babel/standalone";
import ReactDOM from "react-dom";
import * as PatternflyReact from "@patternfly/react-core/dist/js";
import * as PatternflyReactIcons from "@patternfly/react-icons/dist/js";
import * as PatternflyReactDeprecated from "@patternfly/react-core/dist/js/deprecated";
import { FormResources } from "@kie-tools/runtime-tools-shared-gateway-api/src/types";
import { sourceHandler } from "../../../utils";
import ResourcesContainer from "../ResourcesContainer/ResourcesContainer";

import "@patternfly/patternfly/patternfly.css";

interface ReactFormRendererProps {
  source: string;
  resources: FormResources;
  setIsExecuting: (isExecuting: boolean) => void;
}

declare global {
  interface Window {
    PatternFlyReact: any;
    PatternflyReactIcons: any;
    PatternflyReactDeprecated: any;
    PatternFly: any;
  }
}

const ReactFormRenderer: React.FC<ReactFormRendererProps> = ({ source, resources, setIsExecuting }) => {
  useEffect(() => {
    /* istanbul ignore else */
    if (source) {
      renderform();
    }
  }, [source, resources]);

  const renderform = () => {
    /* istanbul ignore else */
    if (source) {
      setIsExecuting(true);
      try {
        window.React = React;
        window.ReactDOM = ReactDOM;

        window.PatternFlyReact = PatternflyReact;
        window.PatternflyReactIcons = PatternflyReactIcons;
        window.PatternflyReactDeprecated = PatternflyReactDeprecated;

        const container = document.getElementById("formContainer");
        if (!container) {
          return;
        }
        container.innerHTML = "";
        const containerId = uuidv4();
        const formContainer: HTMLElement = document.createElement("div");
        formContainer.id = containerId;

        container.appendChild(formContainer);

        const {
          reactElements,
          patternflyElements,
          patternflyIconElements,
          patternflyDeprecatedElements,
          formName,
          trimmedSource,
        } = sourceHandler(source)!;

        const content = `
        const {${reactElements}} = React;
        const {${patternflyElements}} = PatternFlyReact;
        ${patternflyIconElements !== undefined ? `const {${patternflyIconElements}} = PatternflyReactIcons;` : ""}
        ${patternflyDeprecatedElements !== undefined ? `const {${patternflyDeprecatedElements}} = PatternflyReactDeprecated;` : ""}
        ${trimmedSource}
        const target = document.getElementById("${containerId}");
        const element = window.React.createElement(${formName}, {});
        window.ReactDOM.render(element, target);
        `;

        const reactCode = transform(content.trim(), {
          presets: [
            "react",
            [
              "typescript",
              {
                allExtensions: true,
                isTSX: true,
              },
            ],
          ],
        }).code;

        const scriptElement: HTMLScriptElement = document.createElement("script");
        scriptElement.type = "module";
        scriptElement.text = reactCode!;
        container.appendChild(scriptElement);
      } finally {
        setIsExecuting(false);
      }
    }
  };

  return (
    <>
      <div>
        <ResourcesContainer resources={resources} />
        <div
          style={{
            height: "100%",
          }}
          id={"formContainer"}
        >
          {}
        </div>
      </div>
    </>
  );
};

export default ReactFormRenderer;
