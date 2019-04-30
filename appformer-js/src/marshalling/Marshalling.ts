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

import { Portable } from "./Portable";
import { MarshallerProvider } from "./MarshallerProvider";
import { MarshallingContext } from "./MarshallingContext";
import { UnmarshallingContext } from "./UnmarshallingContext";
import { ErraiObjectConstants } from "./model/ErraiObjectConstants";

/**
 * Serializes a Portable<T> object into an Errai json.
 * @param obj
 */

export function marshall<T>(obj: Portable<T>): string | null {
  if (obj === null || obj === undefined) {
    return null;
  }

  const marshaller = MarshallerProvider.getForObject(obj);
  return JSON.stringify(marshaller.marshall(obj, new MarshallingContext()));
}

/**
 * Deserializes an Errai json creating a Portable<T> object.
 *
 * @param json
 * The Errai json.
 * @param oracle
 * A map containing fqcns as keys and a function returning an empty Portable<T> associated with that fqcn.
 */
export function unmarshall<T>(json: string, oracle: Map<string, () => Portable<any>>): Portable<T> | null | void {
  if (json === null || json === undefined) {
    return undefined;
  }

  const jsonObj = JSON.parse(json);
  const fqcn = jsonObj[ErraiObjectConstants.ENCODED_TYPE];

  const marshaller = MarshallerProvider.getForFqcn(fqcn);
  return marshaller.unmarshall(jsonObj, new UnmarshallingContext(oracle));
}
