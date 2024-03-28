/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from "react";
import { Title, Tooltip } from "@patternfly/react-core/dist/js/components";
import SkeletonStripe from "../../Atoms/SkeletonStripe/SkeletonStripe";
import ExecutionStatus from "../../Atoms/ExecutionStatus/ExecutionStatus";
import FormattedDate from "../../Atoms/FormattedDate/FormattedDate";
import ExecutionId from "../../Atoms/ExecutionId/ExecutionId";
import { RemoteData, Execution, RemoteDataStatus } from "../../../types";
import "./ExecutionHeader.scss";
import { attributeOuiaId } from "@kie-tools/runtime-tools-components/dist/ouiaTools/OuiaUtils";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts";

type ExecutionHeaderProps = {
  execution: RemoteData<Error, Execution>;
};

const ExecutionHeader = (props: ExecutionHeaderProps) => {
  const { execution } = props;

  return (
    <section className="execution-header" {...attributeOuiaId("execution-header")}>
      <Flex>
        <FlexItem>
          {execution.status === RemoteDataStatus.LOADING && (
            <SkeletonStripe
              isInline={true}
              customStyle={{
                height: "1.8em",
                width: 500,
                verticalAlign: "baseline",
                margin: 0,
              }}
            />
          )}
          {execution.status === RemoteDataStatus.SUCCESS && (
            <Title size="3xl" headingLevel="h2" {...attributeOuiaId("title")}>
              <span className="execution-header__uuid">
                Execution <ExecutionId id={execution.data.executionId} />
              </span>
            </Title>
          )}
        </FlexItem>
        <FlexItem className="execution-header__property">
          {execution.status === RemoteDataStatus.SUCCESS && (
            <Tooltip
              entryDelay={23}
              exitDelay={23}
              distance={5}
              position="bottom"
              content={
                <div>
                  <span>
                    Created on <FormattedDate date={execution.data.executionDate} fullDateAndTime={true} />
                  </span>
                  {execution.data.executorName && (
                    <>
                      <br />
                      <span>Executed by {execution.data.executorName}</span>
                    </>
                  )}
                </div>
              }
            >
              <div>
                <ExecutionStatus result={execution.data.executionSucceeded ? "success" : "failure"} ouiaId="status" />
              </div>
            </Tooltip>
          )}
        </FlexItem>
      </Flex>
    </section>
  );
};

export default ExecutionHeader;
