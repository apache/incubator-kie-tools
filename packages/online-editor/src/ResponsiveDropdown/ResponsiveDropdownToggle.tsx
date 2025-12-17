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

import React from "react";
import { DropdownToggle, DropdownToggleProps } from "@patternfly/react-core/deprecated";
import { useResponsiveDropdownContext } from "./ResponsiveDropdownContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

export function ResponsiveDropdownToggle(props: DropdownToggleProps) {
  const { isModal } = useResponsiveDropdownContext();
  return (
    <>
      {isModal ? (
        <Button
          variant={props.isPlain ? ButtonVariant.plain : ButtonVariant.primary}
          onClick={(e) => {
            props.onToggle?.(e, props.isOpen ?? false);
            props.onClick?.(e);
          }}
          className={props.className}
        >
          {props.icon ?? props.children}
        </Button>
      ) : (
        <DropdownToggle {...props} style={{ ...(props.style ?? {}), display: "flex", flexWrap: "nowrap" }} />
      )}
    </>
  );
}
