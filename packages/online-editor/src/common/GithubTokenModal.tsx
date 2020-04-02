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
  Button,
  BaseSizes,
  Title,
  TitleLevel,
  InputGroup,
  TextInput,
  InputGroupText
} from "@patternfly/react-core";
import { ExternalLinkAltIcon, CheckIcon } from "@patternfly/react-icons";

import {
  GITHUB_OAUTH_TOKEN_SIZE,
  GITHUB_TOKENS_URL,
  GITHUB_TOKENS_HOW_TO_URL
} from './GithubService';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onContinue: () => void;
}

export function GithubTokenModal(props: Props) {
  const context = useContext(GlobalContext);

  const [potentialToken, setPotentialToken] = useState("");
  const [authenticated, setAuthenticated] = useState(context.githubService.isAuthenticated());

  const tokenToDisplay = useMemo(() => {
    return obfuscate(context.githubService.resolveToken() || potentialToken);
  }, [context.githubService, potentialToken]);

  const onPasteHandler = useCallback(e => {
    const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
    setPotentialToken(token);
    context.githubService.authenticate(token)
      .then(isAuthenticated => setAuthenticated(isAuthenticated))
  }, []);

  const onResetHandler = useCallback(() => {
    context.githubService.reset();
    setPotentialToken("");
    setAuthenticated(false);
  }, []);

  return (
    <Modal
      isSmall={true}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title=""
      header={
        <>
          <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
            GitHub OAuth Token
          </Title>
          <p className="pf-u-pt-sm">Authentication required for exporting to GitHub gist.</p>
        </>
      }
      footer={
        <div className="pf-u-w-100">
          <h3>
            <a href={GITHUB_TOKENS_URL} target={"_blank"}>
              Create a new token<ExternalLinkAltIcon className="pf-u-mx-sm" />
            </a>
          </h3>
          <InputGroup className="pf-u-mt-sm">
            <TextInput
              id="token-input"
              name="tokenInput"
              aria-describedby="token-text-input-helper"
              placeholder="Paste your token here"
              maxLength={GITHUB_OAUTH_TOKEN_SIZE}
              isDisabled={authenticated}
              isValid={!!authenticated}
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
            <Button
              variant="danger"
              onClick={onResetHandler}>
              Reset
            </Button>
            <Button
              className="pf-u-ml-sm"
              variant="primary"
              isDisabled={!authenticated}
              onClick={props.onContinue}>
              Continue
            </Button>
          </div>
        </div >
      }
    >
      <>
        <p>
          <span className="pf-u-mr-sm">
            By authenticating with your OAuth Token we are able to create gists
            so you can share your diagrams with your colleagues.
            The token you provide is locally stored as browser cookies and it is never shared with anyone.
          </span>
          <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"}>
            Learn more about GitHub tokens<ExternalLinkAltIcon className="pf-u-mx-sm" />
          </a>
        </p>
        <br />
        <p>
          <b><u>NOTE:</u>&nbsp;</b>
          You should provide a token with the <b>'gist'</b> permission.
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