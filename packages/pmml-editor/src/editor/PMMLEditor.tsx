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
import {
  Actions,
  AllActions,
  DataDictionaryFieldReducer,
  DataDictionaryReducer,
  HeaderReducer,
  ModelReducer,
  PMMLReducer
} from "./reducers";
import { Model, PMML, PMML2XML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Provider } from "react-redux";
import mergeReducers from "combine-reducer";
import { HistoryService } from "./history";
import { LandingPage } from "./components/LandingPage/templates";
import { Page } from "@patternfly/react-core";
import { HashRouter } from "react-router-dom";
import { Redirect, Route, Switch } from "react-router";
import { EmptyStateNoContent } from "./components/LandingPage/organisms";
import { SingleEditorRouter } from "./components/EditorCore/organisms";
import { PMMLModelMapping, PMMLModels, SupportedCapability } from "./PMMLModelHelper";
import { Operation } from "./components/EditorScorecard";

const EMPTY_PMML: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"><Header /><DataDictionary/></PMML>`;

interface Props {
  exposing: (s: PMMLEditor) => void;
  channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
}

export interface State {
  path: string;
  content: string;
  originalContent: string;
  activeOperation: Operation;
}

interface History {
  service: HistoryService;
  getCurrentState: () => PMML | undefined;
}

export const HistoryContext = React.createContext<History>({
  service: new HistoryService(),
  getCurrentState: () => undefined
});

interface ActiveOperation {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
}

export const OperationContext = React.createContext<ActiveOperation>({
  activeOperation: Operation.NONE,
  setActiveOperation: (operation: Operation) => null
});

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
      originalContent: "",
      activeOperation: Operation.NONE
    };

    enableAllPlugins();

    this.reducer = mergeReducers(PMMLReducer(this.service), {
      Header: HeaderReducer(this.service),
      DataDictionary: mergeReducers(DataDictionaryReducer(this.service), {
        DataField: DataDictionaryFieldReducer(this.service)
      }),
      models: ModelReducer(this.service)
    });
  }

  public componentDidMount(): void {
    this.props.channelApi.notifications.receive_ready();
  }

  public setContent(path: string, content: string): Promise<void> {
    return Promise.resolve(this.doSetContent(path, content));
  }

  private doSetContent(path: string, content: string): void {
    let pmml: PMML;
    let _content: string = content;

    if (content === "") {
      _content = EMPTY_PMML;
      pmml = XML2PMML(_content);

      //If there is only one supported type of model then create a default entry
      const supportedEditorTypes: Array<PMMLModelMapping<any>> = PMMLModels.filter(
        m => m.capability === SupportedCapability.EDITOR
      );
      if (content === "" && supportedEditorTypes.length === 1) {
        const factory = supportedEditorTypes[0].factory;
        if (factory) {
          pmml.models = [factory()];
        }
      }
    } else {
      pmml = XML2PMML(_content);
    }

    this.store = createStore(this.reducer, pmml);
    this.setState({ path: path, content: _content, originalContent: _content, activeOperation: Operation.NONE });
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

  private isSingleModel(): boolean {
    if (!this.store) {
      return false;
    }
    const models: Model[] | undefined = this.store.getState().models;
    if (models !== undefined) {
      if (models.length === 1) {
        return true;
      }
    }
    return false;
  }

  public render() {
    const isSingleModel: boolean = this.isSingleModel();

    if (this.store) {
      const path: string = this.state.path;
      return (
        <HashRouter>
          <Page>
            <Provider store={this.store}>
              <Switch>
                <Route exact={true} path={"/"}>
                  {!isSingleModel && <LandingPage path={path} />}
                  {isSingleModel && <Redirect from={"/"} to={"/editor/0"} />}
                </Route>
                <Route exact={true} path={"/editor/:index"}>
                  <OperationContext.Provider
                    value={{
                      activeOperation: this.state.activeOperation,
                      setActiveOperation: operation =>
                        this.setState({
                          ...this.state,
                          activeOperation: operation
                        })
                    }}
                  >
                    <HistoryContext.Provider
                      value={{
                        service: this.service,
                        getCurrentState: () => this.store?.getState()
                      }}
                    >
                      <SingleEditorRouter path={path} />
                    </HistoryContext.Provider>
                  </OperationContext.Provider>
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
