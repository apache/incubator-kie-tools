/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
const path = require("path");
const fs = require("fs");
const formData = require("../MockData/forms/formData");
const customDashboardData = require("../MockData/customDashboard/data");
const { v4: uuidv4 } = require("uuid");

const formsUnableToSave = ["html_hiring_ITInterview", "react_hiring_ITInterview"];

/**
 * Check if the formName is one of the accepted ones
 *
 * @param {string} formName
 */
function validateFormName(formName) {
  return [
    "html_hiring_HRInterview",
    "html_hiring_ITInterview",
    "react_hiring_HRInterview",
    "react_hiring_ITInterview",
  ].includes(formName);
}

module.exports = controller = {
  getCustomDashboards: (req, res) => {
    const filterNames = req.query.names.split(";");
    if (filterNames[0].length === 0) {
      res.send(customDashboardData);
    } else {
      const filteredCustomDashboards = [];
      filterNames.forEach((name) => {
        customDashboardData.forEach((customDashboard) => {
          if (customDashboard.name === name) {
            filteredCustomDashboards.push(customDashboard);
          }
        });
      });
      res.send(filteredCustomDashboards);
    }
  },

  getCustomDashboardContent: (req, res) => {
    const dashboardName = req.params.name;
    let content = "";
    if (dashboardName === "age.dash.yaml") {
      content = fs.readFileSync(__dirname + "/customDashboard/age.dash.yaml", "utf-8");
    }
    if (dashboardName === "products.dash.yaml") {
      content = fs.readFileSync(__dirname + "/customDashboard/products.dash.yaml", "utf-8");
    }
    res.send(content);
  },

  getForms: (req, res) => {
    const formFilterNames = req.query.names.split(";");
    if (formFilterNames[0].length === 0) {
      res.send(formData);
    } else {
      const filteredForms = [];
      formFilterNames.forEach((name) => {
        formData.forEach((form) => {
          if (form.name === name) {
            filteredForms.push(form);
          }
        });
      });
      res.send(filteredForms);
    }
  },

  getFormContent: (req, res) => {
    console.log(`......Get Custom Form Content: --formName:${req.params.formName}`);
    const formName = req.params.formName;
    const formInfo = formData.filter((datum) => datum.name === formName);

    if (formInfo.length === 0 || !validateFormName(formName)) {
      res.status(500).send("Cannot find form");
      return;
    }
    let sourceString;

    const configString = fs.readFileSync(path.join(`${__dirname}/forms/examples/${formName}.config`), "utf8");
    if (formInfo[0].type.toLowerCase() === "html") {
      sourceString = fs.readFileSync(path.join(`${__dirname}/forms/examples/${formName}.html`), "utf8");
    } else if (formInfo[0].type.toLowerCase() === "tsx") {
      sourceString = fs.readFileSync(path.join(`${__dirname}/forms/examples/${formName}.tsx`), "utf8");
    }
    const response = {
      formInfo: formInfo[0],
      source: sourceString,
      configuration: JSON.parse(configString),
    };

    res.send(response);
  },

  saveFormContent: (req, res) => {
    console.log(`......Save Form Content: --formName:${req.params.formName}`);
    if (formsUnableToSave.includes(req.params.formName)) {
      res.status(500).send("Unexpected failure saving form!");
    } else {
      res.send("Saved!");
    }
  },

  startProcessInstance: (_req, res) => {
    res.send({
      id: uuidv4(),
    });
  },

  triggerCloudEvent: (req, res) => {
    console.log(
      `......Trigger Cloud Event:: id: ${req.headers["ce-id"]} type: ${req.headers["ce-type"]} source: ${req.headers["ce-source"]}`
    );

    if (req.body.type === "error") {
      res.status(500).send("internal server error");
    }
    if (req.body.kogitobusinesskey) {
      console.log(`Starting Serverless workflow with business key: ${req.body.kogitobusinesskey}`);
      return res.status(200).send(encodeURIComponent(req.body.kogitobusinesskey));
    } else if (req.body.kogitoprocrefid) {
      console.log(`Serverless Workflow with id ${req.body.kogitoprocrefid} successfully completed`);
      return res.status(200).send(encodeURIComponent(req.body.kogitoprocrefid));
    }

    return res.status(200).send("Cloud Event successfully triggered");
  },
};
