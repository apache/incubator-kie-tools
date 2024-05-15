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
import { useContext, useRef } from "react";

export interface Commands {
  hideFromDrd: () => void;
  toggleHierarchyHighlight: () => void;
  togglePropertiesPanel: () => void;
  createGroup: () => void;
  selectAll: () => void;
  panDown: () => void;
  panUp: () => void;
  paste: () => void;
  copy: () => void;
  cut: () => void;
  cancelAction: () => void;
  focusOnSelection: () => void;
  resetPosition: () => void;
  redo: () => void;
  undo: () => void;
}

const CommandsContext = React.createContext<{
  commandsRef: React.MutableRefObject<Commands>;
}>({} as any);

export function useCommands() {
  return useContext(CommandsContext);
}

export function CommandsContextProvider(props: React.PropsWithChildren<{}>) {
  const commandsRef = useRef<Commands>({
    hideFromDrd: () => {
      throw new Error("DMN EDITOR: hideFromDrd command not implemented.");
    },
    toggleHierarchyHighlight: () => {
      throw new Error("DMN EDITOR: toggleHierarchyHighlight command not implemented.");
    },
    togglePropertiesPanel: () => {
      throw new Error("DMN EDITOR: togglePropertiesPanel command not implemented.");
    },
    createGroup: () => {
      throw new Error("DMN EDITOR: createGroup command not implemented.");
    },
    selectAll: () => {
      throw new Error("DMN EDITOR: selectAll command not implemented.");
    },
    panDown: () => {
      throw new Error("DMN EDITOR: panDown command not implemented.");
    },
    panUp: () => {
      throw new Error("DMN EDITOR: panUp command not implemented.");
    },
    paste: () => {
      throw new Error("DMN EDITOR: paste command not implemented.");
    },
    copy: () => {
      throw new Error("DMN EDITOR: copy command not implemented.");
    },
    cut: () => {
      throw new Error("DMN EDITOR: cut command not implemented.");
    },
    cancelAction: () => {
      throw new Error("DMN EDITOR: cancelAction command not implemented.");
    },
    focusOnSelection: () => {
      throw new Error("DMN EDITOR: focusOnSelection command not implemented.");
    },
    resetPosition: () => {
      throw new Error("DMN EDITOR: resetPosition command not implemented.");
    },
    redo: () => {
      throw new Error("DMN EDITOR: redo command not implemented.");
    },
    undo: () => {
      throw new Error("DMN EDITOR: undo command not implemented.");
    },
  });

  return <CommandsContext.Provider value={{ commandsRef }}>{props.children}</CommandsContext.Provider>;
}
