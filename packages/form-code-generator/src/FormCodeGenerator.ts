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

import { FormCodeGeneratorTheme, FormSchema, FormAsset, FormCodeGeneration } from "./types";

export interface Args<FormTheme> {
  theme: FormTheme;
  formSchemas: FormSchema[];
}

export class FormCodeGenerator<
  FormThemeFileExt extends string,
  FormThemeName extends string,
  CustomFormAsset extends FormAsset<FormThemeFileExt>,
> {
  constructor(...themes: FormCodeGeneratorTheme<FormThemeFileExt, FormThemeName, CustomFormAsset>[]) {
    if (themes !== undefined) {
      themes.forEach((theme) => this.registerFormGeneratorType(theme));
    }
  }

  private formGeneratorIndex = new Map<
    string,
    FormCodeGeneratorTheme<FormThemeFileExt, FormThemeName, CustomFormAsset>
  >();

  public registerFormGeneratorType(
    formGenerator: FormCodeGeneratorTheme<FormThemeFileExt, FormThemeName, CustomFormAsset>
  ) {
    this.formGeneratorIndex.set(formGenerator.theme, formGenerator);
  }

  public getFormCodeGenerator(theme: FormThemeName) {
    const formGenerator = this.formGeneratorIndex.get(theme);
    if (formGenerator) {
      return formGenerator;
    }
    throw new Error(`Unsupported form generation type: "${theme}"`);
  }

  public generateForms({ theme, formSchemas }: Args<FormThemeName>) {
    const formGenerator = this.getFormCodeGenerator(theme);
    return formSchemas.reduce((generatedForms, formSchema) => {
      try {
        generatedForms.push({ formAssets: formGenerator.generate(formSchema), formErrors: undefined });
      } catch (error) {
        console.error(`Error generating form: ${error}`);
        generatedForms.push({ formAssets: undefined, formErrors: error });
      }
      return generatedForms;
    }, [] as FormCodeGeneration<FormThemeFileExt>[]);
  }
}
