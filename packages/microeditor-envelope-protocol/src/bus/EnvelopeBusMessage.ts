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

export interface EnvelopeBusMessage<D, T> {
  data: D;
  type: T;
  busId?: string; // Used for messages going from the envelope to the channel
  requestId?: string; // Used when purpose is REQUEST or RESPONSE
  purpose: EnvelopeBusMessagePurpose;
  error?: any; //Used on RESPONSES when an exception happens when processing a request
}

export enum EnvelopeBusMessagePurpose {
  REQUEST = "request",
  RESPONSE = "response",
  NOTIFICATION = "notification"
}
