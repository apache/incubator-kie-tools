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

export class I18nService {
  constructor(private readonly onLocaleChangeSubscriptions: Array<(locale: string) => void> = []) {}

  public executeOnLocaleChangeSubscriptions(locale: string) {
    this.onLocaleChangeSubscriptions.forEach((onLocaleChange) => {
      onLocaleChange?.(locale);
    });
  }

  public subscribeToLocaleChange(onLocaleChange: (locale: string) => void) {
    this.onLocaleChangeSubscriptions.push(onLocaleChange);
    return onLocaleChange;
  }

  public unsubscribeToLocaleChange(onLocaleChange: (locale: string) => void) {
    const index = this.onLocaleChangeSubscriptions.indexOf(onLocaleChange);
    if (index > -1) {
      this.onLocaleChangeSubscriptions.splice(index, 1);
    }
  }
}
