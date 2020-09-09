/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { AllActions } from "./reducers/Actions";
import { PMMLReducer } from "./reducers/PMMLReducer";
import { HeaderReducer } from "./reducers/HeaderReducer";
import { DataDictionaryReducer } from "./reducers/DataDictionaryReducer";
import mergeReducers from "combine-reducer";
import { DataFieldReducer } from "./reducers/DataFieldReducer";

const pmml: PMML = {
  Header: {},
  DataDictionary: {
    DataField: []
  },
  version: "1.0"
};

interface EditorContext {
  state: PMML;
  dispatch: React.Dispatch<AllActions>;
}

const AppContext = React.createContext<EditorContext>({
  state: pmml,
  dispatch: () => null
});

const AppProvider: React.FC = ({ children }) => {
  const reducer = React.useMemo(
    () =>
      mergeReducers(PMMLReducer, {
        Header: HeaderReducer,
        DataDictionary: mergeReducers(DataDictionaryReducer, { DataField: DataFieldReducer })
      }),
    []
  );
  const [state, dispatch] = React.useReducer(reducer, pmml);
  const contextValue = React.useMemo(() => {
    return { state, dispatch };
  }, [state, dispatch]);

  return <AppContext.Provider value={contextValue}>{children}</AppContext.Provider>;
};

export { AppProvider, AppContext };
