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

export function EdgeMarkers() {
  return (
    <defs>
      <marker
        id="closed-circle-at-center"
        viewBox="0 0 10 10"
        refX={5}
        refY={5}
        markerUnits="userSpaceOnUse"
        markerWidth="8"
        markerHeight="8"
        orient="auto-start-reverse"
      >
        <circle cx="5" cy="5" r="5" fill="context-fill" stroke="context-stroke" />
      </marker>
      <marker
        id="closed-circle-at-border"
        viewBox="0 0 10 10"
        refX={10}
        refY={5}
        markerUnits="userSpaceOnUse"
        markerWidth="8"
        markerHeight="8"
        orient="auto-start-reverse"
      >
        <circle cx="5" cy="5" r="5" fill="context-fill" stroke="context-stroke" />
      </marker>
      <marker
        id="closed-arrow"
        viewBox="0 0 10 10"
        refX={10}
        refY={5}
        markerUnits="userSpaceOnUse"
        markerWidth="8"
        markerHeight="8"
        orient="auto-start-reverse"
      >
        <path d="M 0 0 L 10 5 L 0 10 z" fill="context-fill" stroke="context-stroke" />
      </marker>
      <marker
        id="open-arrow"
        viewBox="0 0 10 10"
        refX={10}
        refY={5}
        markerUnits="userSpaceOnUse"
        markerWidth="8"
        markerHeight="8"
        orient="auto-start-reverse"
      >
        <path d="M 0,0 L 10,5 M 10,5 L 0,10" stroke="black" />
      </marker>
    </defs>
  );
}
