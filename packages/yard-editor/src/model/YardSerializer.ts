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

import * as yaml from "js-yaml";
import { YardModel } from "./YardModel";

/**
 * It deserializes content string as YardModel object
 * @param {string} content The JSON or YAML yard source to deserialize
 * @returns {YardModel} Resulting object representation of yard model
 */
export function deserialize(content: string): YardModel {
  try {
    const model = yaml.load(content);
    return new YardModel(model);
  } catch (e) {
    throw new Error("Error during deserialize phase of yard model" + e.toString());
  }
}
