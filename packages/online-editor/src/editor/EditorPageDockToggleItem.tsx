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
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { useEditorDockContext } from "./EditorPageDockContextProvider";

interface Props {
  // required by Tooltip
  children: React.ReactElement;
}

export function EditorPageDockToggleItem(props: Props) {
  const { isDisabled, disabledReason } = useEditorDockContext();

  return (
    <>
      {isDisabled ? (
        <Tooltip key={"disabled"} content={disabledReason} position={"top-end"}>
          {props.children}
        </Tooltip>
      ) : (
        props.children
      )}
    </>
  );
}
