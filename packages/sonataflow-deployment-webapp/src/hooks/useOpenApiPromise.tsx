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

import { useCallback } from "react";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { OpenApi } from "openapi-v3";
import { routes } from "../routes";

async function fetchOpenApi(): Promise<OpenApi> {
  const response = await fetch(routes.openApi.path({}));
  return (await response.json()) as OpenApi;
}

export function useOpenApiPromise() {
  const [openApiPromise, setOpenApiPromise] = usePromiseState<OpenApi>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
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
      },
      [setOpenApiPromise]
    )
  );

  return openApiPromise;
}
