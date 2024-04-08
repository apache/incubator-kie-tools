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

import React, { useEffect, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { useFormDetailsContext } from "../../components/contexts/FormDetailsContext";
import { buildTestContext } from "./utils/utils";
import { EmbeddedFormDisplayer, FormDisplayerApi } from "../../../../formDisplayer";

interface FormDisplayerContainerProps {
  formContent: Form;
  targetOrigin: string;
}

const FormDisplayerContainer: React.FC<FormDisplayerContainerProps> = ({ formContent, targetOrigin }) => {
  const [displayerKey, setDisplayerKey] = useState<string>(uuidv4());
  const appContext = useFormDetailsContext();
  const formDisplayerApiRef = React.useRef<FormDisplayerApi>({} as FormDisplayerApi);

  useEffect(() => {
    const unsubscribeUserChange = appContext.onUpdateContent({
      onUpdateContent(formContent) {
        setDisplayerKey(uuidv4());
      },
    });
    return () => {
      unsubscribeUserChange.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedFormDisplayer
      targetOrigin={targetOrigin}
      envelopePath={"resources/form-displayer.html"}
      formContent={formContent}
      context={buildTestContext(formContent)}
      ref={formDisplayerApiRef}
      key={displayerKey}
    />
  );
};

export default FormDisplayerContainer;
