/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import React from "react";
import { useCallback, useContext } from "react";
import { JitDmn } from "../common/JitDmn";
import { GlobalContext } from "../common/GlobalContext";
import { AutoForm } from "uniforms-unstyled";
import JSONSchemaBridge from "uniforms-bridge-json-schema";

interface Props {
  editorContent: Promise<string> | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
}

export function JitDmnForm(props: Props) {
  const context = useContext(GlobalContext);

  // const generateDmnForm = useCallback(async () => {
  //   try {
  //     if (context.file.fileExtension !== "dmn") {
  //       return;
  //     }
  //
  //     const content = (await props.editorContent) ?? "";
  //     const formSchema = await JitDmn.getFormSchema(content);
  //   } catch (err) {
  //     console.error(err);
  //   }
  // }, [context.file]);

  return <div>{props.jsonSchemaBridge && <AutoForm schema={props.jsonSchemaBridge} onSubmit={console.log} />}</div>;
}
