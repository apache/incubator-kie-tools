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
 * Change the base URL to using the window.location from a given URL
 *
 * @param {string} url - The URL string to modify
 * @returns {string} The new URL.
 */
export function changeBaseURLToCurrentLocation(url: string): string {
  const { protocol, hostname, port } = window.parent.location ?? window.location;
  const parsedUrl = new URL(url, window.parent.location.href ?? window.location.href);

  return `${protocol}//${hostname}${port ? `:${port}` : ""}${parsedUrl.pathname}`;
}
