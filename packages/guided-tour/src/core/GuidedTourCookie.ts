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

export class GuidedTourCookie {
  private GUIDED_TOUR_ENABLED_COOKIE = "is-guided-tour-enabled";

  public isDisabled() {
    return this.getCookie(this.GUIDED_TOUR_ENABLED_COOKIE) === "NO";
  }

  public markAsDisabled() {
    return this.setCookie(this.GUIDED_TOUR_ENABLED_COOKIE, "NO");
  }

  private getCookie(name: string) {
    const value = "; " + document.cookie;
    const parts = value.split("; " + name + "=");

    if (parts.length === 2) {
      return parts
        .pop()!
        .split(";")
        .shift();
    }
  }

  private setCookie(name: string, value: string) {
    const date = new Date();
    date.setTime(date.getTime() + 365 * 24 * 60 * 60); // expires in 1 year
    document.cookie = name + "=" + value + "; expires=" + date.toUTCString() + "; path=/";
  }
}
