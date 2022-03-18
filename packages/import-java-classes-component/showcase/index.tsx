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
import { useCallback, useState } from "react";
import "@patternfly/react-core/dist/styles/base.css";
import "./index.css";
import { ImportJavaClasses } from "../src";

const Showcase: React.FunctionComponent = () => {
  const LSP_SERVER_NOT_AVAILABLE = "Java LSP Server is not available. Please install Java Extension";
  const [buttonDisableStatus, setButtonDisableStatus] = useState(true);
  const [buttonTooltipMessage, setButtonTooltipMessage] = useState(LSP_SERVER_NOT_AVAILABLE);
  const onSelectChange = useCallback((event) => setButtonDisableStatus(event.target.value === "true"), []);
  const onInputChange = useCallback((event) => setButtonTooltipMessage(event.target.value), []);
  /* This function temporary mocks a call to the LSP service method getClasses */
  const lspGetClassServiceMocked = (value: string) => {
    /* Mocked data retrieved from LSP Service */
    const booClassesList = ["org.kie.test.kogito.Book", "org.kie.test.kogito.Boom"];
    const bookClassesList = ["org.kie.test.kogito.Book"];
    const boomClassesList = ["org.kie.test.kogito.Boom"];

    /* Temporary mocks managing */
    if (value === "Boo") {
      return booClassesList;
    } else if (value === "Book") {
      return bookClassesList;
    } else if (value === "Boom") {
      return boomClassesList;
    } else {
      return [];
    }
  };
  const lspGetClassFieldsServiceMocked = async (className: string) => {
    /* Mocked data retrieved from LSP Service */
    const bookClassFieldsList = new Map<string, string>();
    bookClassFieldsList.set("author", "org.kie.test.kogito.Author");
    bookClassFieldsList.set("title", "java.lang.String");
    bookClassFieldsList.set("year", "java.lang.Integer");
    bookClassFieldsList.set("boom", "org.kie.test.kogito.Boom");
    const boomClassFieldsList = new Map<string, string>();
    boomClassFieldsList.set("time", "java.util.Date");
    boomClassFieldsList.set("big", "java.lang.Boolean");
    boomClassFieldsList.set("color", "java.lang.String");
    boomClassFieldsList.set("countdown", "java.time.Duration");
    const authorClassFieldsList = new Map<string, string>();
    authorClassFieldsList.set("age", "int");
    authorClassFieldsList.set("name", "java.lang.String");

    await delay();

    /* Temporary mocks managing */
    if (className === "org.kie.test.kogito.Book") {
      return bookClassFieldsList;
    } else if (className === "org.kie.test.kogito.Boom") {
      return boomClassFieldsList;
    } else if (className === "org.kie.test.kogito.Author") {
      return authorClassFieldsList;
    } else {
      return new Map<string, string>();
    }
  };

  const delay = () => new Promise((res) => setTimeout(res, Math.random() * (4000 - 750) + 1000));

  window.envelopeMock = {
    lspGetClassServiceMocked: (value: string) => lspGetClassServiceMocked(value),
    lspGetClassFieldsServiceMocked: (className: string) => lspGetClassFieldsServiceMocked(className),
  };

  return (
    <div className="showcase">
      <p>
        This showcase demonstrates how the <strong>Import Java Classes</strong> component works. Adding the component to
        the DOM, will result to render a Button with <em>Import Java Classes</em> label. As default status, the button
        is disabled with a tooltip reporting the reason. Using the above menu, you can modify the button status and the
        related tooltip message.
      </p>
      <p>
        To simulate the searching of a Java Classes on the Search box inside the wizard, please use values:
        <em>Boo</em>, <em>Boom</em> or <em>Book</em> as key, which are mocked in this showcase to demonstrate the
        component usage.
      </p>
      <div className="menu">
        <strong>Import Java classes button state</strong>
        <select onChange={onSelectChange}>
          <option value="true">Disabled</option>
          <option value="false">Enabled</option>
        </select>
        <strong>Tooltip Message (Optional)</strong>
        <input value={buttonTooltipMessage} onChange={onInputChange} />
      </div>
      <div className="main">
        <ImportJavaClasses buttonDisabledStatus={buttonDisableStatus} buttonTooltipMessage={buttonTooltipMessage} />
      </div>
    </div>
  );
};

ReactDOM.render(<Showcase />, document.getElementById("root"));
