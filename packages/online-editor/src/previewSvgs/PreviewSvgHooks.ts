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

import { Holder } from "../reactExt/Hooks";
import { useCallback } from "react";
import { decoder } from "../workspace/encoderdecoder/EncoderDecoder";
import { useSyncedCompanionFsFile } from "../companionFs/CompanionFsHooks";
import { usePreviewSvgs } from "./PreviewSvgsContext";

export function usePreviewSvg(workspaceId: string, workspaceFileRelativePath: string) {
  const { previewSvgService } = usePreviewSvgs();

  const { contentPromise } = useSyncedCompanionFsFile(
    previewSvgService.companionFsService,
    workspaceId,
    workspaceFileRelativePath,
    useCallback(
      async (cancellationToken: Holder<boolean>) => {
        const file = await previewSvgService.companionFsService.get({ workspaceId, workspaceFileRelativePath });
        if (cancellationToken.get() || !file) {
          return;
        }

        const svgString = await file.getFileContents();
        if (cancellationToken.get()) {
          return;
        }

        return decoder.decode(svgString);
      },
      [previewSvgService.companionFsService, workspaceId, workspaceFileRelativePath]
    )
  );

  return { previewSvgString: contentPromise };
}
