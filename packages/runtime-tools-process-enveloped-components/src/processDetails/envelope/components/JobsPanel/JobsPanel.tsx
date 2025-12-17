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
import React, { useMemo } from "react";
import { Card, CardHeader, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { TableVariant, IRow, ICell } from "@patternfly/react-table/dist/js/components/Table";
import { Table, TableHeader, TableBody } from "@patternfly/react-table/dist/js/deprecated";
import Moment from "react-moment";
import JobActionsKebab from "../JobActionsKebab/JobActionsKebab";
import { JobsIconCreator } from "../../../utils/Utils";
import { ProcessDetailsChannelApi } from "../../../api";
import { Job } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

interface JobsPanelProps {
  jobs: Job[];
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

const JobsPanel: React.FC<JobsPanelProps> = ({ jobs, channelApi }) => {
  const columns: ICell[] = useMemo(() => {
    return [
      {
        title: "Job id",
      },
      {
        title: "Status",
      },
      {
        title: "Expiration time",
      },
      {
        title: "Actions",
      },
    ];
  }, []);

  const rows: IRow[] = useMemo(() => {
    if (!jobs) {
      return [];
    }

    return jobs.map((job) => {
      return {
        cells: [
          {
            title: (
              <Tooltip content={job.id}>
                <span>{job.id.substring(0, 7)}</span>
              </Tooltip>
            ),
          },
          {
            title: JobsIconCreator(job.status),
          },
          {
            title: (
              <React.Fragment>
                {job.expirationTime ? (
                  <>
                    {" "}
                    expires in{" "}
                    <Moment fromNow ago>
                      {job.expirationTime}
                    </Moment>
                  </>
                ) : (
                  "N/A"
                )}
              </React.Fragment>
            ),
          },
          {
            title: <JobActionsKebab job={job} channelApi={channelApi} />,
          },
        ],
      };
    });
  }, [channelApi, jobs]);

  if (jobs.length > 0) {
    return (
      <Card className="process-details-jobs-panel" style={{ height: "100%" }}>
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Jobs
          </Title>
        </CardHeader>
        <CardBody>
          <Table
            aria-label="Process details jobs panel"
            aria-labelledby="Process details jobs panel"
            variant={TableVariant.compact}
            rows={rows}
            cells={columns}
          >
            <TableHeader />
            <TableBody />
          </Table>
        </CardBody>
      </Card>
    );
  } else {
    return null;
  }
};

export default JobsPanel;
