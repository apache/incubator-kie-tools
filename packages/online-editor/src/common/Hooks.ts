/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { EffectCallback, useCallback, useEffect, useRef, useState } from "react";

export function usePrevious(value: any) {
  const ref = useRef();

  useEffect(() => {
    if (ref.current !== value) {
      ref.current = value;
    }
  }, [value]);

  return ref.current;
}

export function useController<T>(): [T | undefined, (controller: T) => void] {
  const [controller, setController] = useState<T | undefined>(undefined);

  const ref = useCallback((controller: T) => {
    setController(controller);
  }, []);

  return [controller, ref];
}

export type ArrowFunction<A, B> = (a: A) => B;

export class Holder<T> {
  constructor(private value: T) {}
  public readonly get = () => this.value;
  public readonly set = (newValue: T) => (this.value = newValue);
}

export class IfNotCanceledHolder extends Holder<IfNotCanceledHolderType> {
  public run = <T, R, O>(then: ArrowFunction<T, R>, or?: ArrowFunction<T, O>) => this.get()(then, or);
}

export type IfNotCanceledHolderType = <T, R, O = undefined>(
  then: ArrowFunction<T, R>,
  or?: ArrowFunction<T, O>
) => ArrowFunction<T, R | O>;

export type SafeEffectParams = {
  canceled: Holder<boolean>;
  ifNotCanceled: IfNotCanceledHolder;
};

export function useCancelableEffect(effect: (args: SafeEffectParams) => ReturnType<EffectCallback>) {
  useEffect(() => {
    const canceled = new Holder(false);

    const safely = new IfNotCanceledHolder((then, or) => (arg) => {
      if (!canceled.get()) {
        return then(arg);
      } else {
        return or?.(arg) ?? (undefined as any);
      }
    });

    const effectCleanup = effect({ canceled, ifNotCanceled: safely });

    return () => {
      canceled.set(true);
      if (effectCleanup) {
        effectCleanup();
      }
    };
  }, [effect]);
}
