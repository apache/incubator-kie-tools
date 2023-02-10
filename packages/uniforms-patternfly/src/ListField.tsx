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
import { Children, cloneElement, isValidElement, ReactNode } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";
import ListItemField from "./ListItemField";
import ListAddField from "./ListAddField";

export type ListFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    children?: ReactNode;
    info?: string;
    error?: boolean;
    initialCount?: number;
    itemProps?: object;
    showInlineError?: boolean;
  }
>;

declare module "uniforms" {
  interface FilterDOMProps {
    wrapperCol: never;
    labelCol: never;
  }
}

filterDOMProps.register("minCount", "wrapperCol", "labelCol");

function ListField({
  children = <ListItemField name="$" />,
  error,
  errorMessage,
  info,
  initialCount,
  itemProps,
  label,
  name,
  value,
  showInlineError,
  ...props
}: ListFieldProps) {
  return (
    <div data-testid={"list-field"} {...filterDOMProps(props)}>
      <Split hasGutter>
        <SplitItem>
          {label && (
            <label>
              {label}
              {!!info && (
                <span>
                  &nbsp;
                  <Tooltip content={info}>
                    <OutlinedQuestionCircleIcon />
                  </Tooltip>
                </span>
              )}
            </label>
          )}
        </SplitItem>
        <SplitItem isFilled />
        <SplitItem>
          <ListAddField name={"$"} initialCount={initialCount} />
        </SplitItem>
      </Split>

      <div>
        {value?.map((item, itemIndex) =>
          Children.map(children, (child, childIndex) =>
            isValidElement(child)
              ? cloneElement(child as React.ReactElement<{ name: string }, string>, {
                  key: `${itemIndex}-${childIndex}`,
                  name: child.props.name
                    ?.split(/\$(.*)/s)
                    .slice(0, -1)
                    .join(`${itemIndex}`),
                  ...itemProps,
                })
              : child
          )
        )}
      </div>
    </div>
  );
}

export default connectField(ListField);
