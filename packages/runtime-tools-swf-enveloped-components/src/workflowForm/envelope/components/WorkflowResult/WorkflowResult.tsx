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
import React, { useMemo } from "react";
import { TextContent } from "@patternfly/react-core/dist/js/components/Text";
import ReactJson from "@microlink/react-json-view";
import { WorkflowResponse } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

export interface WorkflowResultProps {
  response: WorkflowResponse;
}

export default function WorkflowResult(props: WorkflowResultProps) {
  const filteredResponse = useMemo(() => ({ workflowdata: props.response.workflowdata }), [props.response]);

  return (
    <>
      <TextContent>
        <Title headingLevel="h3">Workflow result</Title>
      </TextContent>
      <br />
      <TextContent>
        <div>
          <ReactJson src={filteredResponse} name={false} />
        </div>
      </TextContent>
    </>
  );
}
