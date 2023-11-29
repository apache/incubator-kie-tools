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

import "@patternfly/react-core/dist/styles/base.css";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { useState } from "react";
import "./static/resources/style.css";
import { DmnFormApp } from "../../src/DmnFormApp";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { HashRouter, Link, Route, Switch } from "react-router-dom";

// each button should change routes.
// http://localhost:9008/#/form/insurance-pricing.dmn
export const App: React.FunctionComponent = () => {
  return (
    <div style={{ display: "flex", flexWrap: "nowrap" }}>
      <HashRouter>
        <div style={{ flex: "0 1" }}>
          <Link to="/">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Home
            </Button>
          </Link>
          <Link to="/form/Insurance Pricing">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Insurance Pricing
            </Button>
          </Link>
          <Link to="/form/Loan Pre Qualification">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Loan Pre Qualification
            </Button>
          </Link>
          <Link to="/form/Strategy">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Strategy
            </Button>
          </Link>
          <Link to="/form/Routing">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Routing
            </Button>
          </Link>
          <Link to="/form/Find Employees">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Find Employees
            </Button>
          </Link>
          <Link to="/form/Adjudication">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Adjudication
            </Button>
          </Link>
          <Link to="/form/Flight Rebooking">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Flight Rebooking
            </Button>
          </Link>
          <Link to="/form/Can Drive">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Can Drive
            </Button>
          </Link>
          <Link to="/form/Many Inputs">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Many Inputs
            </Button>
          </Link>
          <Link to="/form/Recursive">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Recursive
            </Button>
          </Link>
        </div>

        <div style={{ flex: "1 0" }}>
          <Switch>
            <Route path="/form/">
              <DmnFormApp baseOrigin={`http://localhost:${process.env.WEBPACK_REPLACE__quarkusPort}`} basePath="" />
            </Route>
            <Route path="/">
              <Home />
            </Route>
          </Switch>
        </div>
      </HashRouter>
    </div>
  );
};

function Home() {
  return (
    <>
      <div style={{ margin: "10px" }}>Please select an Example!</div>
    </>
  );
}

ReactDOM.render(<App />, document.getElementById("root"));
