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

import { DmnFormSchema } from "@kogito-tooling/form/dist/dmn";
import * as React from "react";
import { useContext } from "react";
import { DmnRunnerService } from "./DmnRunnerService";
import { DmnRunnerStatus } from "./DmnRunnerStatus";

export interface DmnRunnerContextType {
  status: DmnRunnerStatus;
  setStatus: React.Dispatch<DmnRunnerStatus>;
  formSchema?: DmnFormSchema;
  isDrawerExpanded: boolean;
  setDrawerExpanded: React.Dispatch<boolean>;
  formData: any;
  setFormData: React.Dispatch<any>;
  service: DmnRunnerService;
  formError: boolean;
  setFormError: React.Dispatch<boolean>;
}

export const DmnRunnerContext = React.createContext<DmnRunnerContextType>({
  status: DmnRunnerStatus.UNAVAILABLE,
  isDrawerOpen: false,
  isModalOpen: false,
} as any);

export function useDmnRunner() {
  return useContext(DmnRunnerContext);
}
