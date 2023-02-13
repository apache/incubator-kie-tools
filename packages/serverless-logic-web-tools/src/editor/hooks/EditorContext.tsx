/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import { useContext, useMemo, useState } from "react";

export interface EditorContextType {
  notifications: Notification[];
}

export interface EditorDispatchContextType {
  setNotifications: React.Dispatch<React.SetStateAction<Notification[]>>;
}

export const EditorContext = React.createContext<EditorContextType>({} as any);
export const EditorDispatchContext = React.createContext<EditorDispatchContextType>({} as any);

export function EditorContextProvider(props: React.PropsWithChildren<{}>) {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const value = useMemo(() => ({ notifications }), [notifications]);
  const dispatch = useMemo(() => ({ setNotifications }), [setNotifications]);
  return (
    <EditorContext.Provider value={value}>
      <EditorDispatchContext.Provider value={dispatch}>{props.children}</EditorDispatchContext.Provider>
    </EditorContext.Provider>
  );
}

export function useEditor() {
  return useContext(EditorContext);
}

export function useEditorDispatch() {
  return useContext(EditorDispatchContext);
}
