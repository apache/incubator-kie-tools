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

import { CommitMessageValidationResource, CommitMessageValidationFetch } from "./CommitMessageValidation";

export class CommitMessageValidationService {
  constructor(private readonly args: { commitMessageValidationServiceUrl: string }) {}

  public async validateCommitMessage(commitMessage: string): Promise<CommitMessageValidationResource> {
    const commitMessageValidationResource = new CommitMessageValidationFetch({
      commitMessageValidationServiceUrl: this.args.commitMessageValidationServiceUrl,
      commitMessage,
    });
    try {
      const response = await fetch(commitMessageValidationResource.endpoint(), {
        method: commitMessageValidationResource.method(),
        body: commitMessageValidationResource.body(),
        headers: commitMessageValidationResource.headers(),
      });

      if (response.ok) {
        return (await response.json()) as CommitMessageValidationResource;
      } else {
        return {
          result: false,
          reason: `Commit message validator ${this.args.commitMessageValidationServiceUrl} is not accessible. ([HTTP ${
            response.status
          }]: ${await response.text()})`,
        };
      }
    } catch (e) {
      return {
        result: false,
        reason: `Commit message validator ${this.args.commitMessageValidationServiceUrl} is not accessible.`,
      };
    }
  }
}
