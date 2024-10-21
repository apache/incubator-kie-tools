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
import { HTMLProps } from "react";
import { useForm, filterDOMProps } from "uniforms";

// Define the type for the style prop
type Style = React.CSSProperties;

// Define the ErrorsFieldProps without defaultProps
export type ErrorsFieldProps = HTMLProps<HTMLDivElement> & {
  style?: Style;
};

const defaultStyle: Style = {
  backgroundColor: "rgba(255, 85, 0, 0.2)",
  border: "1px solid rgb(255, 85, 0)",
  borderRadius: "7px",
  margin: "20px 0px",
  padding: "10px",
};

function ErrorsField({ children, style = {}, ...props }: ErrorsFieldProps): React.ReactElement | null {
  const { error, schema } = useForm();

  if (!error && !children) {
    return null;
  }

  // Merge default styles with any styles passed via props
  const combinedStyle: Style = { ...defaultStyle, ...style };

  return (
    <div data-testid="errors-field" style={combinedStyle} {...filterDOMProps(props)}>
      {children}
      <ul>
        {schema.getErrorMessages(error).map((message, index) => (
          <li key={index} style={{ margin: "3px" }}>
            {message}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ErrorsField;
