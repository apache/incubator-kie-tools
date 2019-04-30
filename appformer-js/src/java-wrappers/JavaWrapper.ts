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

import { Portable } from "../marshalling/Portable";

export abstract class JavaWrapper<T> implements Portable<JavaWrapper<T>> {
  private static readonly javaWrapperInstanceIdentifier = "fbeef485-6129-4c23-a047-166c6d2fb7a9";

  public abstract get(): T;

  public abstract set(val: T | ((current: T) => T)): void;

  private instanceIdentifier(): string {
    return JavaWrapper.javaWrapperInstanceIdentifier;
  }

  public static extendsJavaWrapper<T>(obj: any): obj is JavaWrapper<T> {
    if (!obj.instanceIdentifier) {
      return false;
    }

    // this is just a trick to allow the application to identify in runtime if an object extends JavaWrapper.
    return obj.instanceIdentifier() === JavaWrapper.javaWrapperInstanceIdentifier;
  }
}
