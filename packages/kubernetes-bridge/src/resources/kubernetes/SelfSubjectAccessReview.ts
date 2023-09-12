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

import { HttpMethod } from "../../fetch/FetchConstants";
import { CreateResourceFetchArgs, ResourceFetch, UniqueResourceFetchArgs } from "../../fetch/ResourceFetch";
import { SelfSubjectAccessReview, ISelfSubjectAccessReview } from "kubernetes-models/authorization.k8s.io/v1";
import { ResourceDataSource } from "../common";

export type CreateSelfSubjectAccessReviewTemplateArgs = {
  resourceDataSource: ResourceDataSource.TEMPLATE;
  resource: string;
};

export type SelfSubjectAccessReviewDescriptor = ISelfSubjectAccessReview;

export type CreateSelfSubjectAccessReviewArgs = CreateResourceFetchArgs &
  (
    | CreateSelfSubjectAccessReviewTemplateArgs
    | { descriptor: SelfSubjectAccessReviewDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const SELF_SUBJECT_ACCESS_REVIEW_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateSelfSubjectAccessReviewTemplateArgs
): SelfSubjectAccessReviewDescriptor => {
  return new SelfSubjectAccessReview({
    spec: {
      resourceAttributes: {
        resource: args.resource,
        verb: "*",
        namespace: args.namespace,
      },
    },
  }).toJSON();
};

export class CreateSelfSubjectAccessReview extends ResourceFetch {
  constructor(protected args: CreateSelfSubjectAccessReviewArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : SELF_SUBJECT_ACCESS_REVIEW_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${SelfSubjectAccessReview.apiVersion}/selfsubjectaccessreviews`;
  }
}
