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
import { ReactNode } from "react";
import { HTMLFieldProps } from "uniforms";
import UniformsListItemField from "./UniformsListItemField";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import { connectField, filterDOMProps } from "uniforms/esm";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";
import { ListAddField } from "@kie-tools/uniforms-patternfly/dist/esm";

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

function UniformsListField({ children = <UniformsListItemField name={"$"} />, itemProps, ...props }: ListFieldProps) {
  return wrapField(
    props as any,
    <div data-testid={"unitables-list-field"} {...filterDOMProps(props)} style={{ display: "flex" }}>
      <Split hasGutter>
        <SplitItem>
          {props.label && (
            <label>
              {props.label}
              {!!props.info && (
                <span>
                  &nbsp;
                  <Tooltip content={props.info}>
                    <OutlinedQuestionCircleIcon />
                  </Tooltip>
                </span>
              )}
            </label>
          )}
        </SplitItem>
        <SplitItem isFilled />
        <SplitItem>
          <ListAddField name={"$"} initialCount={props.initialCount} />
        </SplitItem>
      </Split>

      {props.value?.map((item, itemIndex) =>
        React.Children.map(children, (child, childIndex) =>
          React.isValidElement(child)
            ? React.cloneElement(child as React.ReactElement<{ name: string }, string>, {
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
  );
}

export default connectField(UniformsListField);
