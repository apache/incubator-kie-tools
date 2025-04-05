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

import * as React from "react";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import OpenshiftIcon from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import QuestionIcon from "@patternfly/react-icons/dist/js/icons/question-icon";
import UsersIcon from "@patternfly/react-icons/dist/js/icons/users-icon";
import { AuthProvider } from "./AuthProvidersApi";
import { Icon, IconComponentProps } from "@patternfly/react-core/dist/js/components/Icon";

export type AuthProviderIconProps = Pick<IconComponentProps, "size"> & {
  authProvider: AuthProvider | undefined;
};
export function AuthProviderIcon(props: AuthProviderIconProps) {
  if (!props.authProvider) {
    return (
      <Icon size={props.size}>
        <UsersIcon />
      </Icon>
    );
  }

  if (props.authProvider.type === "github") {
    return (
      <Icon size={props.size}>
        <GithubIcon />
      </Icon>
    );
  }

  if (props.authProvider.type === "bitbucket") {
    return (
      <Icon size={props.size}>
        <BitbucketIcon color="blue" />
      </Icon>
    );
  }

  if (props.authProvider.type === "gitlab") {
    return (
      <Icon size={props.size}>
        <GitlabIcon color="orange" />
      </Icon>
    );
  }

  if (props.authProvider.type === "openshift") {
    return (
      <Icon size={props.size}>
        <OpenshiftIcon color="red" />
      </Icon>
    );
  }

  return (
    <Icon size={props.size}>
      <QuestionIcon />
    </Icon>
  );
}
