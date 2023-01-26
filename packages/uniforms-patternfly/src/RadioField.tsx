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
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";
import wrapField from "./wrapField";

export type RadioFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    transform?: (string?: string) => string;
    allowedValues: string[];
    onChange: (value: string) => void;
    value?: string;
    disabled?: boolean;
  }
>;

function RadioField(props: RadioFieldProps) {
  filterDOMProps.register("checkboxes", "decimal");
  return wrapField(
    props,
    <div data-testid={"radio-field"} {...filterDOMProps(props)}>
      {props.allowedValues?.map((item) => (
        <React.Fragment key={item}>
          <Radio
            isChecked={item === props.value}
            isDisabled={props.disabled}
            id={`${props.id}`}
            name={props.name}
            label={props.transform ? props.transform(item) : item}
            aria-label={props.name}
            onChange={() => props.onChange(item)}
          />
        </React.Fragment>
      ))}
    </div>
  );
}

export default connectField(RadioField);
