/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useMemo } from "react";
import { FormDmnValidator } from "./FormDmnValidator";
import { formDmnI18n } from "./i18n";
import { FormComponent, FormProps } from "@kie-tools/form/dist/FormComponent";
import { DmnAutoFieldProvider } from "@kie-tools/dmn-runner/dist/uniforms";
import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import { formDmnRunnerAutoFieldValue } from "./uniforms/FormDmnRunnerAutoFieldValue";

export type InputRow = Record<string, any>;

export function FormDmn(props: FormProps<InputRow, ExtendedServicesDmnJsonSchema>) {
  const i18n = useMemo(() => {
    formDmnI18n.setLocale(props.locale ?? navigator.language);
    return formDmnI18n.getCurrent();
  }, [props.locale]);
  const dmnValidator = useMemo(() => new FormDmnValidator(i18n), [i18n]);

  return (
    <FormComponent
      {...props}
      i18n={i18n}
      validator={dmnValidator}
      removeRequired={true}
      entryPath={"definitions.InputSet"}
      propertiesEntryPath={"definitions.InputSet.properties"}
    >
      <DmnAutoFieldProvider value={formDmnRunnerAutoFieldValue} />
    </FormComponent>
  );
}
