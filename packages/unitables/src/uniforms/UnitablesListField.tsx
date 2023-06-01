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
import { ReactNode, useMemo } from "react";
import { HTMLFieldProps } from "uniforms";
import UnitablesListItemField from "./UnitablesListItemField";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import { connectField, filterDOMProps } from "uniforms/esm";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";
import { ListAddField } from "@kie-tools/uniforms-patternfly/dist/esm";

export type UnitablesListFieldProps = HTMLFieldProps<
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

function UnitablesListField({
  children = <UnitablesListItemField name={"$"} />,
  itemProps,
  ...props
}: UnitablesListFieldProps) {
  const hasValue = useMemo(() => props.value && Array.isArray(props.value) && props.value.length > 0, [props.value]);

  return wrapField(
    props as any,
    <div data-testid={"unitables-list-field"} {...filterDOMProps(props)} style={{ display: "flex", width: "100%" }}>
      <Split hasGutter={hasValue} style={!hasValue ? { width: "100%" } : {}}>
        {props.label && (
          <>
            <SplitItem>
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
            </SplitItem>
            <SplitItem isFilled={true} />
          </>
        )}
        <SplitItem
          style={{
            borderRight: "3px solid var(--pf-global--palette--black-300)",
          }}
        >
          <ListAddField
            style={{
              minWidth: "60px",
              maxWidth: "60px",
            }}
            name={"$"}
            initialCount={props.initialCount}
          />
        </SplitItem>
        {!hasValue && (
          <SplitItem style={{ width: "100%" }}>
            <TextInput aria-label={"Add inputs placeholder"} isDisabled={true} value={"Add inputs"} />
          </SplitItem>
        )}
      </Split>

      {hasValue &&
        props.value!.map((_, itemIndex) => {
          return (
            <div key={itemIndex} style={{ display: "flex", width: "100%" }}>
              <div
                style={{
                  maxWidth: "60px",
                  minWidth: "60px",
                  minHeight: "60px",
                  fontSize: "16px",
                  color: "gray",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  borderRight: "1px solid lightgray",
                }}
              >
                {itemIndex}
              </div>
              {React.isValidElement(children)
                ? React.cloneElement(
                    children as React.ReactElement<{ name: string; style: React.CSSProperties }, string>,
                    {
                      key: `${itemIndex}`,
                      name: children.props.name
                        ?.split(/\$(.*)/s)
                        .slice(0, -1)
                        .join(`${itemIndex}`),
                      ...itemProps,
                      style:
                        props.value!.length - 1 !== itemIndex
                          ? { width: "100%", borderRight: "3px solid var(--pf-global--palette--black-300)" }
                          : { width: "100%" },
                    }
                  )
                : children}
            </div>
          );
        })}
    </div>
  );
}

export default connectField(UnitablesListField);
