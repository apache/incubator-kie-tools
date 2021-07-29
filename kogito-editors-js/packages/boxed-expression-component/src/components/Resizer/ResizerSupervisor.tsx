/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import "./Resizer.css";
import * as React from "react";
import { applyDOMSupervisor } from "./dom";
import { useEffect, useContext } from "react";
import { BoxedExpressionGlobalContext } from "../../context";

export interface ResizerSupervisorProps {
  children?: React.ReactElement;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = ({ children }) => {
  const { supervisorHash } = useContext(BoxedExpressionGlobalContext);

  useEffect(() => {
    const id = setTimeout(applyDOMSupervisor, 0);
    return () => clearTimeout(id);
  }, [supervisorHash]);

  return <>{children}</>;
};
