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

export enum InnerEnvelopeBusMessageType {
  REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
  REQUEST_INIT,
  REQUEST_PREVIEW,
  REQUEST_CONTENT,

  NOTIFY_CONTENT_CHANGED,
  NOTIFY_EDITOR_REDO,
  NOTIFY_EDITOR_UNDO
}
