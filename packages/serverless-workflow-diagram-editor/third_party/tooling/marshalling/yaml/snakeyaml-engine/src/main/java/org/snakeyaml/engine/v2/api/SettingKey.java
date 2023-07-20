/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.api;

import java.io.Serializable;

/**
 * Type for the custom keys in settings. The keys (used in custom settings) must implement this
 * interface. It makes possible to be 'compiler safe' and define Enum for keys. This way is easier
 * to follow the changes in the key names and avoid a typo. The implementation of this interface
 * must properly implement equals() and hashCode() to be used as keys in a Map
 */
public interface SettingKey extends Serializable {}
