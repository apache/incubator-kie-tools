/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useState, useCallback, useImperativeHandle, Ref } from "react";
import { Modal, ModalProps } from "@patternfly/react-core/dist/js/components/Modal";

export type PromiseModalChildren<T> = ({
  onReturn,
  onClose,
}: {
  onReturn: (value: T) => void;
  onClose: () => void;
}) => JSX.Element;

export type PromiseModalProps<T> = Omit<ModalProps, "isOpen" | "ref" | "children"> & {
  children: PromiseModalChildren<T>;
  forwardRef: Ref<PromiseModalRef<T>>;
};

export interface PromiseModalRef<T> {
  open: () => Promise<T>;
  close: () => void;
}

export function PromiseModal<T>(props: PromiseModalProps<T>) {
  const [isOpen, setIsOpen] = useState(false);
  const [promiseCallbacks, setPromiseCallbacks] =
    useState<{ resolve: (value: T) => void; reject: (reason?: any) => void }>();

  const open = useCallback(async () => {
    return new Promise<T>((resolve, reject) => {
      setPromiseCallbacks({
        resolve,
        reject,
      });

      setIsOpen(true);
    });
  }, []);

  const close = useCallback(() => {
    setIsOpen(false);
  }, []);

  const onReturn = useCallback(
    (value: T) => {
      promiseCallbacks?.resolve(value);
      close();
    },
    [close, promiseCallbacks]
  );

  const onClose = useCallback(() => {
    promiseCallbacks?.reject();
    close();
  }, [close, promiseCallbacks]);

  const { children, forwardRef, ...modalProps } = props;

  useImperativeHandle(
    forwardRef,
    () => ({
      open,
      close,
    }),
    [open, close]
  );

  return (
    <Modal {...modalProps} isOpen={isOpen} onClose={onClose}>
      {children({ onReturn, onClose })}
    </Modal>
  );
}
