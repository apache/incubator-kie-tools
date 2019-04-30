/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

export enum JavaType {
  BYTE = "java.lang.Byte",
  DOUBLE = "java.lang.Double",
  FLOAT = "java.lang.Float",
  INTEGER = "java.lang.Integer",
  LONG = "java.lang.Long",
  SHORT = "java.lang.Short",
  BOOLEAN = "java.lang.Boolean",
  STRING = "java.lang.String",
  DATE = "java.util.Date",
  BIG_DECIMAL = "java.math.BigDecimal",
  BIG_INTEGER = "java.math.BigInteger",
  ARRAY_LIST = "java.util.ArrayList",
  UNMODIFIABLE_COLLECTION = "java.util.Collections$UnmodifiableCollection",
  UNMODIFIABLE_SET = "java.util.Collections$UnmodifiableSet",
  UNMODIFIABLE_MAP = "java.util.Collections$UnmodifiableMap",
  HASH_SET = "java.util.HashSet",
  HASH_MAP = "java.util.HashMap",
  OPTIONAL = "java.util.Optional",
  ENUM = "java.lang.Enum"
}
