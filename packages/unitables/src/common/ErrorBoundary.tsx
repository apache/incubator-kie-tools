/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { ErrorInfo } from "react";

interface State {
  hasError: boolean;
}

interface Props {
  children: React.ReactNode;
  error: React.ReactNode;
  setHasError: React.Dispatch<boolean>;
}

export class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: any) {
    super(props);
    this.state = { hasError: false };
  }

  public reset() {
    this.props.setHasError(false);
    this.setState({ hasError: false });
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    this.props.setHasError(true);
    console.error("Error", error, errorInfo);
  }

  public render() {
    if (this.state.hasError) {
      return this.props.error;
    }

    return this.props.children;
  }

  public static getDerivedStateFromError(error: Error) {
    return { hasError: true };
  }
}
