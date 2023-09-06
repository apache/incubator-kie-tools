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

import React, { createContext, PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import {
  AuthenticatedUserResponse,
  fetchAuthenticatedBitbucketUser,
  fetchAuthenticatedGitHubUser,
} from "../accounts/git/ConnectToGitSection";
import { KieSandboxOpenShiftService } from "../devDeployments/services/KieSandboxOpenShiftService";
import {
  GitAuthProvider,
  SupportedGitAuthProviders,
  isGitAuthProvider,
  isSupportedGitAuthProviderType,
} from "../authProviders/AuthProvidersApi";
import { switchExpression } from "../switchExpression/switchExpression";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { useEnv } from "../env/hooks/EnvContext";
import { KubernetesService, KubernetesServiceArgs } from "../devDeployments/services/KubernetesService";
import { KieSandboxDeploymentService, KieSandboxDeploymentServiceProps } from "./services/types";
import { useAuthSessions } from "../authSessions/AuthSessionsContext";
import { CloudAuthSession, CloudAuthSessionType, isCloudAuthSession } from "../authSessions/AuthSessionApi";
import { KieSandboxKubernetesService } from "./services/KieSandboxKubernetesService";

export type DevDeploymentsServicesContextType = {
  devDeploymentsServices: Map<string, KieSandboxDeploymentService>;
  devDeploymentsServicesStatus: Map<string, KubernetesConnectionStatus>;
};

export type DevDeploymentsServicesDispatchContextType = {
  recalculateDevDeploymentsServicesStatus: () => void;
  add: (devDeploymentsService: KieSandboxDeploymentService) => void;
  remove: (devDeploymentsService: KieSandboxDeploymentService) => void;
};

const DevDeploymentsServicesContext = createContext<DevDeploymentsServicesContextType>({} as any);
const DevDeploymentsServicesDispatchContext = createContext<DevDeploymentsServicesDispatchContextType>({} as any);

export function useDevDeploymentsServices() {
  return useContext(DevDeploymentsServicesContext);
}

export function useDevDeploymentsServicesDispatch() {
  return useContext(DevDeploymentsServicesDispatchContext);
}

const fsCache = new LfsFsCache();
const fsService = new LfsStorageService();
const broadcastChannel = new BroadcastChannel("dev_deployments_services");

const DEV_DEPLOYMENT_SERVICES_FILE_PATH = "/devDeploymentsServices.json";
const DEV_DEPLOYMENT_SERVICES_FS_NAME = "dev_deployments_services";

export function DevDeploymentsServicesContextProvider(props: PropsWithChildren<{}>) {
  const { env } = useEnv();
  const { authSessions } = useAuthSessions();
  const [devDeploymentsServices, setDevDeploymentsServices] = useState<Map<string, KieSandboxDeploymentService>>(
    new Map()
  );
  const [devDeploymentsServicesStatus, setDevDeploymentsServicesStatus] = useState<
    Map<string, KubernetesConnectionStatus>
  >(new Map());

  console.log(devDeploymentsServices);

  const refresh = useCallback(async () => {
    console.log("Refreshing!");
    const fs = fsCache.getOrCreateFs(DEV_DEPLOYMENT_SERVICES_FS_NAME);
    const content = await (await fsService.getFile(fs, DEV_DEPLOYMENT_SERVICES_FILE_PATH))?.getFileContents();

    console.log("Raw content", content);

    console.log("Parsed content", JSON.parse(decoder.decode(content)));

    const parsedContent = JSON.parse(decoder.decode(content));

    const mapItems = parsedContent.map((item: [string, KieSandboxDeploymentServiceProps]) => {
      const [_, props] = item;
      console.log({ item });
      return [
        props.id,
        switchExpression(props.type, {
          [CloudAuthSessionType.Kubernetes]: new KieSandboxKubernetesService(props.args, props.id),
          [CloudAuthSessionType.OpenShift]: new KieSandboxOpenShiftService(props.args, props.id),
        }),
      ];
    });

    console.log("MapItems", mapItems);

    setDevDeploymentsServices(new Map(mapItems));
  }, []);

  const persistdevDeploymentsServices = useCallback(
    async (map: Map<string, KieSandboxDeploymentService>) => {
      const fs = fsCache.getOrCreateFs(DEV_DEPLOYMENT_SERVICES_FS_NAME);
      await fsService.createOrOverwriteFile(
        fs,
        new LfsStorageFile({
          path: DEV_DEPLOYMENT_SERVICES_FILE_PATH,
          getFileContents: async () => encoder.encode(JSON.stringify([...map.entries()])),
        })
      );

      // This goes to other broadcast channel instances, on other tabs
      broadcastChannel.postMessage("UPDATE_DEV_DEPLOYMENTS_SERVICES");

      // This updates this tab
      refresh();
    },
    [refresh]
  );

  const add = useCallback(
    (devDeploymentsService: KieSandboxDeploymentService) => {
      console.log("Adding", devDeploymentsService);
      const n = new Map(devDeploymentsServices?.entries() ?? []);
      n?.set(devDeploymentsService.id, devDeploymentsService);
      persistdevDeploymentsServices(n);
    },
    [devDeploymentsServices, persistdevDeploymentsServices]
  );

  const remove = useCallback(
    (devDeploymentsService: KieSandboxDeploymentService) => {
      console.log("Removing", devDeploymentsService);
      const n = new Map(devDeploymentsServices?.entries() ?? []);
      n?.delete(devDeploymentsService.id);
      persistdevDeploymentsServices(n);
    },
    [devDeploymentsServices, persistdevDeploymentsServices]
  );

  const getService = useCallback(
    async (authSession: CloudAuthSession) => {
      if (authSession.type === "openshift") {
        const k8sApiServerEndpointsByResourceKind = await KubernetesService.getK8sApiServerEndpointsMap({
          connection: authSession,
          proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
        });
        return new KieSandboxOpenShiftService(
          {
            connection: authSession,
            proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
            k8sApiServerEndpointsByResourceKind,
          },
          authSession.id
        );
      } else if (authSession.type === "kubernetes") {
        const k8sApiServerEndpointsByResourceKind = await KubernetesService.getK8sApiServerEndpointsMap({
          connection: authSession,
        });
        return new KieSandboxKubernetesService(
          {
            connection: authSession,
            k8sApiServerEndpointsByResourceKind,
          },
          authSession.id
        );
      }
      throw new Error("Invalid AuthSession type.");
    },
    [env.KIE_SANDBOX_CORS_PROXY_URL]
  );

  useEffect(() => {
    const updateDevDeploymentServices = async () => {
      const authSessionsIds = Array.from(authSessions.keys());
      const devDeploymentsServicesIds = Array.from(devDeploymentsServices.keys());

      const added = authSessionsIds.filter((element) => !devDeploymentsServicesIds.includes(element));
      const removed = devDeploymentsServicesIds.filter((element) => !authSessionsIds.includes(element));

      added.forEach(async (authSessionId) => {
        const authSession = authSessions.get(authSessionId);
        if (!authSession || !isCloudAuthSession(authSession)) {
          return;
        }
        try {
          add(await getService(authSession));
        } catch (e) {
          console.error(`Failed to create service for authSession: ${authSessionId}`);
        }
      });

      removed.forEach(async (authSessionId) => {
        const devDeploymentService = devDeploymentsServices.get(authSessionId);
        if (!devDeploymentService) {
          return;
        }
        try {
          remove(devDeploymentService);
        } catch (e) {
          console.error(`Failed to remove service for authSession: ${authSessionId}`);
        }
      });
    };
    updateDevDeploymentServices();
  }, [add, authSessions, devDeploymentsServices, getService, remove]);

  // Update after persisted
  useEffect(() => {
    broadcastChannel.onmessage = refresh;
  }, [refresh]);

  // Init
  useEffect(() => {
    async function run() {
      const fs = fsCache.getOrCreateFs(DEV_DEPLOYMENT_SERVICES_FS_NAME);
      if (!(await fsService.exists(fs, DEV_DEPLOYMENT_SERVICES_FILE_PATH))) {
        await persistdevDeploymentsServices(new Map());
      } else {
        refresh();
      }
    }

    run();
  }, [persistdevDeploymentsServices, refresh]);

  const recalculateDevDeploymentsServicesStatus = useCallback(
    (args?: { canceled: Holder<boolean> }) => {
      async function run() {
        const newDevDeploymentServicesStatus: [string, KubernetesConnectionStatus][] = await Promise.all(
          [...(devDeploymentsServices?.values() ?? [])].map(async (devDeploymentService) => {
            return [devDeploymentService.id, await devDeploymentService.isConnectionEstablished()];
          })
        );

        if (args?.canceled.get()) {
          return;
        }

        setDevDeploymentsServicesStatus(new Map(newDevDeploymentServicesStatus));
      }
      run();
    },
    [devDeploymentsServices]
  );

  useCancelableEffect(recalculateDevDeploymentsServicesStatus);

  const dispatch = useMemo(() => {
    return { add, remove, recalculateDevDeploymentsServicesStatus };
  }, [add, remove, recalculateDevDeploymentsServicesStatus]);

  const value = useMemo(() => {
    return devDeploymentsServices && devDeploymentsServicesStatus
      ? { devDeploymentsServices, devDeploymentsServicesStatus }
      : undefined;
  }, [devDeploymentsServicesStatus, devDeploymentsServices]);

  return (
    <>
      {value && (
        <DevDeploymentsServicesContext.Provider value={value}>
          <DevDeploymentsServicesDispatchContext.Provider value={dispatch}>
            {props.children}
          </DevDeploymentsServicesDispatchContext.Provider>
        </DevDeploymentsServicesContext.Provider>
      )}
    </>
  );
}

export function useDevDeploymentService(
  devDeploymentServiceId: string | undefined
): KieSandboxDeploymentService | undefined {
  const { devDeploymentsServices } = useDevDeploymentsServices();

  const devDeploymentService = useMemo(() => {
    if (!devDeploymentServiceId) {
      return undefined;
    } else {
      return devDeploymentsServices.get(devDeploymentServiceId);
    }
  }, [devDeploymentServiceId, devDeploymentsServices]);

  return devDeploymentService;
}
