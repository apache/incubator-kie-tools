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

export class FsUsageCounter {
  private readonly counter = new Map<string, number>();

  public isInUse(fsMountPoint: string) {
    return this.counter.has(fsMountPoint);
  }

  public addUsage(fsMountPoint: string) {
    console.log(`Adding to usage counter ${fsMountPoint}`);
    this.counter.set(fsMountPoint, (this.counter.get(fsMountPoint) ?? 0) + 1);
  }

  public releaseUsage(fsMountPoint: string) {
    const currentCount = this.counter.get(fsMountPoint);
    if (!currentCount) {
      throw new Error(`Catastrophic error releasing usage of ${fsMountPoint}. No ack counterpart.`);
    }

    console.log(`Subtracting from usage counter ${fsMountPoint}`);

    const nextCount = currentCount - 1;
    if (nextCount < 0) {
      throw new Error(`Catastrophic error releasing usage of ${fsMountPoint}. Negative usage count.`);
    } else if (nextCount === 0) {
      this.counter.delete(fsMountPoint);
    } else {
      this.counter.set(fsMountPoint, nextCount);
    }

    return { usagesLeft: nextCount };
  }
}
