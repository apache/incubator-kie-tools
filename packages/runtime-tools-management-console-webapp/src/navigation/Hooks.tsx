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
import { useContext, useEffect, useMemo } from "react";
import { BlockerDelegate, NavigationBlockerContext, NavigationStatusContext } from "./NavigationContextProvider";
import { routes } from "./Routes";

function useNavigationBlockerContext() {
  return useContext(NavigationBlockerContext);
}

export function useNavigationStatus() {
  return useContext(NavigationStatusContext);
}

export function useNavigationBlockersBypass() {
  const { bypass } = useNavigationBlockerContext();
  return useMemo(() => ({ execute: bypass }), [bypass]);
}

export function useNavigationStatusToggle() {
  const { unblock } = useNavigationBlockerContext();
  return useMemo(() => ({ unblock }), [unblock]);
}

export function useNavigationBlocker(key: string, blocker: BlockerDelegate) {
  const { addBlocker, removeBlocker } = useNavigationBlockerContext();

  useEffect(() => {
    addBlocker(key, blocker);
    return () => removeBlocker(key);
  }, [addBlocker, removeBlocker, key, blocker]);
}

export function useRoutes() {
  return useMemo(() => routes, []);
}
