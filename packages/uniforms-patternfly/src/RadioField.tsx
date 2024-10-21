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

import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { Fragment } from "react";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";
import { TransformFn } from "./SelectField.types";
import wrapField, { WrapFieldProps } from "./wrapField";

export type RadioFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    transform?: TransformFn;
    options: string[];
    onChange: (value: string) => void;
    value?: string;
    disabled?: boolean;
  } & WrapFieldProps
>;

function RadioField(props: RadioFieldProps) {
  filterDOMProps.register("checkboxes", "decimal");

  return wrapField(
    props,
    <div data-testid={"radio-field"} {...filterDOMProps(props)}>
      {props.options?.map((item) => (
        <Fragment key={item}>
          <Radio
            isChecked={item === props.value}
            isDisabled={props.disabled}
            id={`${props.id}`}
            name={props.name}
            label={props.transform ? props.transform(item).label : item}
            aria-label={props.name}
            onChange={() => props.onChange(item)}
          />
        </Fragment>
      ))}
    </div>
  );
}

export default connectField(RadioField);
