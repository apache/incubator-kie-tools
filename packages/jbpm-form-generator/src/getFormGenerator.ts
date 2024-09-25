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

import { PatternflyFormGenerator } from "./generators/PatternflyFormGenerator";
import { FormGenerator } from "./types";
import { Bootstrap4FormGenerator } from "./generators/Bootstrap4FormGenerator";

/**
 * A index of form generator type by its generator class
 */
const formGeneratorIndex: Map<string, FormGenerator> = new Map<string, FormGenerator>();

export function registerFormGeneratorType(formGenerator: FormGenerator) {
  formGeneratorIndex.set(formGenerator.type, formGenerator);
}

registerFormGeneratorType(new PatternflyFormGenerator());
registerFormGeneratorType(new Bootstrap4FormGenerator());

export function getFormGenerator(type: string): FormGenerator {
  const formGenerator = formGeneratorIndex.get(type);
  if (formGenerator) {
    return formGenerator;
  }
  throw new Error(`Unsupported form generation type: "${type}"`);
}
