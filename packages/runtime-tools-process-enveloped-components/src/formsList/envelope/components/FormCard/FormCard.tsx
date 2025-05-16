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

import React, { useCallback, useMemo } from "react";
import { FormGroup, Form } from "@patternfly/react-core/dist/js/components/Form";
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { TextVariants, Text } from "@patternfly/react-core/dist/js/components/Text";
import { FormInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import Moment from "react-moment";
import { FormsListChannelApi } from "../../../api";
import { getFormTypeLabel } from "../FormsListUtils/FormsListUtils";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export interface FormCardProps {
  formData: FormInfo;
  channelApi: MessageBusClientApi<FormsListChannelApi>;
}

const FormCard: React.FC<FormCardProps> = ({ formData, channelApi }) => {
  const label = useMemo(() => getFormTypeLabel(formData.type), [formData.type]);

  const handleCardClick = useCallback(async () => {
    await channelApi.notifications.formsList__openForm.send(formData);
  }, [channelApi.notifications, formData]);

  return (
    <Card isSelectable onClick={handleCardClick}>
      <CardHeader>{label}</CardHeader>
      <CardHeader>
        <Text component={TextVariants.h1} className="pf-u-font-weight-bold">
          {formData.name}
        </Text>
      </CardHeader>
      <CardBody>
        <div className="pf-u-mt-md">
          <Form>
            <FormGroup label="Type" fieldId="type">
              <Text component={TextVariants.p}>{formData.type}</Text>
            </FormGroup>
            <FormGroup label="LastModified" fieldId="lastModified">
              <Text component={TextVariants.p}>
                <Moment fromNow>{formData.lastModified}</Moment>
              </Text>
            </FormGroup>
          </Form>
        </div>
      </CardBody>
    </Card>
  );
};

export default FormCard;
