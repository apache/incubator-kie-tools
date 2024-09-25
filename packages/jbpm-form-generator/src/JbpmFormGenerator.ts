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

import { Bootstrap4FormGenerator } from "./generators/Bootstrap4FormGenerator";
import { PatternflyFormGenerator } from "./generators/PatternflyFormGenerator";
import { FormGenerator, FormSchema, FormAsset, FormGenerationError } from "./types";

export interface Args {
  type: string;
  formSchemas: FormSchema[];
}

export class JbpmFormGenerator {
  private formGeneratorIndex: Map<string, FormGenerator> = new Map<string, FormGenerator>();

  constructor() {
    this.registerFormGeneratorType(new PatternflyFormGenerator());
    this.registerFormGeneratorType(new Bootstrap4FormGenerator());
  }

  public registerFormGeneratorType(formGenerator: FormGenerator) {
    this.formGeneratorIndex.set(formGenerator.type, formGenerator);
  }

  public getFormGenerator(type: string): FormGenerator {
    const formGenerator = this.formGeneratorIndex.get(type);
    if (formGenerator) {
      return formGenerator;
    }
    throw new Error(`Unsupported form generation type: "${type}"`);
  }

  public generateForms({ type, formSchemas }: Args): (FormAsset | FormGenerationError)[] {
    const formGenerator = this.getFormGenerator(type);
    return formSchemas.reduce((generatedForms: (FormAsset | FormGenerationError)[], formSchema) => {
      try {
        generatedForms.push(formGenerator.generate(formSchema));
      } catch (error) {
        console.error(`Error generating form: ${error}`);
        generatedForms.push({ error });
      }
      return generatedForms;
    }, []);
  }
}
