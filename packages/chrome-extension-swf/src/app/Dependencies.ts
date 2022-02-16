/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

export class Dependencies {
  public readonly createServerlessWorkflow = {
    card: () => document.querySelector(".co-catalog") as HTMLElement | null, //FIXME
  };

  public readonly applicationServices = {
    menu: () => document.querySelector(".pf-c-nav__list") as HTMLUListElement | null,
    main: () => document.querySelector(".ins-c-render") as HTMLElement | null,
    page: () => document.querySelector(".applicationServices") as HTMLElement | null,
  };

  public readonly deploymentViewer = {
    buildOverview: () => document.querySelector(".build-overview") as HTMLElement | null,
    resourceName: () => document.querySelector(".co-resource-item__resource-name") as HTMLAnchorElement | null,
  };

  public readonly all = {
    body: () => document.body,
  };
}
