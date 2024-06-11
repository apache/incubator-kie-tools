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

import { GIT_CONSTS, GIT_HTTP_METHODS, isGitOperation } from "../../src/proxy/git";

const baseGitURL = "https://github.com/apache/incubator-kie-tools.git";

describe("Git isGitOperation tests", () => {
  it("Non valid Git Operations", () => {
    expect(
      isGitOperation(
        "https://oc.test/apis/apps/v1/namespaces/dev/deployments?labelSelector=kogito.kie.org/created-by",
        "GET",
        {
          accept: "application/json",
          authorization: "Bearer ABC",
        }
      )
    ).toBeFalsy();
  }),
    it("isPreflightInfoRefs", () => {
      expect(
        isGitOperation(
          `${baseGitURL}/${GIT_CONSTS.INFO_REFS}?service=${GIT_CONSTS.GIT_UPLOAD_PACK}`,
          GIT_HTTP_METHODS.OPTIONS
        )
      ).toBeTruthy();
      expect(
        isGitOperation(
          `${baseGitURL}/${GIT_CONSTS.INFO_REFS}?service=${GIT_CONSTS.GIT_RECEIVE_PACK}`,
          GIT_HTTP_METHODS.OPTIONS
        )
      ).toBeTruthy();
    });

  it("isInfoRefs", () => {
    expect(
      isGitOperation(
        `${baseGitURL}/${GIT_CONSTS.INFO_REFS}?service=${GIT_CONSTS.GIT_UPLOAD_PACK}`,
        GIT_HTTP_METHODS.GET
      )
    ).toBeTruthy();
    expect(
      isGitOperation(
        `${baseGitURL}/${GIT_CONSTS.INFO_REFS}?service=${GIT_CONSTS.GIT_RECEIVE_PACK}`,
        GIT_HTTP_METHODS.GET
      )
    ).toBeTruthy();
  });

  it("isPreflightPull", () => {
    expect(
      isGitOperation(`${baseGitURL}/${GIT_CONSTS.GIT_UPLOAD_PACK}`, GIT_HTTP_METHODS.OPTIONS, {
        [GIT_CONSTS.ACCESS_CONTROL_HEADERS]: GIT_CONSTS.CONTENT_TYPE,
      })
    ).toBeTruthy();
  });

  it("isPull", () => {
    expect(
      isGitOperation(`${baseGitURL}/${GIT_CONSTS.GIT_UPLOAD_PACK}`, GIT_HTTP_METHODS.POST, {
        [GIT_CONSTS.CONTENT_TYPE]: GIT_CONSTS.X_GIT_UPLOAD_PACK_REQUEST,
      })
    ).toBeTruthy();
  });

  it("isPreflightPush", () => {
    expect(
      isGitOperation(`${baseGitURL}/${GIT_CONSTS.GIT_RECEIVE_PACK}`, GIT_HTTP_METHODS.OPTIONS, {
        [GIT_CONSTS.ACCESS_CONTROL_HEADERS]: GIT_CONSTS.CONTENT_TYPE,
      })
    ).toBeTruthy();
  });

  it("isPush", () => {
    expect(
      isGitOperation(`${baseGitURL}/${GIT_CONSTS.GIT_RECEIVE_PACK}`, GIT_HTTP_METHODS.POST, {
        [GIT_CONSTS.CONTENT_TYPE]: GIT_CONSTS.X_GIT_RECEIVE_PACK_REQUEST,
      })
    ).toBeTruthy();
  });
});
