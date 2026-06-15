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

/**
 * Remove the trailing slash from an URL if it exists.
 *
 * @param url -
 * @returns
 */
export function removeTrailingSlashFromUrl(url: string): string {
  return url.replace(/\/$/, "");
}

export function isDataIndexUrlValid(url: string): boolean {
  try {
    const parsedUrl = new URL(url);
    return parsedUrl.protocol === "http:" || parsedUrl.protocol === "https:";
  } catch (_) {
    return false;
  }
}

/**
 * Changes the origin (protocol, hostname, and port) of a URL while preserving the path.
 *
 * @param url - The URL with the origin to be changed
 * @param newOrigin - The origin to be applied to the URL. If a full URL is passed, only the origin will be used.
 * @returns The URL with the changed origin (e.g., "https://my-app.openshift.com/hello_world")
 */
export function changeUrlOrigin(url: string, newOrigin: string): string {
  const newOriginUrl = new URL(newOrigin);
  const urlObj = new URL(url);

  urlObj.protocol = newOriginUrl.protocol;
  urlObj.hostname = newOriginUrl.hostname;
  urlObj.port = newOriginUrl.port;

  return urlObj.toString();
}
