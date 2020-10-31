/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { FieldName, Predicate, SimplePredicate } from "@kogito-tooling/pmml-editor-marshaller";

export const toText = (predicate: Predicate | undefined): string => {
  return JSON.stringify(predicate, undefined, 2);
};

export const fromText = (text: string): Predicate => {
  //TODO {manstis} The text in the payload needs to have been converted to a Predicate
  const predicate = new SimplePredicate({
    field: "mocked" as FieldName,
    operator: "equal",
    value: 48
  });
  //TODO {manstis} This is vitally important to ensure marshalling to XML works OK!
  (predicate as any)._type = "SimplePredicate";
  return predicate;
};
