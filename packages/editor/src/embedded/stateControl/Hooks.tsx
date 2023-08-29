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

import { useEffect, useState } from "react";
import { EmbeddedEditorRef } from "../embedded";

export function useDirtyState(editor?: EmbeddedEditorRef) {
  const [isDirty, setIsDirty] = useState(false);

  useEffect(() => {
    setIsDirty(editor?.getStateControl().isDirty() ?? false);
    const callback = editor?.getStateControl().subscribe(setIsDirty);
    return () => {
      editor?.getStateControl().unsubscribe(callback!);
    };
  }, [editor]);

  return isDirty;
}

export function useStateControlSubscription(
  editor: EmbeddedEditorRef | undefined,
  callback: (isDirty: boolean) => void | Promise<void>,
  args: { throttle: number } = { throttle: 0 }
) {
  useEffect(() => {
    if (!editor?.isReady) {
      return;
    }

    let timeout: number | undefined;
    const subscription = editor?.getStateControl().subscribe((isDirty) => {
      if (args.throttle <= 0) {
        callback(isDirty);
        return;
      }

      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = window.setTimeout(() => {
        callback(isDirty);
      }, args.throttle);
    });

    return () => {
      if (subscription) {
        return editor?.getStateControl().unsubscribe(subscription);
      }
    };
  }, [editor, callback, args.throttle]);
}
