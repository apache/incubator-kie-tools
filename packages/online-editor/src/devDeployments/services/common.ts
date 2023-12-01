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

export enum DeploymentState {
  UP = "UP",
  DOWN = "DOWN",
  IN_PROGRESS = "IN_PROGRESS",
  PREPARING = "PREPARING",
  ERROR = "ERROR",
}

export enum HealthStatus {
  UP = "UP",
  ERROR = "ERROR",
}

export interface DeployedModel {
  resourceName: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
}
