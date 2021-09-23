/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { when } from "jest-when";
import { OpenShiftService } from "../../../settings/OpenShiftService";
import { CreateDeployment, Deployment, ListDeployments } from "../../../settings/resources/Deployment";
import { CreateRoute, ListRoutes, Route } from "../../../settings/resources/Route";
import { Build, CreateBuild, ListBuilds } from "../../../settings/resources/Build";
import { GetProject } from "../../../settings/resources/Project";
import { CreateImageStream } from "../../../settings/resources/ImageStream";
import { CreateService } from "../../../settings/resources/Service";
import { CreateBuildConfig } from "../../../settings/resources/BuildConfig";
import { ResourceFetch } from "../../../settings/resources/Resource";

describe("DmnDevSandboxService", () => {
  const service = new OpenShiftService("proxyUrl");
  const fetchResourceFn = jest.spyOn(service, "fetchResource");

  const config = {
    namespace: "test-username",
    host: "http://localhost:8080",
    token: "test-token",
  };

  function createDeployment(id: string, createdBy?: string): Deployment {
    return {
      metadata: {
        uid: `uid-${id}`,
        name: `deployment-${id}`,
        labels: { "kogito.kie.org/created-by": createdBy ?? "other" },
        annotations: { "kogito.kie.org/filename": `myModel-${id}.dmn` },
        creationTimestamp: new Date().toISOString(),
      },
      status: {
        replicas: 1,
      },
    };
  }

  function createBuild(id: string): Build {
    return {
      metadata: {
        uid: `uid-${id}`,
        name: `deployment-${id}`,
        labels: {},
        annotations: {},
        creationTimestamp: new Date().toISOString(),
      },
      status: {
        phase: "Complete",
      },
    };
  }

  function createRoute(id: string): Route {
    return {
      metadata: {
        uid: `uid-${id}`,
        name: `deployment-${id}`,
        labels: {},
        annotations: {},
        creationTimestamp: new Date().toISOString(),
      },
      spec: {
        host: "test-host",
      },
    };
  }

  beforeEach(() => {
    jest.resetAllMocks();
  });

  it("should return true when the connection is established", async () => {
    when(fetchResourceFn).calledWith(expect.any(GetProject)).mockReturnValueOnce(Promise.resolve({}));
    expect(await service.isConnectionEstablished(config)).toBeTruthy();
  });

  it("should return false when the connection is not established", async () => {
    when(fetchResourceFn)
      .calledWith(expect.any(GetProject))
      .mockImplementationOnce((target: ResourceFetch, rollbacks?: ResourceFetch[]) => {
        throw new Error();
      });
    expect(await service.isConnectionEstablished(config)).toBeFalsy();
  });

  it("should fetch all resources when the deploy is successful", async () => {
    when(fetchResourceFn)
      .calledWith(expect.any(CreateBuildConfig), expect.anything())
      .mockReturnValueOnce(Promise.resolve({ metadata: { uid: "uid" } }));

    when(fetchResourceFn)
      .calledWith(expect.any(CreateRoute), expect.anything())
      .mockReturnValueOnce(Promise.resolve({ metadata: { uid: "uid" }, spec: { host: "host" } }));

    await service.deploy({
      filename: "myModel.dmn",
      editorContent: "diagramContent",
      config,
      onlineEditorUrl: () => "",
    });

    expect(fetchResourceFn).toHaveBeenCalledTimes(6);
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateImageStream));
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateService), expect.anything());
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateRoute), expect.anything());
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateBuildConfig), expect.anything());
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateBuild), expect.anything());
    expect(fetchResourceFn).toHaveBeenCalledWith(expect.any(CreateDeployment), expect.anything());
  });

  it("should return an empty array in case there is no deployments to list", async () => {
    when(fetchResourceFn)
      .calledWith(expect.any(ListDeployments))
      .mockReturnValueOnce(Promise.resolve({ items: [] }));

    const deployments = await service.loadDeployments(config);

    expect(deployments).toStrictEqual([]);
    expect(fetchResourceFn).toHaveBeenCalledTimes(1);
    expect(fetchResourceFn).not.toHaveBeenCalledWith(expect.any(ListBuilds));
  });

  it("should filter deployments created by online-editor", async () => {
    const deployments = [
      createDeployment("1"),
      createDeployment("2", "online-editor"),
      createDeployment("3", "online-editor"),
      createDeployment("4", "online-editor"),
      createDeployment("5"),
      createDeployment("6"),
    ];

    const builds = [
      createBuild("1"),
      createBuild("2"),
      createBuild("3"),
      createBuild("4"),
      createBuild("5"),
      createBuild("6"),
    ];

    const routes = [
      createRoute("1"),
      createRoute("2"),
      createRoute("3"),
      createRoute("4"),
      createRoute("5"),
      createRoute("6"),
    ];

    when(fetchResourceFn)
      .calledWith(expect.any(ListDeployments))
      .mockReturnValueOnce(
        Promise.resolve({
          items: deployments,
        })
      );

    when(fetchResourceFn)
      .calledWith(expect.any(ListBuilds))
      .mockReturnValueOnce(
        Promise.resolve({
          items: builds,
        })
      );

    when(fetchResourceFn)
      .calledWith(expect.any(ListRoutes))
      .mockReturnValueOnce(
        Promise.resolve({
          items: routes,
        })
      );

    expect((await service.loadDeployments(config)).length).toBe(3);
  });
});
