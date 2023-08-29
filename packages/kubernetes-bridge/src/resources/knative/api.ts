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

export enum KnativeApiVersions {
  KAFKA_SOURCE = "sources.knative.dev/v1beta1",
  SERVICE = "serving.knative.dev/v1",
}

export const KnativeLabelNames = {
  SERVICE: "serving.knative.dev/service",
};

export const KAFKA_SOURCE_FINALIZER = "kafkasources.sources.knative.dev";
export const KAFKA_SOURCE_CLIENT_ID_KEY = "kafka-source-client-id";
export const KAFKA_SOURCE_CLIENT_SECRET_KEY = "kafka-source-client-secret";
export const KAFKA_SOURCE_CLIENT_MECHANISM_KEY = "kafka-source-client-mechanism";
export const KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN = "PLAIN";
