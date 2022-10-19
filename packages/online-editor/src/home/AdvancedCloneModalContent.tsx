/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useState } from "react";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { UrlType } from "../workspace/hooks/ImportableUrlHooks";
import { useEnhancedImportableUrl } from "./ImportFromUrlCard";

export interface AdvancedCloneModalRef {
  open(): void;
}

export interface AdvancedCloneModalProps {
  enhancedImportableUrl: ReturnType<typeof useEnhancedImportableUrl>;
}

// These are temporary placeholders
export type AuthSource = string;
const AUTH_SOURCE_NONE = "none";
//

export const AdvancedCloneModal = React.forwardRef<AdvancedCloneModalRef, AdvancedCloneModalProps>(
  (props, forwardedRef) => {
    const [isModalOpen, setModalOpen] = useState(false);
    const [branch, setBranch] = React.useState("");
    const [isBranchSelectorOpen, setBranchSelectorOpen] = React.useState(false);
    const [authSource, setAuthSource] = React.useState<AuthSource>(AUTH_SOURCE_NONE);
    const [isAuthSourceSelectorOpen, setAuthSourceSelectorOpen] = React.useState(false);

    useImperativeHandle(forwardedRef, () => ({ open: () => setModalOpen(true) }), []);

    useEffect(() => {
      setBranch(props.enhancedImportableUrl.selectedBranch);
    }, [props.enhancedImportableUrl.selectedBranch]);

    const clone = useCallback(() => {
      /* TODO: Redirect to clone page? Do it here? */
    }, []);

    const settings = useSettings();

    useEffect(() => {
      const urlType = props.enhancedImportableUrl.importableUrl.type;
      if (urlType === UrlType.GITHUB_DOT_COM || urlType === UrlType.GIST_DOT_GITHUB_DOT_COM) {
        if (settings.github.authStatus === AuthStatus.SIGNED_IN) {
          setAuthSource("github.com");
          return;
        }
      }

      setAuthSource(AUTH_SOURCE_NONE);
    }, [props.enhancedImportableUrl.importableUrl.type, settings.github.authStatus]);

    // This is a temporary placeholder.
    const authSources = [
      {
        enabled: true,
        icon: <GithubIcon />,
        domain: "github.com",
        description:
          settings.github.authStatus === AuthStatus.SIGNED_IN ? `(${settings.github.user?.login})` : "Not logged in",
      },
      { enabled: false, icon: <BitbucketIcon />, domain: "bitbucket.com", description: "Available soon!" },
      { enabled: false, icon: <GitlabIcon />, domain: "gitlab.com", description: "Available soon!" },
    ];

    return (
      <>
        <Modal
          isOpen={isModalOpen}
          variant={ModalVariant.medium}
          title={"Clone"}
          onClose={() => setModalOpen(false)}
          actions={[
            <Button key="confirm" variant="primary" onClick={clone}>
              Clone
            </Button>,
          ]}
        >
          <Page style={{ minHeight: "50vh" }}>
            <PageSection>
              <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
                <Form onSubmit={clone}>
                  <FormGroup fieldId="url" label="URL">
                    <TextInput
                      isDisabled={true}
                      type="text"
                      id="advanced-clone-url"
                      name="advanced-clone-url"
                      value={props.enhancedImportableUrl.importableUrl.url.toString()}
                    />
                  </FormGroup>
                  <FormGroup fieldId="auth-source" label="Authenticate with">
                    <Select
                      variant={SelectVariant.typeahead}
                      selections={authSource}
                      isOpen={isAuthSourceSelectorOpen}
                      onToggle={setAuthSourceSelectorOpen}
                      onSelect={(e, value) => {
                        setAuthSource(value as string);
                        setAuthSourceSelectorOpen(false);
                      }}
                      menuAppendTo={"parent"}
                      maxHeight={"400px"}
                    >
                      {[
                        <SelectOption key={-1} value={AUTH_SOURCE_NONE}>
                          None
                        </SelectOption>,
                        ...authSources.map((authSource, index) => (
                          <SelectOption
                            key={index}
                            value={authSource.domain}
                            isDisabled={!authSource.enabled}
                            description={<i>{authSource.description}</i>}
                          >
                            {authSource.icon}&nbsp;&nbsp;
                            {authSource.domain}
                          </SelectOption>
                        )),
                      ]}
                    </Select>
                  </FormGroup>
                  <FormGroup fieldId="branch" label="Branch">
                    <Select
                      variant={SelectVariant.typeahead}
                      selections={branch}
                      isOpen={isBranchSelectorOpen}
                      onToggle={setBranchSelectorOpen}
                      onSelect={(e, value) => {
                        setBranch(value as string);
                        setBranchSelectorOpen(false);
                      }}
                      menuAppendTo={document.body}
                      maxHeight={"400px"}
                    >
                      {props.enhancedImportableUrl.refsPromise.data
                        ?.filter(({ ref }) => ref.startsWith("refs/heads/"))
                        .map(({ ref }, index) => (
                          <SelectOption key={index} value={ref.replace("refs/heads/", "")}>
                            {ref.replace("refs/heads/", "")}
                          </SelectOption>
                        ))}
                    </Select>
                  </FormGroup>
                </Form>
              </PageSection>
            </PageSection>
          </Page>
        </Modal>
      </>
    );
  }
);
