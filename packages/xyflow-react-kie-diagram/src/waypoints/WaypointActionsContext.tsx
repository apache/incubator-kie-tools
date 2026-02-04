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

import React, { useContext } from "react";
import { DC__Point } from "../maths/model";

export interface WaypointActionsContextType {
  onWaypointAdded: (args: { beforeIndex: number; waypoint: DC__Point; edgeIndex: number }) => void;
  onWaypointDeleted: (args: { edgeIndex: number; waypointIndex: number }) => void;
  onWaypointRepositioned: (args: { edgeIndex: number; waypointIndex: number; waypoint: DC__Point }) => void;
}

const WaypointActionsContext = React.createContext<WaypointActionsContextType>({} as any);

export function useWaypointsActions() {
  return useContext(WaypointActionsContext);
}

export function WaypointActionsContextProvider(props: React.PropsWithChildren<{ value: WaypointActionsContextType }>) {
  return <WaypointActionsContext.Provider value={props.value}>{props.children}</WaypointActionsContext.Provider>;
}
