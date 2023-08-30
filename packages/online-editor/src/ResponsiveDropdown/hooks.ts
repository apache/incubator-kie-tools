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

import { useEffect, useState } from "react";
import {
  Breakpoint,
  RelationToBreakpoint,
  responsiveBreakpoints,
} from "../responsiveBreakpoints/ResponsiveBreakpoints";

export function useWindowWidth() {
  const [width, setWidth] = useState(() => window.innerWidth);

  useEffect(() => {
    let task: ReturnType<typeof setTimeout>;
    const refreshWidth = () => {
      clearTimeout(task);
      task = setTimeout(() => setWidth(window.innerWidth), 100);
    };

    window.addEventListener("resize", refreshWidth);
    return () => {
      window.removeEventListener("resize", refreshWidth);
      clearTimeout(task);
    };
  }, []);

  return width;
}

export function useWindowSizeRelationToBreakpoint(breakpoint: Breakpoint): RelationToBreakpoint {
  const width = useWindowWidth();

  if (width >= responsiveBreakpoints[breakpoint]) {
    return RelationToBreakpoint.Above;
  }
  return RelationToBreakpoint.Below;
}
