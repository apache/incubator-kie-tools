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

import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { EMPTY_CONFIG } from "../../../settings/OpenShiftSettingsConfig";
import { OpenShiftInstanceStatus } from "../../../editor/DmnDevSandbox/OpenShiftInstanceStatus";
import { DmnDevSandboxModalConfig } from "../../../editor/DmnDevSandbox/DmnDevSandboxModalConfig";
import {
  usingTestingDmnDevSandboxContext,
  usingTestingGlobalContext,
  usingTestingKieToolingExtendedServicesContext,
  usingTestingOnlineI18nContext,
} from "../../testing_utils";

describe("DmnDevSandboxModalConfig", () => {
  it("should show the validation error alert when saving an invalid config", async () => {
    const { getByTestId } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingDmnDevSandboxContext(<DmnDevSandboxModalConfig />, {
              isConfigModalOpen: true,
              currentConfig: EMPTY_CONFIG,
            }).wrapper
          ).wrapper
        ).wrapper
      ).wrapper
    );
    fireEvent.click(getByTestId("save-config-button"));
    expect(getByTestId("alert-validation-error")).toBeVisible();
  });

  it("should show the config expired warning when the instance status is EXPIRED", async () => {
    const { getByTestId } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingDmnDevSandboxContext(<DmnDevSandboxModalConfig />, {
              isConfigModalOpen: true,
              instanceStatus: OpenShiftInstanceStatus.EXPIRED,
            }).wrapper
          ).wrapper
        ).wrapper
      ).wrapper
    );
    expect(getByTestId("alert-config-expired-warning")).toBeVisible();
  });

  it("should reset the config when clicking on the reset button", async () => {
    const { getByTestId } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingDmnDevSandboxContext(<DmnDevSandboxModalConfig />, {
              isConfigModalOpen: true,
              currentConfig: { namespace: "namespace", host: "host", token: "token" },
            }).wrapper
          ).wrapper
        ).wrapper
      ).wrapper
    );
    fireEvent.click(getByTestId("reset-config-button"));
    expect(getByTestId("namespace-text-field")).toHaveTextContent("");
    expect(getByTestId("host-text-field")).toHaveTextContent("");
    expect(getByTestId("token-text-field")).toHaveTextContent("");
  });
});
