/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { makeCookieName, getCookie, setCookie } from "../../cookies";

export const KAFKA_BOOTSTRAP_SERVER_COOKIE_NAME = makeCookieName("kafka", "bootstrap-server");
export const KAFKA_TOPIC_COOKIE_NAME = makeCookieName("kafka", "topic");

export interface KafkaSettingsConfig {
  bootstrapServer: string;
  topic: string;
}

export const EMPTY_CONFIG: KafkaSettingsConfig = {
  bootstrapServer: "",
  topic: "",
};

export function isKafkaConfigValid(config: KafkaSettingsConfig): boolean {
  return isBootstrapServerValid(config.bootstrapServer) && isTopicValid(config.topic);
}

export function isBootstrapServerValid(bootstrapServer: string): boolean {
  return bootstrapServer !== undefined && bootstrapServer.trim().length > 0;
}

export function isOAuthEndpointUriValid(oauthEndpointUri: string): boolean {
  return oauthEndpointUri !== undefined && oauthEndpointUri.trim().length > 0;
}

export function isTopicValid(topic: string): boolean {
  return topic !== undefined && topic.trim().length > 0;
}

export function readKafkaConfigCookie(): KafkaSettingsConfig {
  return {
    bootstrapServer: getCookie(KAFKA_BOOTSTRAP_SERVER_COOKIE_NAME) ?? "",
    topic: getCookie(KAFKA_TOPIC_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveBootstrapServerCookie(bootstrapServer: string): void {
  setCookie(KAFKA_BOOTSTRAP_SERVER_COOKIE_NAME, bootstrapServer);
}

export function saveTopicCookie(topic: string): void {
  setCookie(KAFKA_TOPIC_COOKIE_NAME, topic);
}

export function saveConfigCookie(config: KafkaSettingsConfig): void {
  saveBootstrapServerCookie(config.bootstrapServer);
  saveTopicCookie(config.topic);
}
