/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

export interface Route<T> {
  url(args: T): string;

  args(url: string): T;
}

export class DownloadHubRoute implements Route<{}> {
  public url(args: {}) {
    return "/download";
  }

  public args(url: string) {
    return {};
  }
}

export class HomeRoute implements Route<{}> {
  public url(args: {}) {
    return "/";
  }

  public args(url: string) {
    return {};
  }
}

export interface EditorRouteArgs {
  type: string;
}

export class EditorRoute implements Route<EditorRouteArgs> {
  public url(args: EditorRouteArgs) {
    return `/editor/${args.type}`;
  }

  public args(url: string) {
    return { type: this.getFileExtension(url)! };
  }

  private getFileExtension(url: string) {
    return url.split("/").pop()?.match(/[\w\d]+/)?.pop();
  }
}

export class Routes {
  public readonly home = new HomeRoute();
  public readonly editor = new EditorRoute();
  public readonly downloadHub = new DownloadHubRoute();
}
