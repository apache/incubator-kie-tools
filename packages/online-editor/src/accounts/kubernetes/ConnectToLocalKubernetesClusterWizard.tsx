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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { List, ListComponent, ListItem, OrderType } from "@patternfly/react-core/dist/js/components/List";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Wizard, WizardContextConsumer, WizardFooter } from "@patternfly/react-core/dist/js/components/Wizard";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useOnlineI18n } from "../../i18n";
import { KubernetesSettingsTabMode } from "./ConnectToKubernetesSection";
import { KubernetesInstanceStatus } from "./KubernetesInstanceStatus";
import { v4 as uuid } from "uuid";
import { useAuthSessionsDispatch } from "../../authSessions/AuthSessionsContext";
import { KubernetesAuthSession } from "../../authSessions/AuthSessionApi";
import {
  KubernetesConnection,
  KubernetesConnectionStatus,
  isHostValid,
  isKubernetesConnectionValid,
  isNamespaceValid,
  isTokenValid,
} from "@kie-tools-core/kubernetes-bridge/dist/service";
import { OperatingSystem, getOperatingSystem } from "@kie-tools-core/operating-system";
import { SelectOs } from "../../os/SelectOs";
import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";
import { KieSandboxKubernetesService } from "../../devDeployments/services/KieSandboxKubernetesService";
import { Tab, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";
import ExternalLinkAltIcon from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { useRoutes } from "../../navigation/Hooks";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";

enum WizardStepIds {
  CREATE_CLUSTER = "CREATE_CLUSTER",
  CONNECTION_INFO = "CONNECTION_INFO",
  AUTHENTICATION = "AUTHENTICATION",
  CONNECT = "CONNECT",
}

enum KubernetesFlavor {
  KIND = "Kind",
  MINIKUBE = "Minikube",
}

const FLAVOR_INSTALL_DOCS = {
  [KubernetesFlavor.KIND]: "https://kind.sigs.k8s.io/docs/user/quick-start#installation",
  [KubernetesFlavor.MINIKUBE]: "https://minikube.sigs.k8s.io/docs/start",
};

const KUBECTL_INSTALL_DOCS = {
  [OperatingSystem.LINUX]: "https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/",
  [OperatingSystem.MACOS]: "https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/",
  [OperatingSystem.WINDOWS]: "https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/",
};

const COMMANDS = {
  kindCreateClusterUnix: (configUrl: string) => `kind create cluster --config=<(curl -k ${configUrl})`,
  kindCreateClusterWindows: (configUrl: string) =>
    `curl -k ${configUrl} -o kind-cluster-config.yaml; kind create cluster --config=kind-cluster-config.yaml; rm kind-cluster-config.yaml`,
  minikubeCreateCluster: () =>
    `minikube start --extra-config "apiserver.cors-allowed-origins=[https://*]"  --ports 80:80,443:443,8443:8443 --listen-address 0.0.0.0`,
  kindApplyIngress: () =>
    `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml`,
  minikubeApllyIngress: () => `minikube addons enable ingress`,
  sleep: () => `sleep 10`,
  waitForIngress: () =>
    `kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s`,
  applyDeploymentResourcesUnix: (resourcesUrl: string) => `kubectl apply -f <(curl -k ${resourcesUrl})`,
  applyDeploymentResourcesWindows: (resourcesUrl: string) =>
    `curl -k ${resourcesUrl} -o kie-sandbox-resources.yaml; kubectl apply -f kie-sandbox-resources.yaml; rm kie-sandbox-resources.yaml`,
  getSecretUnix: () => `kubectl get secret kie-sandbox-secret -o jsonpath={.data.token} | base64 -d`,
  getSecretWindows: () =>
    `$KubeToken = kubectl get secret kie-sandbox-secret -o jsonpath="{.data.token}"; [System.Text.Encoding]::ASCII.GetString([System.Convert]::FromBase64String(\${KubeToken}))`,
};

const DEFAULT_LOCAL_CLUSTER_NAMESPACE = "local-kie-sandbox-dev-deployments";
const DEFAULT_LOCAL_CLUSTER_HOST = "http://localhost/kube-apiserver";

type ClusterConfigCommands = {
  createCluster: (configUrl?: string) => string;
  applyAndWaitIngress: () => string;
  applyDeploymentResources: (resourcesUrl: string) => string;
  getSecret: () => string;
};

export function ConnectToLocalKubernetesClusterWizard(props: {
  kubernetesService: KieSandboxKubernetesService;
  setMode: React.Dispatch<React.SetStateAction<KubernetesSettingsTabMode>>;
  connection: KubernetesConnection;
  setConnection: React.Dispatch<React.SetStateAction<KubernetesConnection>>;
  status: KubernetesInstanceStatus;
  setStatus: React.Dispatch<React.SetStateAction<KubernetesInstanceStatus>>;
  setNewAuthSession: React.Dispatch<React.SetStateAction<KubernetesAuthSession>>;
}) {
  const { i18n } = useOnlineI18n();
  const routes = useRoutes();
  const [isConnectionValidated, setConnectionValidated] = useState(false);
  const [isConnecting, setConnecting] = useState(false);
  const [isConnectLoading, setConnectLoading] = useState(false);
  const authSessionsDispatch = useAuthSessionsDispatch();
  const [operatingSystem, setOperatingSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [kubernetesFlavor, setKubernetesFlavor] = useState<KubernetesFlavor>(KubernetesFlavor.KIND);
  const [previousConnection, setPreviousConnection] = useState<KubernetesConnection>();

  const onClearHost = useCallback(() => props.setConnection({ ...props.connection, host: "" }), [props]);
  const onClearNamespace = useCallback(() => props.setConnection({ ...props.connection, namespace: "" }), [props]);
  const onClearToken = useCallback(() => props.setConnection({ ...props.connection, token: "" }), [props]);

  const clusterConfigCommands: ClusterConfigCommands = useMemo(() => {
    if (kubernetesFlavor === KubernetesFlavor.KIND) {
      return {
        createCluster:
          operatingSystem === OperatingSystem.WINDOWS
            ? COMMANDS.kindCreateClusterWindows
            : COMMANDS.kindCreateClusterUnix,
        applyAndWaitIngress: () =>
          operatingSystem === OperatingSystem.WINDOWS
            ? `${COMMANDS.kindApplyIngress()}; ${COMMANDS.sleep()}; ${COMMANDS.waitForIngress()}`
            : `${COMMANDS.kindApplyIngress()} && ${COMMANDS.sleep()} && ${COMMANDS.waitForIngress()}`,
        applyDeploymentResources:
          operatingSystem === OperatingSystem.WINDOWS
            ? COMMANDS.applyDeploymentResourcesWindows
            : COMMANDS.applyDeploymentResourcesUnix,
        getSecret: operatingSystem === OperatingSystem.WINDOWS ? COMMANDS.getSecretWindows : COMMANDS.getSecretUnix,
      };
    } else {
      return {
        createCluster: COMMANDS.minikubeCreateCluster,
        applyAndWaitIngress: () =>
          operatingSystem === OperatingSystem.WINDOWS
            ? `${COMMANDS.minikubeApllyIngress()}; ${COMMANDS.sleep()}; ${COMMANDS.waitForIngress()}`
            : `${COMMANDS.minikubeApllyIngress()} && ${COMMANDS.sleep()} && ${COMMANDS.waitForIngress()}`,
        applyDeploymentResources:
          operatingSystem === OperatingSystem.WINDOWS
            ? COMMANDS.applyDeploymentResourcesWindows
            : COMMANDS.applyDeploymentResourcesUnix,
        getSecret: operatingSystem === OperatingSystem.WINDOWS ? COMMANDS.getSecretWindows : COMMANDS.getSecretUnix,
      };
    }
  }, [kubernetesFlavor, operatingSystem]);

  useEffect(() => {
    setPreviousConnection(props.connection);
    props.setConnection({ namespace: DEFAULT_LOCAL_CLUSTER_NAMESPACE, host: DEFAULT_LOCAL_CLUSTER_HOST, token: "" });
  }, []);

  const isNamespaceValidated = useMemo(() => {
    return isNamespaceValid(props.connection.namespace);
  }, [props.connection.namespace]);

  const isHostValidated = useMemo(() => {
    return isHostValid(props.connection.host);
  }, [props.connection.host]);

  const isTokenValidated = useMemo(() => {
    return isTokenValid(props.connection.token);
  }, [props.connection.token]);

  useEffect(() => {
    setConnectionValidated(isKubernetesConnectionValid(props.connection));
  }, [props.connection]);

  const onCancel = useCallback(() => {
    if (previousConnection) {
      props.setConnection(previousConnection);
    }
    props.setMode(KubernetesSettingsTabMode.SIMPLE);
  }, [props, previousConnection]);

  const onNamespaceInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, namespace: newValue }));
    },
    [props]
  );

  const onHostInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, host: newValue }));
    },
    [props]
  );

  const onTokenInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, token: newValue }));
    },
    [props]
  );

  const onStepChanged = useCallback(
    async ({ id }) => {
      if (id === WizardStepIds.CONNECT) {
        setConnectLoading(true);
        setConnectionValidated(
          (await props.kubernetesService.isConnectionEstablished()) === KubernetesConnectionStatus.CONNECTED
        );
        setConnectLoading(false);
      }
    },
    [props.kubernetesService]
  );

  const onSave = useCallback(async () => {
    if (isConnecting) {
      return;
    }

    if (!isKubernetesConnectionValid(props.connection)) {
      return;
    }

    setConnecting(true);
    const isConnectionEstablished = await props.kubernetesService.isConnectionEstablished();
    setConnecting(false);

    if (isConnectionEstablished === KubernetesConnectionStatus.CONNECTED) {
      const newAuthSession: KubernetesAuthSession = {
        type: "kubernetes",
        id: uuid(),
        ...props.connection,
        authProviderId: "kubernetes",
        createdAtDateISO: new Date().toISOString(),
      };
      setConnectionValidated(true);
      props.setStatus(KubernetesInstanceStatus.CONNECTED);
      authSessionsDispatch.add(newAuthSession);
      props.setNewAuthSession(newAuthSession);
    } else {
      setConnectionValidated(false);
      return;
    }
  }, [authSessionsDispatch, isConnecting, props]);

  const firstStepContent = useMemo(
    () => (
      <>
        <br />
        <List component={ListComponent.ol} type={OrderType.number} className="pf-u-mt-md">
          <ListItem>
            <TextContent>
              <Text component={TextVariants.p}>
                <a href={FLAVOR_INSTALL_DOCS[kubernetesFlavor]} target={"_blank"} rel="noreferrer">
                  {i18n.devDeployments.kubernetesConfigWizard.steps.first.installFlavor(kubernetesFlavor)}
                  &nbsp;
                  <ExternalLinkAltIcon className="pf-u-mx-sm" />
                </a>
              </Text>
            </TextContent>
          </ListItem>
          <ListItem>
            <TextContent>
              <Text component={TextVariants.p}>
                <a href={KUBECTL_INSTALL_DOCS[operatingSystem]} target={"_blank"} rel="noreferrer">
                  {i18n.devDeployments.kubernetesConfigWizard.steps.first.installKubectl}
                  &nbsp;
                  <ExternalLinkAltIcon className="pf-u-mx-sm" />
                </a>
              </Text>
            </TextContent>
          </ListItem>
          <ListItem>
            <TextContent>
              <Text component={TextVariants.p}>
                {i18n.devDeployments.kubernetesConfigWizard.steps.first.runCommandsTerminal}
              </Text>
            </TextContent>
            <List component={ListComponent.ol} type={OrderType.lowercaseLetter}>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.devDeployments.kubernetesConfigWizard.steps.first.createCluster}
                  </Text>
                </TextContent>
                <CommandCopyBlock
                  command={clusterConfigCommands.createCluster(
                    `${window.location.origin}${
                      window.location.pathname
                    }${routes.static.kubernetes.kindClusterConfig.path({})}`
                  )}
                />
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.devDeployments.kubernetesConfigWizard.steps.first.installIngress}
                  </Text>
                </TextContent>
                <CommandCopyBlock command={clusterConfigCommands.applyAndWaitIngress()} />
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.devDeployments.kubernetesConfigWizard.steps.first.installKieSandboxYaml}
                  </Text>
                </TextContent>
                <CommandCopyBlock
                  command={clusterConfigCommands.applyDeploymentResources(
                    `${window.location.origin}${
                      window.location.pathname
                    }${routes.static.kubernetes.kieSandboxDevDeploymentsResources.path({})}`
                  )}
                />
              </ListItem>
            </List>
          </ListItem>
        </List>
      </>
    ),
    [
      clusterConfigCommands,
      i18n.devDeployments.kubernetesConfigWizard.steps.first,
      kubernetesFlavor,
      operatingSystem,
      routes.static.kubernetes,
    ]
  );

  const wizardSteps = useMemo(
    () => [
      {
        id: WizardStepIds.CREATE_CLUSTER,
        name: i18n.devDeployments.kubernetesConfigWizard.steps.first.name,
        component: (
          <div>
            <Text component={TextVariants.p}>
              {i18n.devDeployments.kubernetesConfigWizard.steps.first.introduction}
            </Text>
            <Tabs
              activeKey={kubernetesFlavor}
              onSelect={(_, flavor) => setKubernetesFlavor(flavor as KubernetesFlavor)}
              isVertical={false}
              isBox={false}
            >
              <Tab
                className="kie-tools--settings-tab"
                eventKey={KubernetesFlavor.KIND}
                title={<TabTitleText>Kind</TabTitleText>}
              >
                {firstStepContent}
              </Tab>
              <Tab
                className="kie-tools--settings-tab"
                eventKey={KubernetesFlavor.MINIKUBE}
                title={<TabTitleText>Minikube</TabTitleText>}
              >
                {firstStepContent}
              </Tab>
            </Tabs>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECTION_INFO,
        name: i18n.devDeployments.kubernetesConfigWizard.steps.second.name,
        component: (
          <div>
            <Text component={TextVariants.p}>
              {i18n.devDeployments.kubernetesConfigWizard.steps.second.introduction}
            </Text>
            <Text component={TextVariants.small} style={{ color: "var(--pf-global--palette--red-100)" }}>
              {i18n.devDeployments.kubernetesConfigWizard.steps.second.disclaimer}
            </Text>
            <br />
            <br />
            <Form className="pf-u-mt-md" onSubmit={(e) => e.preventDefault()}>
              <FormGroup
                fieldId={"dev-deployments-config-namespace"}
                label={i18n.devDeployments.kubernetesConfigWizard.fields.namespace}
                validated={isNamespaceValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    type="text"
                    id="namespace-field"
                    name="namespace-field"
                    aria-label="namespace field"
                    value={props.connection.namespace}
                    placeholder={i18n.devDeployments.kubernetesConfigWizard.steps.second.namespacePlaceholder}
                    onChange={onNamespaceInputChanged}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear namespace button" onClick={onClearNamespace}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <Text component={TextVariants.p}>
                {i18n.devDeployments.kubernetesConfigWizard.steps.second.namespaceInputReason}
              </Text>
              <FormGroup
                fieldId={"dev-deployments-config-host"}
                label={i18n.devDeployments.kubernetesConfigWizard.fields.kubernetesApiServerUrl}
                validated={isHostValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="host-field"
                    name="host-field"
                    aria-label="Host field"
                    value={props.connection.host}
                    placeholder={i18n.devDeployments.kubernetesConfigWizard.steps.second.hostPlaceholder}
                    onChange={onHostInputChanged}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <Text component={TextVariants.p}>
                {i18n.devDeployments.kubernetesConfigWizard.steps.second.hostInputReason}
              </Text>
            </Form>
          </div>
        ),
      },
      {
        id: WizardStepIds.AUTHENTICATION,
        name: i18n.devDeployments.kubernetesConfigWizard.steps.third.name,
        component: (
          <div>
            <Text component={TextVariants.p}>
              {i18n.devDeployments.kubernetesConfigWizard.steps.third.introduction}
            </Text>
            <br />
            <TextContent>
              <Text component={TextVariants.p}>{i18n.devDeployments.kubernetesConfigWizard.steps.third.getToken}</Text>
            </TextContent>
            <CommandCopyBlock command={clusterConfigCommands.getSecret()} />
            <br />
            <br />
            <Form className="pf-u-mt-md">
              <FormGroup
                fieldId={"dev-deployments-config-token"}
                label={i18n.terms.token}
                validated={isTokenValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="token-field"
                    name="token-field"
                    aria-label="Token field"
                    value={props.connection.token}
                    placeholder={i18n.devDeployments.kubernetesConfigWizard.steps.third.tokenPlaceholder}
                    onChange={onTokenInputChanged}
                    tabIndex={2}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearToken}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
            </Form>
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.devDeployments.kubernetesConfigWizard.steps.third.tokenInputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECT,
        name: i18n.devDeployments.kubernetesConfigWizard.steps.final.name,
        component: (
          <>
            {isConnectLoading && (
              <div className="kogito--editor__dev-deployments-wizard-loading-spinner">
                <Spinner isSVG size="xl" />
              </div>
            )}
            {!isConnectLoading && isConnectionValidated && (
              <div>
                <Alert
                  variant={"default"}
                  isInline={true}
                  title={i18n.devDeployments.kubernetesConfigWizard.steps.final.connectionSuccess}
                />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.kubernetesConfigWizard.steps.final.introduction}
                </Text>
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.kubernetesConfigWizard.steps.final.configNote}
                </Text>
              </div>
            )}
            {!isConnectLoading && !isConnectionValidated && (
              <div>
                <Alert
                  variant={"danger"}
                  isInline={true}
                  title={i18n.devDeployments.kubernetesConfigWizard.steps.final.connectionError}
                />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.kubernetesConfigWizard.steps.final.connectionErrorLong}
                </Text>
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.kubernetesConfigWizard.steps.final.possibleErrorReasons.introduction}
                </Text>
                <br />
                <List className="pf-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.kubernetesConfigWizard.steps.final.possibleErrorReasons.emptyField}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {
                          i18n.devDeployments.kubernetesConfigWizard.steps.final.possibleErrorReasons
                            .clusterNotCreatedCorrectly
                        }
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.kubernetesConfigWizard.steps.final.possibleErrorReasons.tokenExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.kubernetesConfigWizard.steps.final.checkInfo}
                </Text>
              </div>
            )}
          </>
        ),
      },
    ],
    [
      i18n,
      kubernetesFlavor,
      firstStepContent,
      isNamespaceValidated,
      props.connection.namespace,
      props.connection.host,
      props.connection.token,
      onNamespaceInputChanged,
      onClearNamespace,
      isHostValidated,
      onHostInputChanged,
      onClearHost,
      isTokenValidated,
      onTokenInputChanged,
      onClearToken,
      isConnectLoading,
      isConnectionValidated,
      clusterConfigCommands,
    ]
  );

  const wizardFooter = useMemo(
    () => (
      <WizardFooter>
        <WizardContextConsumer>
          {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
            if (activeStep.name !== i18n.devDeployments.kubernetesConfigWizard.steps.final.name) {
              return (
                <>
                  <Button variant="primary" onClick={onNext}>
                    {i18n.terms.next}
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={onBack}
                    isDisabled={activeStep.name === i18n.devDeployments.kubernetesConfigWizard.steps.first.name}
                  >
                    {i18n.terms.back}
                  </Button>
                  <Button variant="link" onClick={onCancel}>
                    {i18n.terms.cancel}
                  </Button>
                </>
              );
            }
            // Final step buttons
            return (
              <>
                <Button
                  id="dev-deployments-config-continue-editing-button"
                  onClick={onSave}
                  isDisabled={!isConnectionValidated}
                  variant={ButtonVariant.primary}
                  isLoading={isConnecting}
                  spinnerAriaValueText={isConnecting ? "Loading" : undefined}
                >
                  {isConnecting ? i18n.devDeployments.common.saving : i18n.terms.save}
                </Button>
                <Button variant="secondary" onClick={onBack}>
                  {i18n.terms.back}
                </Button>
                <Button variant="link" onClick={onCancel}>
                  {i18n.terms.cancel}
                </Button>
              </>
            );
          }}
        </WizardContextConsumer>
      </WizardFooter>
    ),
    [i18n, isConnectionValidated, isConnecting, onCancel, onSave]
  );

  return (
    <div>
      <Form isHorizontal={true}>
        <FormGroup fieldId={"select-os"} label={i18n.terms.os.full}>
          <SelectOs selected={operatingSystem} onSelect={setOperatingSystem} direction={SelectDirection.down} />
        </FormGroup>
      </Form>
      <br />
      <Wizard steps={wizardSteps} footer={wizardFooter} onNext={onStepChanged} onGoToStep={onStepChanged} />
    </div>
  );
}

function CommandCopyBlock(props: { command: string }) {
  const onCopy = useCallback(() => {
    navigator.clipboard.writeText(props.command);
  }, [props.command]);

  return (
    <ClipboardCopy
      isReadOnly
      hoverTip="Copy"
      clickTip="Copied"
      variant={ClipboardCopyVariant.expansion}
      isCode
      onCopy={onCopy}
      className="kie-sandbox--copy-command-block"
    >
      {`$ ${props.command}`}
    </ClipboardCopy>
  );
}
