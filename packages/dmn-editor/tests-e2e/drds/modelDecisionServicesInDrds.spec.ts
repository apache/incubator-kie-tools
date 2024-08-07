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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test } from "../__fixtures__/base";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model Decision Services - DRD", () => {
  //TODO Extract scenarios from https://github.com/apache/incubator-kie-issues/issues/870

  test("888 add to DRD, all DRDs depiction same", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // create non empty DS
    // add the ^ DS into non default DRD
    // all the DS content needs to be added into DRD also
  });

  test("888 add to DRD, automatically layout", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // add decision service, non empty, that is currently in no DRD
    // its content should be automatically layout-ed
  });

  test("888 add to DRD, mix of collapsed and expanded", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // However, if one of its contained Decisions it also contained by other Decision Services
    // present on that DRD, and one of them is in expanded form,
    // the Decision Service is added as collapsed, and no Decisions are added or moved.
  });

  test("888 add to DRD, collapsed", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // prepare on empty DS
    // add some of its content to different DRD
    // collapse the DS
    // add the DS into DRD where already some its content is
    // the DS should be added as collapsed and its content removed from the DRD
  });

  test("894 do not allow add node that is already in DS and in DRD expanded", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/894");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/894",
    });
    // On the DMN Editor, do not allow adding a Decision to a DRD
    // if this Decision is contained by one or more Decision Services already present
    // on that DRD, even if in collapsed state.
  });

  test("894 do not allow add node that is already in DS and in DRD collapsed", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/894");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/894",
    });
    // On the DMN Editor, do not allow adding a Decision to a DRD
    // if this Decision is contained by one or more Decision Services already present
    // on that DRD, even if in collapsed state.
  });

  test("972 autolayout", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/972");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/972",
    });
    // TODO, question, not understand
  });

  test("892 collapse - remove its content", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("892 collapse - allow only if no other DS is expanded in the DRD", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("892 expand - same depiction as other DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("892 expand - allow only if no other DS is collpased in the DRD", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("889 move DS content and replicate in DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/889");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/889",
    });
    // TODO
  });

  test("889 resize DS content and replicate in DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/889");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/889",
    });
    // TODO
  });

  test("898 move DS divider and replicate in DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/898");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/898",
    });
    // TODO
  });

  test("890 add node to DS, check its added to all DRDs where DS in expanded form", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/890");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/890",
    });
    // TODO
  });

  test("890 add node to DS, check its removed from DRDs where DS in collapsed form", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/890");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/890",
    });
    // TODO
  });

  test("891 remove node from DS, check its removed form DS in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/891");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/891",
    });
    // TODO, question, isn't this the same as 889 - syncing depiction of DS in all DRDs?
  });

  test("893 collapsed external DS, synced in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("893 expanded external DS, synced in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("893 remove node from DS, check its removed form DS in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("895 ", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/895");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/895",
    });
    // TODO , Question, isn't that in conflict with other DRD tickets?
    // is that even possible to be in state DS_One is collapsed
    // DS_Two is expanded and both of them contain the same decision?
  });

  test("1298 ", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1298");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1298",
    });
    // TODO
  });
});
