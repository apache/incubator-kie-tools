/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { FsCache } from "./FsCache";

enum FlushStatus {
  FLUSH_IN_PROGRESS,
  FLUSH_AND_DEINIT_IN_PROGRESS,
  FLUSH_SCHEDULED,
  FLUSH_AND_DEINIT_SCHEDULED,
}

type FlushControl =
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushStatus.FLUSH_SCHEDULED }
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushStatus.FLUSH_AND_DEINIT_SCHEDULED }
  | { operationPromise: Promise<void>; status: FlushStatus.FLUSH_IN_PROGRESS }
  | { operationPromise: Promise<void>; status: FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS };

export class FsFlushManager {
  private readonly flushControlDebounceTimeoutInMs = 2000;

  public readonly flushControl = new Map<string, FlushControl>();

  private async executeFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    await fsCache.flushFs(fsMountPoint);
    if (deinitArgs.deinit) {
      await fsCache.deinitFs(fsMountPoint);
    }
  }

  private scheduleFsFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    this.flushControl.set(fsMountPoint, {
      status: FlushStatus.FLUSH_SCHEDULED,
      scheduledTask: setTimeout(() => {
        this.flushControl.set(fsMountPoint, {
          status: deinitArgs.deinit ? FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS : FlushStatus.FLUSH_IN_PROGRESS,
          operationPromise: this.executeFlush(fsCache, fsMountPoint, deinitArgs).then(() => {
            console.debug(`Flush complete for ${fsMountPoint}`);
            this.flushControl.delete(fsMountPoint);
          }),
        });
      }, this.flushControlDebounceTimeoutInMs),
    });
  }

  public requestFsFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    const flushControl = this.flushControl.get(fsMountPoint);

    if (!flushControl) {
      console.debug(`Scheduling flush for ${fsMountPoint}`);
      this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs);
    } else if (flushControl.status === FlushStatus.FLUSH_SCHEDULED) {
      // If flush is scheduled, we can always cancel it and put a flush and deinit in its place.
      console.debug(`Debouncing flush request for ${fsMountPoint}`);
      clearTimeout(flushControl.scheduledTask);
      this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs);
    } else if (flushControl.status === FlushStatus.FLUSH_AND_DEINIT_SCHEDULED) {
      if (deinitArgs.deinit) {
        console.debug(`Debouncing flush and deinit request for ${fsMountPoint}`);
        clearTimeout(flushControl.scheduledTask);
        this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs);
      } else {
        console.error(`Flush requested while flush and deinit is in scheduled!!!! ${fsMountPoint}`);
      }
    } else if (flushControl.status === FlushStatus.FLUSH_IN_PROGRESS) {
      if (deinitArgs.deinit) {
        console.error(`Flush and deinit requested while flush is in progress!!!! ${fsMountPoint}`);
      } else {
        console.error(`Flush requested while flush is in progress!!!! ${fsMountPoint}`);
      }
    } else if (flushControl.status === FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS) {
      if (deinitArgs.deinit) {
        console.error(`Flush and deinit requested while flush and deinit is in progress!!!! ${fsMountPoint}`);
      } else {
        console.error(`Flush requested while flush and deinit is in progress!!!! ${fsMountPoint}`);
      }
    } else {
      throw new Error(`Oops! Impossible scenario for flushing '${fsMountPoint}'`);
    }
  }
}
