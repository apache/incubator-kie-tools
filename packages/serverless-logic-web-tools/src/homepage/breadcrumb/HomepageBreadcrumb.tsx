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
import { Breadcrumb, BreadcrumbItem } from "@patternfly/react-core/components/Breadcrumb";

export function HomepageBreadcrumb(props: { paths: string[] }) {
  const getPath = (path: string): string => {
    return "/" + path;
  };
  return (
    <Breadcrumb>
      {props.paths.map((path) => (
        <BreadcrumbItem key={path} to={getPath(path)}>
          {path === "/" ? { path } : "Home"}
        </BreadcrumbItem>
      ))}
      ;
    </Breadcrumb>
  );
}

export default HomepageBreadcrumb;
