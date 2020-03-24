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

import { UserData } from "../storage/core/UserData";
import { Files } from "../storage/core/Files";
import { FS } from "../storage/core/FS";
import { PromisedRecentOpenedFile, RecentOpenedFile } from "../common/RecentOpenedFile";
import * as path from "path";
import { createHash } from "crypto";

const NUMBER_OF_FILES_TO_KEEP = 50;

export class DesktopUserData {
  private userData: UserData;

  constructor() {
    this.userData = new UserData({
      configName: "config",
      resourceTypes: ["thumbnails"],
      defaults: {
        lastOpenedFiles: []
      }
    });
  }

  public registerFile(fullPath: string) {
    const lastOpenedFiles = this.userData.get("lastOpenedFiles");
    lastOpenedFiles.unshift(fullPath);
    this.userData.set(
      "lastOpenedFiles",
      lastOpenedFiles
        .filter((item: string, i: number, ar: string[]) => ar.indexOf(item) === i)
        .slice(0, NUMBER_OF_FILES_TO_KEEP)
    );
  }

  public saveFileThumbnail(filePath: string, fileType: string, fileContent: string) {
    const thumbnailFileName =
      createHash("md5")
        .update(filePath)
        .digest("hex") +
      "." +
      fileType;
    this.userData.saveResource("thumbnails", thumbnailFileName, fileContent);
  }

  public getLastOpenedFiles(): Promise<RecentOpenedFile[]> {
    const updatedData: string[] = this.userData.get("lastOpenedFiles").filter(file => Files.exists(FS.newFile(file)));
    this.userData.set("lastOpenedFiles", updatedData);

    const validThumbnails = updatedData.map(file =>
      path.join(
        this.userData.getBasePath(),
        "thumbnails",
        createHash("md5")
          .update(file)
          .digest("hex") + ".svg"
      )
    );
    const thumbnailsToRemove: string[] = this.userData
      .listResources("thumbnails")
      .filter(thumbnailPath => !validThumbnails.includes(thumbnailPath));
    this.userData.deleteResources(thumbnailsToRemove);

    return this.getPromisedRecentOpenedFiles(
      updatedData.map(filePath => {
        return {
          filePath: filePath,
          previewPromise: this.userData
            .readResource(
              "thumbnails",
              createHash("md5")
                .update(filePath)
                .digest("hex") + ".svg"
            )
            .catch(() => Promise.resolve(""))
        };
      })
    );
  }

  private getPromisedRecentOpenedFiles(promisedFiles: PromisedRecentOpenedFile[]): Promise<RecentOpenedFile[]> {
    const filesPath = promisedFiles.map(file => file.filePath);
    const previewPromises = promisedFiles.map(file => file.previewPromise);

    return Promise.all(previewPromises)
      .then(resolvedPreviews => {
        const recentOpenedFiles: RecentOpenedFile[] = [];
        for (let i = 0; i < resolvedPreviews.length; i++) {
          if (resolvedPreviews[i] !== "") {
            recentOpenedFiles.push({ filePath: filesPath[i], preview: resolvedPreviews[i] });
          }
        }
        return recentOpenedFiles;
      })
      .catch(e => {
        console.info("Error while listing last opened files: " + e);
        this.clear();
        return Promise.resolve([]);
      });
  }

  public clear() {
    this.userData.clearData();
    this.userData.clearResources("thumbnails");
  }
}
