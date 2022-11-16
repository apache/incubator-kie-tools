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

import * as React from "react";
import { getSize, IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import OpenshiftIcon from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import QuestionIcon from "@patternfly/react-icons/dist/js/icons/question-icon";
import UsersIcon from "@patternfly/react-icons/dist/js/icons/users-icon";
import { AuthProvider } from "./AuthProvidersApi";

export function AuthProviderIcon(props: {
  authProvider: AuthProvider | undefined;
  size: IconSize | keyof typeof IconSize;
}) {
  if (!props.authProvider) {
    return <UsersIcon size={props.size} />;
  }

  if (props.authProvider.iconPath) {
    const baseAlign = -0.125 * Number.parseFloat(getSize(props.size)); // Copied from PatternFly
    return (
      <img style={{ height: getSize(props.size), verticalAlign: `${baseAlign}em` }} src={props.authProvider.iconPath} />
    );
  }

  if (props.authProvider.type === "github") {
    return <GithubIcon size={props.size} />;
  }

  if (props.authProvider.type === "bitbucket") {
    return <BitbucketIcon size={props.size} />;
  }

  if (props.authProvider.type === "gitlab") {
    return <GitlabIcon size={props.size} />;
  }

  if (props.authProvider.type === "openshift") {
    return <OpenshiftIcon size={props.size} />;
  }

  return <QuestionIcon size={props.size} />;
}
