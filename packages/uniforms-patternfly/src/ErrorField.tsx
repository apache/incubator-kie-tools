/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { HTMLProps } from "react";
import { connectField, filterDOMProps, Override } from "uniforms";

export type ErrorFieldProps = Override<
  HTMLProps<HTMLDivElement>,
  {
    error?: boolean;
    errorMessage?: string;
  }
>;

function ErrorField({ children, error, errorMessage, ...props }: ErrorFieldProps) {
  return !error ? null : (
    <div data-testid={"error-field"} {...filterDOMProps(props)}>
      {children ? children : <div style={{ margin: "3px" }}>{errorMessage}</div>}
    </div>
  );
}

ErrorField.defaultProps = {
  style: {
    backgroundColor: "rgba(255, 85, 0, 0.2)",
    border: "1px solid rgb(255, 85, 0)",
    borderRadius: "7px",
    margin: "20px 0px",
    padding: "10px",
  },
};

export default connectField<ErrorFieldProps>(ErrorField, { initialValue: false });
