import * as React from "react";
import { useMemo } from "react";
import { AuthSession } from "../authSessions/AuthSessionApi";

export type OpenShiftAuthProvider = {
  id: string;
  type: "openshift";
  name: string;
  domain: undefined;
  iconPath?: string;
  enabled: true;
};

export type GitAuthProvider = {
  id: string;
  type: "github" | "bitbucket" | "gitlab";
  name: string;
  domain: string;
  iconPath?: string;
  enabled: boolean;
  supportedGitRemoteDomains: string[];
};

export type AuthProvider = OpenShiftAuthProvider | GitAuthProvider;

export const AUTH_PROVIDERS: AuthProvider[] = [
  {
    id: "github_dot_com", // Primary Key
    domain: "github.com",
    supportedGitRemoteDomains: ["github.com", "gist.github.com"],
    type: "github",
    name: "GitHub",
    enabled: true,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "gitlab_dot_com",
    domain: "gitlab.com",
    supportedGitRemoteDomains: ["gitlab.com"],
    type: "gitlab",
    name: "GitLab",
    enabled: false,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "bitbucket_dot_com",
    domain: "bitbucket.com",
    supportedGitRemoteDomains: ["bitbucket.com"],
    type: "bitbucket",
    name: "Bitbucket",
    enabled: false,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "github_at_ibm",
    domain: "github.ibm.com",
    supportedGitRemoteDomains: ["github.ibm.com", "gist.github.ibm.com"],
    type: "github",
    name: "GitHub @ IBM",
    enabled: true,
    iconPath: "assets/ibm-github-icon.png", // Always relative path
  },
  //   {
  //     id: "bitbucket_at_my_customer",
  //     domain: "bitbucket.my-customer.com",
  //     type: "bitbucket",
  //     name: "Bitbucket @ My customer",
  //     enabled: false,
  //     // iconPath: "assets/bitbucket-my-customer.png", // Always relative path
  //   },
  //   {
  //     id: "github_at_my_partner",
  //     domain: "my-partner.ibm.com",
  //     type: "github",
  //     name: "GitHub @ My partner",
  //     enabled: false,
  //     // iconPath: "assets/my-partner-github-icon.png", // Always relative path
  //   },
  //   {
  //     id: "openshift",
  //     type: "openshift",
  //     name: "OpenShift cluster",
  //     domain: undefined,
  //     enabled: true,
  //   },
];

export function useAuthProviders() {
  return useMemo<AuthProvider[]>(() => AUTH_PROVIDERS, []);
}

export function useAuthProvider(authSession: AuthSession | undefined) {
  const authProviders = useAuthProviders();
  if (authSession?.type === "none") {
    return undefined;
  }

  return authProviders.find((a) => a.id === authSession?.authProviderId);
}
