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

import { Mode, DemoMode } from ".";

export class Step {
  constructor(
    public mode: Mode,
    public content?:
      | React.ReactNode
      | ((props: { dismiss?: () => void; nextStep?: () => void; prevStep?: () => void }) => React.ReactNode)
      | string,
    public selector?: string,
    public highlightEnabled?: boolean,
    public navigatorEnabled?: boolean,
    public position?: "right" | "bottom" | "center" | "left",
    public negativeReinforcementMessage?: string
  ) {}
}

export const NONE: Step = {
  mode: new DemoMode()
};
