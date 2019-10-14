/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Router } from "@kogito-tooling/core-api";
import { GlobalContext } from "./GlobalContext";
import * as dependencies from "../../dependencies";
import { GitHubPageType } from "../../github/GitHubPageType";

interface Props {
  router: Router;
  pageType: GitHubPageType;
  editorIndexPath: string;
}

export class Main extends React.Component<Props, {}> {
  constructor(props: Props) {
    super(props);
  }

  private getSingleDependencyType() {
    if (this.props.pageType === GitHubPageType.EDIT) {
      return dependencies.singleEdit;
    }

    if (this.props.pageType === GitHubPageType.VIEW) {
      return dependencies.singleView;
    }

    if (this.props.pageType === GitHubPageType.PR) {
      return dependencies.prView;
    }

    return undefined as any;
  }

  public render() {
    const common = this.getSingleDependencyType();

    return (
      <GlobalContext.Provider
        value={{
          dependencies: { common: {...common, ...dependencies.common}, prView: dependencies.prView },
          router: this.props.router,
          editorIndexPath: this.props.editorIndexPath
        }}
      >
        {this.props.children}
      </GlobalContext.Provider>
    );
  }
}
