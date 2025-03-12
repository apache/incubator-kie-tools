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

import React, { useState, useCallback, useImperativeHandle, Ref } from "react";
import { Modal, ModalProps } from "@patternfly/react-core/dist/js/components/Modal";

export type PromiseModalChildren<ExpectedReturnType, ExtraArgs> = ({
  onReturn,
  onClose,
  args,
}: {
  onReturn: (value: ExpectedReturnType) => void;
  onClose: () => void;
  args?: ExtraArgs;
}) => JSX.Element;

export type PromiseModalProps<ExpectedReturnType, ExtraArgs> = Omit<ModalProps, "isOpen" | "ref" | "children"> & {
  children: PromiseModalChildren<ExpectedReturnType, ExtraArgs>;
  forwardRef: Ref<PromiseModalRef<ExpectedReturnType, ExtraArgs>>;
};

export interface PromiseModalRef<ExpectedReturnType, ExtraArgs> {
  open: (args?: ExtraArgs) => Promise<ExpectedReturnType>;
  close: () => void;
}

export function PromiseModal<ExpectedReturnType, ExtraArgs>(props: PromiseModalProps<ExpectedReturnType, ExtraArgs>) {
  const [isOpen, setIsOpen] = useState(false);
  const [args, setArgs] = useState<ExtraArgs>();
  const [promiseCallbacks, setPromiseCallbacks] = useState<{
    resolve: (value: ExpectedReturnType) => void;
    reject: (reason?: any) => void;
  }>();

  const open = useCallback(async (openArgs?: ExtraArgs) => {
    setArgs(openArgs);
    return new Promise<ExpectedReturnType>((resolve, reject) => {
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
    (value: ExpectedReturnType) => {
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
      {children({ onReturn, onClose, args })}
    </Modal>
  );
}
