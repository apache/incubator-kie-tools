/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { CancellationToken, Uri } from "vscode";

export enum JobType {
  SAVE,
  BACKUP
}

export interface FileJob {
  type: JobType;
  target: Uri;
  consumer: () => void;
  cancellation: CancellationToken;
}

export class KogitoEditorJobRegistry {
  private readonly registry = new Map<string, FileJob>();

  public register(jobId: string, job: FileJob) {
    this.registry.set(jobId, job);
  }

  public unregister(jobId: string) {
    this.registry.delete(jobId);
  }

  public resolve(jobId: string): FileJob | undefined {
    return this.registry.get(jobId);
  }

  public execute(jobId: string) {
    const fileJob = this.resolve(jobId);

    if (fileJob) {
      const consumer = fileJob.consumer;
      this.unregister(jobId);
      consumer();
    }
  }
}
