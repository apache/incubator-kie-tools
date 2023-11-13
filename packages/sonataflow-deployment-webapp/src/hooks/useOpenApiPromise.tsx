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

import { useCallback } from "react";
import { PromiseStateStatus, usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { routes } from "../routes";
import { useApp } from "../context/AppContext";
import SwaggerParser from "@apidevtools/swagger-parser";
import { OpenAPI } from "openapi-types";

async function fetchOpenApi(): Promise<OpenAPI.Document> {
  return SwaggerParser.parse(routes.openApiJson.path({}));
}

export function useOpenApiPromise() {
  const app = useApp();
  const [openApiPromise, setOpenApiPromise] = usePromiseState<OpenAPI.Document>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (app.appDataPromise.status === PromiseStateStatus.RESOLVED) {
          fetchOpenApi()
            .then((data) => {
              if (canceled.get()) {
                return;
              }

              if (!data) {
                setOpenApiPromise({ error: "Cannot fetch data file" });
                return;
              }

              setOpenApiPromise({ data });
            })
            .catch((e) => {
              setOpenApiPromise({ error: e });
            });
        } else if (app.appDataPromise.status === PromiseStateStatus.REJECTED) {
          setOpenApiPromise({ error: "data.json file not available" });
          return;
        }
      },
      [setOpenApiPromise, app.appDataPromise.status]
    )
  );

  return openApiPromise;
}
