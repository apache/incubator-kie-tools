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

import * as React from "react";
import * as ReactDOM from "react-dom";
import "@patternfly/react-core/dist/styles/base.css";
import "./index.css";
import { ImportJavaClasses, GWTLayerService, JavaCodeCompletionService } from "../src";

const Showcase: React.FunctionComponent = () => {
  const getJavaCodeCompletionClassesMock = async (value: string) => {
    const booClassesList = [{ fqcn: "org.kie.test.kogito.Book" }, { fqcn: "org.kie.test.kogito.Boom" }];
    const bookClassesList = [{ fqcn: "org.kie.test.kogito.Book" }];
    const boomClassesList = [{ fqcn: "org.kie.test.kogito.Boom" }];

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
      { fqcn: "com.Book", accessor: "author", type: "org.kie.test.kogito.Author" },
      { fqcn: "com.Book", accessor: "title", type: "java.lang.String" },
      { fqcn: "com.Book", accessor: "year", type: "java.lang.Integer" },
      { fqcn: "com.Book", accessor: "boom", type: "org.kie.test.kogito.Boom" },
      { fqcn: "com.Book", accessor: "serialVersionUID", type: "long" },
      { fqcn: "com.Book", accessor: "getClass()", type: "java.lang.Class<?>" },
      { fqcn: "com.Book", accessor: "getOtherBookList()", type: "java.util.List<java.lang.String>" },
      { fqcn: "com.Book", accessor: "getTopicsMap()", type: "java.util.Map<java.lang.String, java.lang.String>" },
      { fqcn: "com.Boom", accessor: "isAvailable()", type: "java.lang.Boolean" },
    ];
    const boomClassFieldsList = [
      { fqcn: "com.Boom", accessor: "time", type: "java.util.Date" },
      { fqcn: "com.Boom", accessor: "big", type: "java.lang.Boolean" },
      { fqcn: "com.Boom", accessor: "color", type: "java.lang.String" },
      { fqcn: "com.Boom", accessor: "countdown", type: "java.time.Duration" },
    ];
    const authorClassFieldsList = [
      { fqcn: "com.Author", accessor: "age", type: "int" },
      { fqcn: "com.Author", accessor: "name", type: "java.lang.String" },
      { fqcn: "com.Author", accessor: "color", type: "java.lang.String" },
      { fqcn: "com.Author", accessor: "countdown", type: "java.time.Duration" },
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
