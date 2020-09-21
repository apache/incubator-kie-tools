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
import { KogitoEditorChannelApi } from "@kogito-tooling/editor/dist/api";
import { MessageBusClientApi } from "@kogito-tooling/envelope-bus/dist/api";
import * as React from "react";
import { Reducer } from "react";
import { enableAllPlugins } from "immer";
import { createStore, Store } from "redux";
import { PMMLReducer } from "./reducers/PMMLReducer";
import { AllActions } from "./reducers/Actions";
import { PMML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Provider } from "react-redux";
import mergeReducers from "combine-reducer";
import { HeaderReducer } from "./reducers/HeaderReducer";
import { DataDictionaryReducer } from "./reducers/DataDictionaryReducer";
import { DataFieldReducer } from "./reducers/DataFieldReducer";
import { HistoryContext, HistoryService } from "./history/HistoryProvider";
import { HistoryLog, StateButtons } from "./components/mocked/MockHistoryUI";
import MockVersionUI from "./components/mocked/MockVersionUI";
import MockHeaderUI from "./components/mocked/MockHeaderUI";
import MockDataFieldsUI from "./components/mocked/MockDataFieldsUI";
import MockSummaryUI from "./components/mocked/MockSummaryUI";

const reducer: Reducer<PMML, AllActions> = mergeReducers(PMMLReducer, {
  Header: HeaderReducer,
  DataDictionary: mergeReducers(DataDictionaryReducer, { DataField: DataFieldReducer })
});

let store: Store<PMML, AllActions> | undefined;

export interface Props {
  exposing: (s: PMMLEditor) => void;
  channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
}

export interface State {
  path: string;
  content: string;
  originalContent: string;
}

export class PMMLEditor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      path: "",
      content: "",
      originalContent: ""
    };

    enableAllPlugins();
  }

  public componentDidMount(): void {
    this.props.channelApi.notifications.receive_ready();
  }

  public setContent(path: string, content: string): Promise<void> {
    return new Promise<void>(res => this.doSetContent(path, content));
  }

  private doSetContent(path: string, content: string): void {
    store = createStore(reducer, XML2PMML(content));
    this.setState({ path: path, content: content, originalContent: content });
  }

  public getContent(): Promise<string> {
    return new Promise<string>(res => this.state.content);
  }

  public render() {
    if (store) {
      return (
        <Provider store={store}>
          <HistoryContext.Provider value={{ service: new HistoryService() }}>
            <hr />
            <StateButtons />
            <hr />
            <MockVersionUI />
            <hr />
            <MockHeaderUI />
            <hr />
            <MockDataFieldsUI />
            <hr />
            <MockSummaryUI />
            <hr />
            <HistoryLog />
          </HistoryContext.Provider>
        </Provider>
      );
    } else {
      return <div>Content not set</div>;
    }
  }
}
