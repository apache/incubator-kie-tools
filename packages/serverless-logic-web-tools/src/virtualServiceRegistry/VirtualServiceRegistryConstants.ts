/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { toVsrMountPoint } from "./VirtualServiceRegistryPathConverter";

export const VIRTUAL_SERVICE_REGISTRY_NAME = "Virtual";
export const VIRTUAL_SERVICE_REGISTRY_MOUNT_POINT = "lfs_v1__vsr__";
export const VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX = "virtual::";
export const VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX = "VSR";
export const VIRTUAL_SERVICE_REGISTRY_DESCRIPTOR_DATABASE_NAME = toVsrMountPoint("registries");
