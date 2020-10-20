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
import { Actions, AllActions, DataDictionaryReducer, DataFieldReducer, HeaderReducer, PMMLReducer } from "./reducers";
import { PMML, PMML2XML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Provider } from "react-redux";
import mergeReducers from "combine-reducer";
import { HistoryService } from "./history";
import { LandingPage } from "./components/LandingPage/templates";
import { Page } from "@patternfly/react-core";
import { HashRouter } from "react-router-dom";
import { Route, Switch } from "react-router";
import { EmptyStateNoContent } from "./components/LandingPage/organisms";

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
  private store: Store<PMML, AllActions> | undefined;
  private readonly service: HistoryService = new HistoryService();
  private readonly reducer: Reducer<PMML, AllActions>;

  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      path: "",
      content: "",
      originalContent: ""
    };

    enableAllPlugins();

    this.reducer = mergeReducers(PMMLReducer(this.service), {
      Header: HeaderReducer(this.service),
      DataDictionary: mergeReducers(DataDictionaryReducer(this.service), { DataField: DataFieldReducer(this.service) })
    });
  }

  public componentDidMount(): void {
    this.props.channelApi.notifications.receive_ready();
  }

  public setContent(path: string, content: string): Promise<void> {
    return Promise.resolve(this.doSetContent(path, content));
  }

  private doSetContent(path: string, content: string): void {
    this.store = createStore(this.reducer, XML2PMML(content));
    this.setState({ path: path, content: content, originalContent: content });
  }

  public getContent(): Promise<string> {
    return Promise.resolve(this.doGetContent());
  }

  private doGetContent(): string {
    const pmml: PMML | undefined = this.store?.getState();
    return pmml ? PMML2XML(pmml) : "";
  }

  public async undo(): Promise<void> {
    return Promise.resolve(this.doUndo());
  }

  private doUndo(): void {
    const pmml: PMML | undefined = this.store?.getState();
    if (pmml !== undefined) {
      this.store?.dispatch({
        type: Actions.Undo,
        payload: undefined
      });
    }
  }

  public async redo(): Promise<void> {
    return Promise.resolve(this.doRedo());
  }

  private doRedo(): void {
    const pmml: PMML | undefined = this.store?.getState();
    if (pmml !== undefined) {
      this.store?.dispatch({
        type: Actions.Redo,
        payload: undefined
      });
    }
  }

  public render() {
    if (this.store) {
      return (
        <HashRouter>
          <Page>
            <Provider store={this.store}>
              <Switch>
                <Route exact={true} path={"/"}>
                  <LandingPage path={this.state.path} />
                </Route>
              </Switch>
            </Provider>
          </Page>
        </HashRouter>
      );
    } else {
      return <EmptyStateNoContent />;
    }
  }
}
