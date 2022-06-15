/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

function main() {
  if ("BroadcastChannel" in window) {
    return;
  }

  const subscriptions = new Map<string, Set<any>>();

  (window as any).BroadcastChannel = class BroadcastChannel {
    public onmessage: ((this: BroadcastChannel, ev: MessageEvent) => any) | null;
    public onmessageerror: ((this: BroadcastChannel, ev: MessageEvent) => any) | null;

    constructor(private readonly name: string) {
      const subscription = subscriptions.get(name);
      if (!subscription) {
        subscriptions.set(name, new Set());
      }

      subscriptions.get(name)!.add(this);
    }

    public postMessage(message: any): void {
      //FIXME: This could be one message per `setTimeout`, but it's fine for now.
      setTimeout(() => {
        subscriptions.get(this.name)!.forEach((bc) => {
          if (bc === this) {
            return;
          }

          bc.onmessage?.({ data: message });
        });
      }, 0);
    }

    public close(): void {
      subscriptions.get(this.name)!.delete(this);
    }
  };
}

main();
