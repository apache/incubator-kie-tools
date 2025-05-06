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

import * as React from "react";
import { Bridge } from "uniforms";
import union from "lodash/union";
import escape from "lodash/escape";
import trim from "lodash/trim";
import * as prettier from "prettier";
import { FormElement } from "../api";
import {
  buildSetFormDataCallback,
  buildGetFormDataCallback,
  NS_SEPARATOR,
  buildGetFormDataCallbackDeps,
} from "./utils/Utils";
import { renderFormInputs } from "./rendering/RenderingUtils";
import { getStaticCodeBlock } from "./staticCode/staticCodeBlocks";

export type AutoFormProps = {
  id: string;
  idWithoutInvalidTsVarChars?: string;
  disabled?: boolean;
  placeholder?: boolean;
  schema: Bridge;
};

const AutoForm: React.FC<AutoFormProps> = (props) => {
  const inputs: FormElement[] = renderFormInputs(props.schema);

  let pfImports: string[] = [];
  let pfIconImports: string[] = [];
  let pfDeprecatedImports: string[] = [];
  let reactImports: string[] = ["useCallback", "useEffect"];
  let staticCodeArray: string[] = [];

  inputs.forEach((input) => {
    pfImports = union(pfImports, input.pfImports);
    pfIconImports = union(pfIconImports, input.pfIconImports);
    pfDeprecatedImports = union(pfDeprecatedImports, input.pfDeprecatedImports);
    reactImports = union(reactImports, input.reactImports);
    staticCodeArray = union(staticCodeArray, input.requiredCode);
  });

  const formId = props.idWithoutInvalidTsVarChars ?? props.id;
  const formName = `Form${formId ? `${NS_SEPARATOR}${formId}` : ""}`;
  const hooks = inputs.map((input) => input.stateCode).join("\n");
  const elements = inputs.map((input) => input.jsxCode).join("\n");
  const staticCodeStr: string = staticCodeArray.map((id) => getStaticCodeBlock(id)).join("\n");
  const formTemplate = `
import React, { ${reactImports.join(", ")} } from "react";
import { ${pfImports.join(", ")} } from "@patternfly/react-core";
${pfIconImports.length > 0 ? `import { ${pfIconImports.join(", ")} } from "@patternfly/react-icons";` : ""}
${pfDeprecatedImports.length > 0 ? `import { ${pfDeprecatedImports.join(", ")} } from "@patternfly/react-core/deprecated";` : ""}
    
const ${formName}: React.FC<any> = ( props:any ) => {
  const [formApi, setFormApi] = useState<any>();
  ${hooks}
      
  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    ${buildSetFormDataCallback(inputs)}
  }
      
  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData:any = {};
    ${buildGetFormDataCallback(inputs)}
    return formData;
  }, [${buildGetFormDataCallbackDeps(inputs)}]);
  
  /* Utility function to validate the form on the 'beforeSubmit' Lifecycle Hook */
  const validateForm = useCallback(() => {}, []);
  
  /* Utility function to perform actions on the on the 'afterSubmit' Lifecycle Hook */
  const afterSubmit = useCallback((result) => {}, []);

  useEffect(() => {
    if (formApi) {
      /*
        Form Lifecycle Hook that will be executed before the form is submitted.
        Throwing an error will stop the form submit. Usually should be used to validate the form.
      */
      formApi.beforeSubmit = () => validateForm();
      /*
        Form Lifecycle Hook that will be executed after the form is submitted.
        It will receive a response object containing the \`type\` flag indicating if the submit has been successful and \`info\` with extra information about the submit result.
      */
      formApi.afterSubmit = (result) => afterSubmit(result);
            
      /* Generates the expected form output object to be posted */
      formApi.getFormData = () => getFormData();
    }
  }, [getFormData, validateForm, afterSubmit]);
  
  useEffect(() => {
    /*
      Call to the Kogito console form engine. It will establish the connection with the console embeding the form
      and return an instance of FormAPI that will allow hook custom code into the form lifecycle.
      
      The \`window.Form.openForm\` call expects an object with the following entries:
        - onOpen: Callback that will be called after the connection with the console is established. The callback
        will receive the following arguments:
          - data: the data to be bound into the form
          - ctx: info about the context where the form is being displayed. This will contain information such as the form JSON Schema, process/task, user...
    */
    const api = window.Form.openForm({
      onOpen: (data, context) => {
        setFormData(data);
      }
    });
    setFormApi(api);
  }, []);
      
  ${staticCodeStr}
      
  return (
    <div className={'pf-v5-c-form'}>
      ${elements}        
    </div>
  )    
}
    
export default ${formName};`;

  const rawTemplate = trim(
    formTemplate
      .split("\n")
      .filter((line) => line && line.trim().length > 0)
      .join("\n")
  );

  const formattedFormTemplate = prettier.format(rawTemplate, {
    parser: "typescript",
    singleQuote: true,
    jsxSingleQuote: true,
    jsxBracketSameLine: true,
    useTabs: true,
  });
  return <>{escape(formattedFormTemplate)}</>;
};

export default AutoForm;
