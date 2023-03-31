/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { IObjectMeta } from "@kubernetes-models/apimachinery/apis/meta/v1/ObjectMeta";

export type IpProtocol = "TCP" | "UDP" | "SCTP";

export type ResourceRequirements = {
  limits: {
    memory: string;
  };
};

export interface ResourceDescriptor {
  apiVersion: string;
  kind: string;
  metadata?: IObjectMeta;
}

export interface ResourceGroupDescriptor<T extends ResourceDescriptor> {
  items: T[];
}

export type Resource<T extends ResourceDescriptor = ResourceDescriptor> =
  | ResourceDescriptor
  | ResourceGroupDescriptor<T>;

export interface EnvVar {
  name: string;
  value: string;
}
