/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { useState } from "react";
import "../static/resources/style.css";
import { DmnFormApp } from "../src/DmnFormApp";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

export const App: React.FunctionComponent = () => {
  const [version, setVersion] = useState(-1);

  return (
    <div className="showcase">
      <h3 style={{ position: "absolute", right: 0 }}>v{version}&nbsp;&nbsp;</h3>
      <div className="boxed-expression">
        <DmnFormApp />
      </div>

      <div className="updated-json">
        <div className="buttons">
          <Button variant="secondary" iconPosition="left" ouiaId="edit-expression-json"></Button>
        </div>
      </div>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
