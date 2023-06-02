/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as Bowser from "bowser";

export type SupportedBrowsers = "chrome" | "edge" | "safari" | "firefox" | "opera";
export type MinVersionForFeature = Record<SupportedBrowsers, number>;
export type BowserSatisfies = Record<SupportedBrowsers, `>=${number}`>;

// https://caniuse.com/sharedworkers
const SharedWebWorkersFeature: MinVersionForFeature = {
  chrome: 4,
  edge: 79,
  safari: 16,
  firefox: 29,
  opera: 11.5,
};

// https://caniuse.com/broadcastchannel
const BroadcastChannelFeature: MinVersionForFeature = {
  chrome: 54,
  edge: 79,
  safari: 15.4,
  firefox: 38,
  opera: 41,
};

export const mapSupportedVersionsToBowser = (...features: MinVersionForFeature[]) => {
  const minSupportedVersions = features.reduceRight((prev, curr) =>
    Object.keys(prev).reduce(
      (obj, browser: keyof MinVersionForFeature) => ({ ...obj, [browser]: Math.max(prev[browser], curr[browser]) }),
      {} as MinVersionForFeature
    )
  );

  return Object.keys(minSupportedVersions).reduce(
    (obj, browser: keyof MinVersionForFeature) => ({ ...obj, [browser]: `>=${minSupportedVersions[browser]}` }),
    {} as BowserSatisfies
  );
};

/**
 * Checks if the browser is Chromium based
 */
export const isBrowserChromiumBased = (): boolean => {
  const agent = window.navigator.userAgent.toLowerCase();
  return agent.indexOf("edg") > -1 || agent.indexOf("chrome") > -1;
};

export const SUPPORTED_BROWSERS = mapSupportedVersionsToBowser(SharedWebWorkersFeature, BroadcastChannelFeature);

const IS_SUPPORTED = Bowser.getParser(window.navigator.userAgent).satisfies(SUPPORTED_BROWSERS);
const HAS_NECESSARY_APIS = window["SharedWorker"] && window["BroadcastChannel"];
export const BROWSER_DETAILS = {
  info: Bowser.parse(window.navigator.userAgent),
  isCompatible: HAS_NECESSARY_APIS && IS_SUPPORTED,
};
