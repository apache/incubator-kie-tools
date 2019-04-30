/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { MarshallingContext } from "./MarshallingContext";
import { Portable } from "./Portable";
import { UnmarshallingContext } from "./UnmarshallingContext";

// marshall(T) => U
// unmarshall(V) => X

export interface Marshaller<T extends Portable<T>, U, V, X> {
  marshall(input: T, ctx: MarshallingContext): U | null;
  unmarshall(input: V | undefined, ctx: UnmarshallingContext): X | undefined;
}
