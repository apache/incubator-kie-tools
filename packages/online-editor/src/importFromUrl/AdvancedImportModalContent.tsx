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
import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ServerRef } from "isomorphic-git";
import * as React from "react";
import { useImperativeHandle, useMemo, useState } from "react";
import { AuthSessionSelect } from "../accounts/authSessions/AuthSessionSelect";
import { authSessionsSelectFilterCompatibleWithGitUrlDomain } from "../accounts/authSessions/CompatibleAuthSessions";
import { getGitRefName, getGitRefType, getGitRefTypeLabel, GitRefType } from "../gitRefs/GitRefs";
import { isPotentiallyGit, useClonableUrl } from "./ImportableUrlHooks";

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
}

export const AdvancedImportModal = React.forwardRef<AdvancedImportModalRef, AdvancedImportModalProps>(
  (props, forwardedRef) => {
    const [isModalOpen, setModalOpen] = useState(false);
    const [isGitRefNameSelectorOpen, setGitRefNameSelectorOpen] = useState(false);

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
                  filter={authSessionsSelectFilterCompatibleWithGitUrlDomain(
                    props.clonableUrl.clonableUrl.url?.hostname
                  )}
                />
              </FormGroup>
              <FormGroup
                fieldId="url"
                label="URL"
                isRequired={true}
                helperTextInvalid={props.validation.helperTextInvalid}
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                helperText={
                  props.validation.option !== ValidatedOptions.success ? (
                    props.validation.helperText
                  ) : (
                    <FormHelperText isHidden={true} icon={<Spinner size={"sm"} />} />
                  )
                }
                validated={props.validation.option}
              >
                <TextInput
                  type="text"
                  id="import-modal-url"
                  name="import-modal-url"
                  validated={props.validation.option}
                  value={props.url}
                  onChange={(value) => props.setUrl(value)}
                />
              </FormGroup>
              <FormGroup
                style={!isPotentiallyGit(props.clonableUrl.clonableUrl.type) ? { visibility: "hidden" } : {}}
                fieldId="gitRefName"
                label="Branch/Tag"
                isRequired={true}
                helperText={
                  <FormHelperText
                    isHidden={!props.url || props.validation.option !== ValidatedOptions.default}
                    icon={<Spinner size={"sm"} />}
                  >
                    Loading...
                  </FormHelperText>
                }
              >
                <Select
                  isDisabled={props.validation.option !== ValidatedOptions.success}
                  variant={SelectVariant.typeahead}
                  selections={props.gitRefName}
                  isOpen={isGitRefNameSelectorOpen}
                  onToggle={setGitRefNameSelectorOpen}
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
