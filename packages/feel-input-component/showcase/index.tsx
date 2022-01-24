/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import * as ReactDOM from "react-dom";
import { useCallback, useEffect, useMemo, useState } from "react";
import { FeelService } from "./FeelService";
import "./index.css";
import { FeelInput } from "../src";

const REACT_APP_FEEL_SERVER = process.env.REACT_APP_FEEL_SERVER;

if (!REACT_APP_FEEL_SERVER) {
  console.info(
    "" +
      "--------------------------------------------------------------------------------------------\n" +
      "The FEEL server is not enabled. You may enable it by starting your development web app with:\n" +
      "`REACT_APP_FEEL_SERVER=http://your-feel-server-url yarn start`.\n" +
      "--------------------------------------------------------------------------------------------"
  );
}

const FeelEditor = () => {
  const [feelExpression, setFeelExpression] = useState("");
  const [feelResult, setFeelResult] = useState("");
  const suggestionProvider = useCallback((text, row, col) => {
    return FeelService.getInstance().getSuggestions(text, row, col);
  }, []);

  useEffect(() => {
    const clientResult = FeelService.getInstance().evaluate(feelExpression);

    if (clientResult !== "") {
      setFeelResult(clientResult);
    }

    if (REACT_APP_FEEL_SERVER) {
      window.clearTimeout(window.__KIE__FEEL__THROTTLING___);
      window.__KIE__FEEL__THROTTLING___ = window.setTimeout(() => {
        (async () => {
          const resp = await fetch(
            REACT_APP_FEEL_SERVER +
              "?" +
              new URLSearchParams({
                feel: feelExpression,
                clientResult,
              })
          );
          const result = await resp.text();
          if (!result.includes("Server Error")) {
            setFeelResult(result);
          }
        })();
      }, 500);
    }
  }, [feelExpression]);

  const feelInput = useMemo(() => {
    return (
      <FeelInput
        enabled={true}
        suggestionProvider={suggestionProvider}
        onChange={(_event, content, _preview) => {
          setFeelExpression(content);
        }}
        options={{
          lineNumbers: "on",
        }}
      />
    );
  }, [setFeelExpression, suggestionProvider]);

  const feelOutput = useMemo(() => {
    return (
      <div className="feel-output">
        <h3>FEEL output</h3>
        {feelResult}
      </div>
    );
  }, [feelResult]);

  return (
    <>
      <div className="feel-editor">
        {feelInput}
        {feelOutput}
      </div>
    </>
  );
};

declare global {
  interface Window {
    __KIE__FEEL__THROTTLING___: number;
  }
}

ReactDOM.render(<FeelEditor />, document.getElementById("root"));
