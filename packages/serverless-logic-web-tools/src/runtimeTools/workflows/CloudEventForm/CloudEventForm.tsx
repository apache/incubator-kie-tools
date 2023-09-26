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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { OUIAProps, ouiaPageTypeAndObjectId, componentOuiaProps } from "@kie-tools/runtime-tools-common/dist/ouiaTools";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-common/dist/components/FormNotification";
import { useHistory } from "react-router-dom";
import CloudEventFormContainer from "../CloudEventFormContainer/CloudEventFormContainer";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

export enum CloudEventPageSource {
  DEFINITIONS = "definitions",
  INSTANCES = "instances",
}

const CloudEventForm: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [notification, setNotification] = useState<Notification>();

  const history = useHistory();

  const isTriggerNewInstance = useMemo(() => {
    const source = (history?.location?.state as any)["source"];
    return source === CloudEventPageSource.DEFINITIONS;
  }, [history]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("trigger-cloud-event-form");
  }, []);

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(`title${ouiaId ? "-" + ouiaId : ""}`, "trigger-cloud-event-form-page-section", ouiaSafe)}
        variant="light"
      >
        <TextContent>
          <Text component={TextVariants.h1}>{"Trigger Cloud Event"}</Text>
        </TextContent>
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps(`content${ouiaId ? "-" + ouiaId : ""}`, "cloud-event-form-page-section", ouiaSafe)}
      >
        <Card className="Dev-ui__card-size">
          <CardBody className="pf-u-h-100">
            <CloudEventFormContainer isTriggerNewInstance={isTriggerNewInstance} />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default CloudEventForm;
