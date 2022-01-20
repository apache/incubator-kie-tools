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
import "@patternfly/react-core/dist/styles/base.css";
import "./index.css";
import { ImportJavaClasses, GWTLayerService, JavaCodeCompletionService } from "../src";

const Showcase: React.FunctionComponent = () => {
  const getJavaCodeCompletionClassesMock = async (value: string) => {
    const booClassesList = [{ query: "org.kie.test.kogito.Book" }, { query: "org.kie.test.kogito.Boom" }];
    const bookClassesList = [{ query: "org.kie.test.kogito.Book" }];
    const boomClassesList = [{ query: "org.kie.test.kogito.Boom" }];

    await delay();

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

  const getJavaCodeCompletionFieldsMock = async (className: string) => {
    const bookClassFieldsList = [
      { fqcn: "org.kie.test.kogito.Author", accessor: "author" },
      { fqcn: "java.lang.String", accessor: "title" },
      { fqcn: "java.lang.Integer", accessor: "year" },
      { fqcn: "org.kie.test.kogito.Boom", accessor: "boom" },
    ];
    const boomClassFieldsList = [
      { fqcn: "java.util.Date", accessor: "time" },
      { fqcn: "java.lang.Boolean", accessor: "big" },
      { fqcn: "java.lang.String", accessor: "color" },
      { fqcn: "java.time.Duration", accessor: "countdown" },
    ];
    const authorClassFieldsList = [
      { fqcn: "int", accessor: "age" },
      { fqcn: "java.lang.String", accessor: "name" },
      { fqcn: "java.lang.String", accessor: "color" },
      { fqcn: "java.time.Duration", accessor: "countdown" },
    ];

    await delay();

    /* Temporary mocks managing */
    if (className === "org.kie.test.kogito.Book") {
      return bookClassFieldsList;
    } else if (className === "org.kie.test.kogito.Boom") {
      return boomClassFieldsList;
    } else if (className === "org.kie.test.kogito.Author") {
      return authorClassFieldsList;
    } else {
      return [];
    }
  };

  const isLanguageServerAvailableMock = async () => {
    await delay();
    return Math.random() < 0.75;
  };

  const delay = () => new Promise((res) => setTimeout(res, Math.random() * (3000 - 500) + 1000));

  const gwtLayerService: GWTLayerService = {
    importJavaClassesInDataTypeEditor: (javaClasses) =>
      window.alert("Java Classes sent to editor:" + javaClasses.length),
  };

  const javaCodeCompletionService: JavaCodeCompletionService = {
    getClasses: getJavaCodeCompletionClassesMock,
    getFields: getJavaCodeCompletionFieldsMock,
    isLanguageServerAvailable: isLanguageServerAvailableMock,
  };

  return (
    <div className="showcase">
      <p>
        This showcase demonstrates how the <strong>Import Java Classes</strong> component works. To simulate Button
        Enabled/Disabled status, which is managed by the <strong>JavaCodeCompletionService</strong>, this showcase will
        randomy enable (75%) or disable (25%) the button.
      </p>
      <p>
        To simulate the searching of a Java Classes on the Search box inside the wizard, please use values:
        <em>Boo</em>, <em>Boom</em> or <em>Book</em> as key, which are mocked in this showcase to demonstrate the
        component usage.
      </p>
      <div className="main">
        <ImportJavaClasses gwtLayerService={gwtLayerService} javaCodeCompletionService={javaCodeCompletionService} />
      </div>
    </div>
  );
};

ReactDOM.render(<Showcase />, document.getElementById("root"));
