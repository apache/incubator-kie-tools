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
import { useCallback, useContext, useState, useMemo } from "react";
import { GlobalContext } from "./GlobalContext";
import {
  Modal,
  ModalVariant,
  Button,
  BaseSizes,
  Title,
  InputGroup,
  TextInput,
  InputGroupText,
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, CheckIcon } from "@patternfly/react-icons";
import { GITHUB_OAUTH_TOKEN_SIZE, GITHUB_TOKENS_URL, GITHUB_TOKENS_HOW_TO_URL } from "./GithubService";
import { useOnlineI18n } from "./i18n";
import { I18nHtml } from "@kogito-tooling/i18n";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onContinue: () => void;
}

export function GithubTokenModal(props: Props) {
  const context = useContext(GlobalContext);
  const { i18n } = useOnlineI18n();

  const [potentialToken, setPotentialToken] = useState(context.githubService.resolveToken());
  const [authenticated, setAuthenticated] = useState(context.githubService.isAuthenticated());

  const tokenToDisplay = useMemo(() => {
    return obfuscate(context.githubService.resolveToken() || potentialToken);
  }, [context.githubService, potentialToken]);

  const onPasteHandler = useCallback(e => {
    const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
    setPotentialToken(token);
    context.githubService.authenticate(token).then(isAuthenticated => setAuthenticated(isAuthenticated));
  }, []);

  const onResetHandler = useCallback(() => {
    context.githubService.reset();
    setPotentialToken("");
    setAuthenticated(false);
  }, []);

  return (
    <Modal
      variant={ModalVariant.small}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title=""
      header={
        <>
          <Title headingLevel="h1" size={BaseSizes['2xl']}>
            <I18nHtml>{i18n.githubTokenModal.header.title}</I18nHtml>
          </Title>
          <p className="pf-u-pt-sm">{i18n.githubTokenModal.header.subtitle}</p>
        </>
      }
      footer={
        <div className="pf-u-w-100">
          <h3>
            <a href={GITHUB_TOKENS_URL} target={"_blank"}>
              <I18nHtml>{i18n.githubTokenModal.footer.createNewToken}</I18nHtml>
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
              validated={!!authenticated ? "error" : "default"}
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
              <I18nHtml>{i18n.terms.reset}</I18nHtml>
            </Button>
            <Button className="pf-u-ml-sm" variant="primary" isDisabled={!authenticated} onClick={props.onContinue}>
              <I18nHtml>{i18n.terms.continue}</I18nHtml>
            </Button>
          </div>
        </div>
      }
    >
      <>
        <p>
          <span className="pf-u-mr-sm">{i18n.githubTokenModal.body.disclaimer}</span>
          <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"}>
            <I18nHtml>{i18n.githubTokenModal.body.learnMore}</I18nHtml>
            <ExternalLinkAltIcon className="pf-u-mx-sm" />
          </a>
        </p>
        <br />
        <p>
          <b>
            <u>{i18n.terms.note.toUpperCase()}:</u>&nbsp;
          </b>
          <I18nHtml>{i18n.githubTokenModal.body.note}</I18nHtml>
        </p>
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
