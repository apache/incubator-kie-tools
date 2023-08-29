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

import React from "react";
import { AcceleratorAppliedConfig } from "../../../accelerators/AcceleratorsApi";
import { useOnlineI18n } from "../../../i18n";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { AcceleratorIcon } from "./AcceleratorIcon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

type Props = {
  accelerator: AcceleratorAppliedConfig;
  isOpen: boolean;
  onClose: () => any;
  isApplying?: boolean;
  onApplyAccelerator?: () => void;
};

export function AcceleratorModal(props: Props) {
  const { i18n } = useOnlineI18n();

  return (
    <Modal
      isOpen={props.isOpen}
      onClose={props.onClose}
      aria-label="Accelerator"
      variant={ModalVariant.medium}
      actions={
        props.isApplying && [
          <Button key="apply" variant="primary" onClick={props.onApplyAccelerator}>
            {i18n.terms.apply}
          </Button>,
          <Button key="cancel" variant="link" onClick={props.onClose}>
            {i18n.terms.cancel}
          </Button>,
        ]
      }
    >
      <Title headingLevel="h1">
        <AcceleratorIcon iconUrl={props.accelerator.iconUrl} />
        &nbsp;
        {props.accelerator.name} Accelerator
      </Title>
      <Grid style={{ margin: "1rem 0" }} hasGutter>
        {props.isApplying && (
          <GridItem span={12}>
            <i>{i18n.accelerators.acceleratorDescription}</i>
          </GridItem>
        )}
        <GridItem span={12}>
          <p>
            {i18n.accelerators.acceleratorDetails}
            &nbsp;
            <a href={props.accelerator.gitRepositoryUrl} target="__blank">
              {props.accelerator.gitRepositoryUrl}
            </a>
          </p>
          Git ref:{" "}
          <small>
            <pre style={{ display: "inline" }}>@{props.accelerator.gitRepositoryGitRef}</pre>
          </small>
        </GridItem>
        <Divider />
        <GridItem span={6}>
          {props.isApplying ? i18n.accelerators.dmnFilesMove : i18n.accelerators.dmnFilesLocation}
        </GridItem>
        <GridItem span={6}>
          <pre>{props.accelerator.dmnDestinationFolder}</pre>
        </GridItem>
        <GridItem span={6}>
          {props.isApplying ? i18n.accelerators.pmmlFilesMove : i18n.accelerators.pmmlFilesLocation}
        </GridItem>
        <GridItem span={6}>
          <pre>{props.accelerator.dmnDestinationFolder}</pre>
        </GridItem>
        <GridItem span={6}>
          {props.isApplying ? i18n.accelerators.bpmnFilesMove : i18n.accelerators.bpmnFilesLocation}
        </GridItem>
        <GridItem span={6}>
          <pre>{props.accelerator.bpmnDestinationFolder}</pre>
        </GridItem>
        <GridItem span={6}>
          {props.isApplying ? i18n.accelerators.otherFilesMove : i18n.accelerators.otherFilesLocation}
        </GridItem>
        <GridItem span={6}>
          <pre>{props.accelerator.otherFilesDestinationFolder}</pre>
        </GridItem>
        {!props.isApplying && props.accelerator.appliedAt && (
          <>
            <Divider />
            <GridItem span={12}>
              <i>
                {i18n.accelerators.appliedAt} {new Date(props.accelerator.appliedAt).toLocaleString()}
              </i>
            </GridItem>
          </>
        )}
        {props.isApplying && (
          <>
            <Divider />
            <GridItem span={12}>
              <i>{i18n.accelerators.applyDisclaimer}</i>
            </GridItem>
          </>
        )}
      </Grid>
    </Modal>
  );
}
