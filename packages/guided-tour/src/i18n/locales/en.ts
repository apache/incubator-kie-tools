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

import { GuidedTourI18n } from "..";
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: GuidedTourI18n = {
  ...en_common,
  great: "Great",
  stop: "Do you want to stop the tour?",
  notFollowing: "Seems like you didn't follow the suggested action. Do you want to stop the tour?",
  options: `Click on ${"Skip tour".bold()} to stop it or ${"Continue".bold()} to resume your tour`,
  oops: "Oops!",
  somethingWrong: "Something went wrong and the content could not be loaded.",
  skipTour: "Skip tour",
};
