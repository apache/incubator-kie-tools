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
import { Reducer } from "react";
import { enableMapSet, enablePatches } from "immer";
import { createStore, Store } from "redux";
import {
  Actions,
  AllActions,
  DataDictionaryFieldReducer,
  DataDictionaryReducer,
  HeaderReducer,
  ModelReducer,
  PMMLReducer,
} from "./reducers";
import { Model, PMML, PMML2XML, XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { Provider } from "react-redux";
import mergeReducers from "combine-reducer";
import { HistoryContext, HistoryService } from "./history";
import { LandingPage } from "./components/LandingPage/templates";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { HashRouter, Navigate, Route, Routes } from "react-router-dom";
import { EmptyStateNoContent } from "./components/LandingPage/organisms";
import { SingleEditorRouter } from "./components/EditorCore/organisms";
import { PMMLModelMapping, PMMLModels, SupportedCapability } from "./PMMLModelHelper";
import { Operation, OperationContext } from "./components/EditorScorecard";
import { toNotifications, ValidationContext, ValidationRegistry } from "./validation";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Builder } from "./paths";

const EMPTY_PMML: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"><Header /><DataDictionary/></PMML>`;

interface Props {
  /**
   * Callback to the container so that it may bind to the PMMLEditor.
   *
   * @returns Instance of the PMMLEditor.
   */
  exposing: (s: PMMLEditor) => void;

  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_ready() to signal to the Channel
   * that the editor is ready. Increases the decoupling of the PMMLEditor from the Channel.
   */
  ready: () => void;

  /**
   * Delegation for WorkspaceChannelApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * that a change has taken place. Increases the decoupling of the PMMLEditor from the Channel.
   * @param edit An object representing the unique change.
   */
  newEdit: (edit: WorkspaceEdit) => void;

  /**
   * Delegation for NotificationsChannelApi.kogitoNotifications_setNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot, notifications) to report all validation
   * notifications to the Channel that  will replace existing notification for the path. Increases the
   * decoupling of the PMMLEditor from the Channel.
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]) => void;
}

export interface State {
  path: string;
  content: string;
  originalContent: string;
  activeOperation: Operation;
}

export class PMMLEditor extends React.Component<Props, State> {
  private store: Store<PMML, AllActions> | undefined;
  private readonly history: HistoryService = new HistoryService([
    (id: string) => {
      this.props.newEdit(new WorkspaceEdit(id));
    },
    () => {
      this.props.setNotifications(this.state.path, this.validate());
    },
  ]);
  private readonly validationRegistry: ValidationRegistry = new ValidationRegistry();
  private readonly reducer: Reducer<PMML, AllActions>;

  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      path: "",
      content: "",
      originalContent: "",
      activeOperation: Operation.NONE,
    };

    enableMapSet();
    enablePatches();

    this.reducer = mergeReducers(PMMLReducer(this.history, this.validationRegistry), {
      Header: HeaderReducer(this.history),
      DataDictionary: mergeReducers(DataDictionaryReducer(this.history, this.validationRegistry), {
        DataField: DataDictionaryFieldReducer(this.history, this.validationRegistry),
      }),
      models: ModelReducer(this.history, this.validationRegistry),
    });
  }

  public componentDidMount(): void {
    this.props.ready();
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    try {
      this.doSetContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content);
      this.props.setNotifications(this.state.path, this.validate());
      return Promise.resolve();
    } catch (e) {
      console.error(e);
      return Promise.reject();
    }
  }

  private doSetContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): void {
    let pmml: PMML;
    let _content: string = content;

    if (content === "") {
      _content = EMPTY_PMML;
      pmml = XML2PMML(_content);

      //If there is only one supported type of model then create a default entry
      const supportedEditorTypes: Array<PMMLModelMapping<any>> = PMMLModels.filter(
        (m) => m.capability === SupportedCapability.EDITOR
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

    //Create and validate store before setting state to ensure Validation is complete before the UI renders
    this.store = createStore(this.reducer, pmml);
    this.store?.dispatch({
      type: Actions.Validate,
      payload: {},
    });

    this.setState({
      path: normalizedPosixPathRelativeToTheWorkspaceRoot,
      content: _content,
      originalContent: _content,
      activeOperation: Operation.NONE,
    });
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
        payload: undefined,
      });
      this.store?.dispatch({
        type: Actions.Validate,
        payload: {},
      });
      this.props.setNotifications(this.state.path, this.validate());
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
        payload: undefined,
      });
      this.store?.dispatch({
        type: Actions.Validate,
        payload: {},
      });
      this.props.setNotifications(this.state.path, this.validate());
    }
  }

  public validate(): Notification[] {
    return toNotifications(this.state.path, this.validationRegistry.get(Builder().build()));
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
              <ValidationContext.Provider value={{ validationRegistry: this.validationRegistry }}>
                <HistoryContext.Provider
                  value={{
                    service: this.history,
                    getCurrentState: () => this.store?.getState(),
                  }}
                >
                  <Routes>
                    <Route
                      path={"/"}
                      element={
                        <>
                          {!isSingleModel && <LandingPage path={path} />}
                          {isSingleModel && <Navigate replace to={"/editor/0"} />}
                        </>
                      }
                    />
                    <Route
                      path={"/editor/:index"}
                      element={
                        <OperationContext.Provider
                          value={{
                            activeOperation: this.state.activeOperation,
                            setActiveOperation: (operation) =>
                              this.setState({
                                ...this.state,
                                activeOperation: operation,
                              }),
                          }}
                        >
                          <SingleEditorRouter path={path} />
                        </OperationContext.Provider>
                      }
                    />
                  </Routes>
                </HistoryContext.Provider>
              </ValidationContext.Provider>
            </Provider>
          </Page>
        </HashRouter>
      );
    } else {
      return <EmptyStateNoContent />;
    }
  }
}
