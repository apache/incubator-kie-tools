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

/**
 * Methods provided by the Envelope that can be consumed by the Channel.
 */
export interface PingPongEnvelopeApi {
  pingPongView__init(association: Association, initArgs: PingPongInitArgs): Promise<void>;
  pingPongView__clearLogs(): Promise<void>;
  pingPongView__getLastPingTimestamp(): Promise<number>;
}

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export interface PingPongInitArgs {
  name: string;
}
