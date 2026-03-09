<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# @kie-tools-core/envelope-bus :: ARCHITECTURE

- `channel/EnvelopeServer` and `envelope/EnvelopeClient` are simply convenient abstractions on top of the `common/EnvelopeBusMessageManager`, where the entire communication logic is.
- Inter-**`Envelope`** notifications are possible through the **`Channel`**, whenever **`Envelopes`** are subscribed to a message of the same type. This can act as a broadcasting mechanism originating from **`Envelopes`**.
- In order to make the definitions of APIs simple while providing a type-safe consumption API, a Proxy mechanism is used to generate dynamic objects. See `cachedProxy` usages in `common/EnvelopeBusMessageManager`.
- Routing logic for incoming messages on both `channel/EnvelopeServer` and `envelope/EnvelopeClient` are inside their `receive` method. Since **`Channels`** can have multiple **`Envelopes`** of various types sending messages to it through the same bus ("message" events on Window), a sophisticated filtering mechanism exists.
- It is possible to nest **`Envelope`**s, making an **`Envelope`** be both a **`Channel`** and an **`Envelope`** at the same time.
