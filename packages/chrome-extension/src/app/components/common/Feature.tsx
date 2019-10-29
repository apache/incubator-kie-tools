/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import {
  AnyResolvedDomDependency,
  dependenciesAllSatisfied,
  DomDependencyMap,
  GlobalDomDependencies,
  resolveDependencies
} from "../../dependencies";
import * as React from "react";
import { useContext, useEffect } from "react";
import { GlobalContext } from "./GlobalContext";

export function Feature<T extends DomDependencyMap>(props: {
  name: string;
  dependencies: (d: GlobalDomDependencies) => T;
  component: (deps: { [J in keyof T]: AnyResolvedDomDependency }) => any;
}) {
  const globalContext = useContext(GlobalContext);
  const featureDependencies = props.dependencies(globalContext.dependencies);

  const shouldRender = dependenciesAllSatisfied(featureDependencies);
  useEffect(() => {
    if (!shouldRender) {
      //FIXME: Show what dependencies weren't satisfied
      globalContext.logger.log(`Could not render feature "${props.name}" because its dependencies were not satisfied.`);
    }
  }, []);

  return <>{shouldRender && props.component(resolveDependencies(featureDependencies))}</>;
}
