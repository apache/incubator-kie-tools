/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { GlobalContext } from "./GlobalContext";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { BaseSizes } from "@patternfly/react-core/dist/js/styles/sizes";
import { CheckIcon } from "@patternfly/react-icons/dist/js/icons/check-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { GITHUB_OAUTH_TOKEN_SIZE, GITHUB_TOKENS_HOW_TO_URL, GITHUB_TOKENS_URL } from "./GithubService";
import { useOnlineI18n } from "./i18n";
import { I18nHtml } from "@kie-tooling-core/i18n/dist/react-components";

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

export function GithubTokenModal(props: Props) {
  const context = useContext(GlobalContext);
  const { i18n } = useOnlineI18n();

  const [potentialToken, setPotentialToken] = useState(context.githubService.resolveToken());
  const [authenticated, setAuthenticated] = useState(context.githubService.isAuthenticated());
  const [isTokenInvalid, setIsTokenInvalid] = useState(!context.githubService.isAuthenticated());

  const tokenToDisplay = useMemo(() => {
    return obfuscate(context.githubService.resolveToken() || potentialToken);
  }, [context.githubService, potentialToken]);

  const onPasteHandler = useCallback((e) => {
    const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
    setPotentialToken(token);
    context.githubService.authenticate(token).then((isAuthenticated) => {
      setAuthenticated(isAuthenticated);
      setIsTokenInvalid(!isAuthenticated);
    });
  }, []);

  const onResetHandler = useCallback(() => {
    context.githubService.reset();
    setPotentialToken("");
    setAuthenticated(false);
    setIsTokenInvalid(false);
  }, []);

  const validated = useMemo(() => (isTokenInvalid ? "error" : "default"), [isTokenInvalid]);

  useEffect(() => {
    context.githubService.authenticate().then((isAuthenticated) => {
      setAuthenticated(isAuthenticated);
      potentialToken.length === 0 ? setIsTokenInvalid(false) : setIsTokenInvalid(!isAuthenticated);
    });
  }, []);

  return (
    <Modal
      data-testid={"github-token-modal"}
      variant={ModalVariant.small}
      isOpen={props.isOpen}
      onClose={props.onClose}
      aria-label={"Set GitHub token modal"}
      title=""
      header={
        <>
          <Title headingLevel="h1" size={BaseSizes["2xl"]}>
            {i18n.githubTokenModal.header.title}
          </Title>
          <p className="pf-u-pt-sm">{i18n.githubTokenModal.header.subtitle}</p>
        </>
      }
      footer={
        <div className="pf-u-w-100">
          <h3>
            <a href={GITHUB_TOKENS_URL} target={"_blank"}>
              {i18n.githubTokenModal.footer.createNewToken}
              <ExternalLinkAltIcon className="pf-u-mx-sm" />
            </a>
          </h3>
          <InputGroup className="pf-u-mt-sm">
            <TextInput
              id="token-input"
              name="tokenInput"
              aria-describedby="token-text-input-helper"
              placeholder={i18n.githubTokenModal.footer.placeHolder}
              maxLength={GITHUB_OAUTH_TOKEN_SIZE}
              isDisabled={authenticated}
              validated={validated}
              value={tokenToDisplay}
              onPaste={onPasteHandler}
              autoFocus={true}
            />
            {authenticated && (
              <InputGroupText style={{ border: "none", backgroundColor: "#ededed" }}>
                <CheckIcon />
              </InputGroupText>
            )}
          </InputGroup>
          <div className="pf-u-mt-md pf-u-mb-0 pf-u-float-right">
            <Button variant="danger" onClick={onResetHandler}>
              {i18n.terms.reset}
            </Button>
            <Button className="pf-u-ml-sm" variant="primary" onClick={props.onClose}>
              {i18n.terms.close}
            </Button>
          </div>
        </div>
      }
    >
      <>
        <p>
          <span className="pf-u-mr-sm">{i18n.githubTokenModal.body.disclaimer}</span>
          <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"}>
            {i18n.githubTokenModal.body.learnMore}
            <ExternalLinkAltIcon className="pf-u-mx-sm" />
          </a>
        </p>
        <br />
        <p>
          <b>
            <u>{i18n.terms.note.toUpperCase()}:</u>&nbsp;
          </b>
        </p>
        <I18nHtml>{i18n.githubTokenModal.body.note}</I18nHtml>
      </>
    </Modal>
  );
}

function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}
