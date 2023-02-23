/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useCallback } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import InfoAltIcon from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { useRoutes } from "../navigation/Hooks";
import { MastheadBrand } from "@patternfly/react-core/dist/js/components/Masthead";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DescriptionList,
  DescriptionListTerm,
  DescriptionListGroup,
  DescriptionListDescription,
} from "@patternfly/react-core/dist/js/components/DescriptionList";

export const AboutButton: React.FunctionComponent = () => {
  const [isModalOpen, setIsModalOpen] = React.useState(false);
  const routes = useRoutes();
  const buildInfo = process.env.WEBPACK_REPLACE__buildInfo;
  const kogitoVersion = process.env.WEBPACK_REPLACE__kogitoRuntimeVersion;
  const quarkusVersion = process.env.WEBPACK_REPLACE__quarkusPlatformVersion;
  const dmnImage = process.env.WEBPACK_REPLACE__dmnDevDeployment_baseImageFullUrl;
  const extendedVersion = process.env.WEBPACK_REPLACE__kieSandboxExtendedServicesCompatibleVersion;
  const commitSha = process.env.WEBPACK_REPLACE__commitHash;

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prev) => !prev);
  }, []);

  return (
    <React.Fragment>
      <Button
        variant={ButtonVariant.plain}
        onClick={handleModalToggle}
        className={"kie-tools--masthead-hoverable-dark"}
      >
        <InfoAltIcon />
      </Button>
      <Modal
        header={
          <MastheadBrand style={{ textDecoration: "none" }}>
            <Flex alignItems={{ default: "alignItemsCenter" }}>
              <FlexItem style={{ display: "flex", alignItems: "center" }}>
                <Brand
                  src={routes.static.images.kieHorizontalLogoDefault.path({})}
                  alt={"Logo"}
                  style={{ display: "inline", height: "80px" }}
                />
              </FlexItem>
              <FlexItem style={{ display: "flex", alignItems: "center" }}>
                <TextContent>
                  <Text component={TextVariants.h2}>Sandbox</Text>
                </TextContent>
              </FlexItem>
            </Flex>
          </MastheadBrand>
        }
        variant={ModalVariant.large}
        isOpen={isModalOpen}
        aria-label="About Modal"
        aria-describedby="modal-title-icon-description"
        onClose={handleModalToggle}
      >
        <Divider inset={{ default: "insetMd" }} />
        <br />
        <Bullseye>
          <DescriptionList isHorizontal>
            <DescriptionListGroup>
              <DescriptionListTerm>Build Version: </DescriptionListTerm>
              <DescriptionListDescription>{buildInfo}</DescriptionListDescription>
            </DescriptionListGroup>
            <DescriptionListGroup>
              <DescriptionListTerm>Kogito Runtimes Version: </DescriptionListTerm>
              <DescriptionListDescription>{kogitoVersion}</DescriptionListDescription>
            </DescriptionListGroup>
            <DescriptionListGroup>
              <DescriptionListTerm>Quarkus Version: </DescriptionListTerm>
              <DescriptionListDescription>{quarkusVersion}</DescriptionListDescription>
            </DescriptionListGroup>
            <DescriptionListGroup>
              <DescriptionListTerm>DMN Dev deployments image URL: </DescriptionListTerm>
              <DescriptionListDescription>{dmnImage}</DescriptionListDescription>
            </DescriptionListGroup>
            <DescriptionListGroup>
              <DescriptionListTerm>Extended Services version: </DescriptionListTerm>
              <DescriptionListDescription>{extendedVersion}</DescriptionListDescription>
            </DescriptionListGroup>
            <DescriptionListGroup>
              <DescriptionListTerm>Commit SHA: </DescriptionListTerm>
              <DescriptionListDescription>{commitSha}</DescriptionListDescription>
            </DescriptionListGroup>
          </DescriptionList>
        </Bullseye>
        <br />
      </Modal>
    </React.Fragment>
  );
};
