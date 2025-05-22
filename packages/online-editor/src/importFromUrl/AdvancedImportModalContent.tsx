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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";

import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ServerRef } from "isomorphic-git";
import * as React from "react";
import { useImperativeHandle, useMemo, useState } from "react";
import { AuthProviderGroup } from "../authProviders/AuthProvidersApi";
import { AuthSessionSelect } from "../authSessions/AuthSessionSelect";
import { authSessionsSelectFilterCompatibleWithGitUrlDomain } from "../authSessions/CompatibleAuthSessions";
import { getGitRefName, getGitRefType, getGitRefTypeLabel, GitRefType } from "../gitRefs/GitRefs";
import { isPotentiallyGit, useClonableUrl } from "./ImportableUrlHooks";

import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { useOnlineI18n } from "../i18n";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

export interface AdvancedImportModalRef {
  open(): void;
}

export interface AdvancedImportModalProps {
  clonableUrl: ReturnType<typeof useClonableUrl>;
  validation: { option: ValidatedOptions; helperText?: React.ReactNode; helperTextInvalid?: string };
  onSubmit: (e: React.SyntheticEvent) => void;
  onClose: (() => void) | undefined;
  url: string;
  setUrl: React.Dispatch<React.SetStateAction<string>>;
  authSessionId: string | undefined;
  setAuthSessionId: React.Dispatch<React.SetStateAction<string | undefined>>;
  gitRefName: string;
  setGitRefName: React.Dispatch<React.SetStateAction<string>>;
  insecurelyDisableTlsCertificateValidation: boolean;
  setInsecurelyDisableTlsCertificateValidation: React.Dispatch<React.SetStateAction<boolean>>;
}

export const AdvancedImportModal = React.forwardRef<AdvancedImportModalRef, AdvancedImportModalProps>(
  (props, forwardedRef) => {
    const [isModalOpen, setModalOpen] = useState(false);
    const [isGitRefNameSelectorOpen, setGitRefNameSelectorOpen] = useState(false);
    const { i18n } = useOnlineI18n();

    useImperativeHandle(
      forwardedRef,
      () => ({
        open: () => setModalOpen(true),
      }),
      []
    );

    const buttonLabel = useMemo(() => {
      if (isPotentiallyGit(props.clonableUrl.clonableUrl.type)) {
        return "Clone";
      } else {
        return "Import";
      }
    }, [props.clonableUrl.clonableUrl.type]);

    const gitServerRefsByType = useMemo(() => {
      return props.clonableUrl.gitServerRefsPromise.data?.refs.reduce(
        (acc, next) => acc.set(getGitRefType(next.ref), [...(acc.get(getGitRefType(next.ref)) ?? []), next]),
        new Map<GitRefType, ServerRef[]>()
      );
    }, [props.clonableUrl.gitServerRefsPromise.data?.refs]);

    return (
      <>
        <Modal
          title="Import"
          isOpen={isModalOpen}
          variant={ModalVariant.medium}
          onClose={() => {
            props.onClose?.();
            return setModalOpen(false);
          }}
          actions={[
            <Button
              key="confirm"
              variant="primary"
              onClick={props.onSubmit}
              isDisabled={props.validation.option !== ValidatedOptions.success}
            >
              {buttonLabel}
            </Button>,
            <Button
              key="cancel"
              variant="link"
              onClick={() => {
                props.onClose?.();
                return setModalOpen(false);
              }}
            >
              Cancel
            </Button>,
          ]}
        >
          <div onKeyDown={(e) => e.stopPropagation()}>
            <Text>
              <TextContent>
                {`Choose the authentication source for your import, paste or type the URL of what you want to import, and if
                it's a Git repository, select the branch or tag as well.`}
              </TextContent>
            </Text>
            <br />
            <br />
            <Form onSubmit={props.onSubmit}>
              <FormGroup fieldId="auth-source" label="Authentication" isRequired={true}>
                <AuthSessionSelect
                  title={"Select authentication source for importing..."}
                  authSessionId={props.authSessionId}
                  setAuthSessionId={props.setAuthSessionId}
                  isPlain={false}
                  showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
                  filter={authSessionsSelectFilterCompatibleWithGitUrlDomain(props.clonableUrl.clonableUrl.url?.host)}
                />
              </FormGroup>
              <FormGroup fieldId="disable-tls-validation">
                <Checkbox
                  id="disable-tls-validation"
                  name="disable-tls-validation"
                  label={
                    <>
                      {i18n.connectToGitModal.insecurelyDisableTlsCertificateValidation}
                      <Popover
                        bodyContent={
                          <I18nHtml>{i18n.connectToGitModal.insecurelyDisableTlsCertificateValidationInfo}</I18nHtml>
                        }
                      >
                        <button
                          type="button"
                          aria-label="More info for disable-tls-validation field"
                          onClick={(e) => e.preventDefault()}
                          aria-describedby="disable-tls-validation-field"
                          className="pf-v5-c-form__group-label-help"
                        >
                          <Icon isInline>
                            <HelpIcon />
                          </Icon>
                        </button>
                      </Popover>
                    </>
                  }
                  aria-label="Disable TLS Certificate Validation"
                  tabIndex={4}
                  isChecked={props.insecurelyDisableTlsCertificateValidation}
                  onChange={(_event, val) => props.setInsecurelyDisableTlsCertificateValidation(val)}
                />
              </FormGroup>
              <FormGroup fieldId="url" label="URL" isRequired={true}>
                <TextInput
                  type="text"
                  id="import-modal-url"
                  name="import-modal-url"
                  validated={props.validation.option}
                  value={props.url}
                  onChange={(_event, value) => props.setUrl(value)}
                />
                <HelperText>
                  {props.validation.option === "error" ? (
                    <HelperTextItem variant="error" icon={<ExclamationCircleIcon />}>
                      {props.validation.helperTextInvalid}
                    </HelperTextItem>
                  ) : (
                    props.validation.option !== "success" && props.validation.helperText
                  )}
                </HelperText>
              </FormGroup>
              <FormGroup
                style={!isPotentiallyGit(props.clonableUrl.clonableUrl.type) ? { visibility: "hidden" } : {}}
                fieldId="gitRefName"
                label="Branch/Tag"
                isRequired={true}
              >
                <Select
                  isDisabled={props.validation.option !== ValidatedOptions.success}
                  variant={SelectVariant.typeahead}
                  selections={props.gitRefName}
                  isOpen={isGitRefNameSelectorOpen}
                  onToggle={(_event, val) => setGitRefNameSelectorOpen(val)}
                  isGrouped={true}
                  onSelect={(e, value) => {
                    props.setGitRefName(value as string);
                    setGitRefNameSelectorOpen(false);
                  }}
                  menuAppendTo={document.body}
                  maxHeight={"400px"}
                >
                  {[...(gitServerRefsByType?.entries() ?? [])]
                    .sort(([a], [b]) => (a > b ? 1 : -1))
                    .filter(([type]) => type === GitRefType.BRANCH || type === GitRefType.TAG)
                    .map(([type, gitServerRefs]) => (
                      <SelectGroup key={type} label={getGitRefTypeLabel(type)}>
                        {gitServerRefs.map(({ ref }) => (
                          <SelectOption key={ref} value={getGitRefName(ref)}>
                            {getGitRefName(ref)}
                          </SelectOption>
                        ))}
                      </SelectGroup>
                    ))}
                </Select>
                <HelperText>{props.validation.option !== "success" && props.validation.helperText}</HelperText>
              </FormGroup>
            </Form>
            <br />
            <br />
          </div>
        </Modal>
      </>
    );
  }
);
