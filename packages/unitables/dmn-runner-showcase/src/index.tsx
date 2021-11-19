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
import { useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
import "@patternfly/react-core/dist/styles/base-no-reset.css";
import { DmnAutoTable } from "./unitables";
import * as schemas from "./schemas";
import * as results from "./results";
import { Button } from "@patternfly/react-core";

export const App: React.FunctionComponent = () => {
  const [tableData, setTableData] = useState([]);
  const [formError, setFormError] = useState(false);

  const [schema, setSchema] = useState<any>(schemas.normalDataType);
  const [result, setResult] = useState<any>(results.normalResults);

  return (
    <>
      <div>
        <h2>Inputs</h2>
        <div style={{ display: "flex" }}>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.emptyInput)}>
              Empty Input
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.normalDataType)}>
              Normal Type
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.multipleInputs)}>
              Multiple Inputs
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.simpleCustomDataType)}>
              Simple Custom
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.customWithMultiple)}>
              Custom With Multiple
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.multipleCustomNormal)}>
              Multiple Custom Normal
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.multipleCustomEnum)}>
              Multiple Custom Enum
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.complexCustomDataType)}>
              Complex Custom
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"primary"} onClick={() => setSchema(schemas.inputOutput)}>
              Input Output
            </Button>
          </div>
        </div>
      </div>

      <div>
        <h2>Results</h2>
        <div style={{ display: "flex" }}>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.normalResults)}>
              Normal Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.multipleResults)}>
              Multiple Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.arrayResult)}>
              Array Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.arrayMultipleResults)}>
              Array Multiple Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.objectResults)}>
              Object Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.skippedResult)}>
              Skipped Results
            </Button>
          </div>
          <div style={{ padding: "5px" }}>
            <Button variant={"secondary"} onClick={() => setResult(results.failedResult)}>
              Failed Results
            </Button>
          </div>
        </div>
      </div>

      <div className="showcase">
        <div className="boxed-expression">
          <DmnAutoTable
            schema={schema}
            tableData={tableData}
            setTableData={setTableData}
            results={result}
            formError={formError}
            setFormError={setFormError}
          />
        </div>
      </div>
    </>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
