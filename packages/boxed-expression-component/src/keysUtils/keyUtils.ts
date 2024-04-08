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
 * Class to check the key pressed during keyboard events.
 * All methods are case insensitive.
 */
export class NavigationKeysUtils {
  static isBackspace(key: string) {
    return /^backspace$/i.test(key);
  }

  static isDelete(key: string) {
    return /^delete$/i.test(key);
  }

  static isEsc(key: string) {
    return /^escape$/i.test(key);
  }

  static isAltGraph(key: string) {
    return /^altgraph$/i.test(key);
  }

  static isArrowDown(key: string) {
    return /^arrowdown$/i.test(key);
  }

  static isArrowLeft(key: string) {
    return /^arrowleft$/i.test(key);
  }

  static isArrowRight(key: string) {
    return /^arrowright$/i.test(key);
  }

  static isArrowUp(key: string) {
    return /^arrowup$/i.test(key);
  }

  static isAnyArrow(key: string) {
    return /^arrow(up|right|down|left)$/i.test(key);
  }

  static isEnter(key: string) {
    return /^enter$/i.test(key);
  }

  static isTab(key: string) {
    return /^tab$/i.test(key);
  }

  static isFunctionKey(key: string) {
    return /^F\d{1,2}$/i.test(key);
  }
}
