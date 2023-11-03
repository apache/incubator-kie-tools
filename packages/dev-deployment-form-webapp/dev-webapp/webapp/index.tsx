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
    <div style={{ margin: "20px" }}>
      <HashRouter>
        <div style={{ display: "flex" }}>
          <Link to="/">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Home
            </Button>
          </Link>
          <Link to="/form/insurance_pricing">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Insurance Pricing
            </Button>
          </Link>
          <Link to="/form/loan_pre_qualification">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Loan Pre Qualification
            </Button>
          </Link>
          <Link to="/form/flight_rebooking">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Flight Rebooking
            </Button>
          </Link>
          <Link to="/form/recursive">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Recursive
            </Button>
          </Link>
          <Link to="/form/many_inputs">
            <Button style={{ margin: "10px" }} variant="secondary" iconPosition="left" ouiaId="edit-expression-json">
              Many Inputs
            </Button>
          </Link>
        </div>

        <div style={{ margin: "10px", border: "1px solid" }}>
          <Switch>
            <Route path="/form/">
              <DmnFormApp baseUrl={`http://localhost:${process.env.WEBPACK_REPLACE__quarkusPort}`} />
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
