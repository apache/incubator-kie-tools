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

export interface FormCodeGenerator<
  FormThemeFileExt extends string,
  FormThemeName extends string,
  CustomFormAsset extends FormAsset<FormThemeFileExt>,
> {
  formCodeGeneratorTheme: FormCodeGeneratorTheme<FormThemeFileExt, FormThemeName, CustomFormAsset>;
  formSchemas: FormSchema[];
}

export function generateFormCode<
  FormThemeFileExt extends string,
  FormThemeName extends string,
  CustomFormAsset extends FormAsset<FormThemeFileExt>,
>({
  formCodeGeneratorTheme: formGeneratorTheme,
  formSchemas,
}: FormCodeGenerator<FormThemeFileExt, FormThemeName, CustomFormAsset>) {
  return formSchemas.reduce((generatedForms, formSchema) => {
    try {
      generatedForms.push({ formAsset: formGeneratorTheme.generate(formSchema), formError: undefined });
    } catch (error) {
      console.error(`Error generating form: ${error}`);
      generatedForms.push({ formAsset: undefined, formError: error });
    }
    return generatedForms;
  }, [] as FormCodeGeneration<FormThemeFileExt>[]);
}
