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

/**
 * Class to represent top-level correlations of a process.
 */
export default class Correlation {
  constructor(
    private readonly id: string,
    private readonly name: string,
    private readonly propertyId: string,
    private readonly propertyName: string,
    private readonly propertyType: string
  ) {}

  public getId(): string {
    return this.id;
  }

  public getName(): string {
    return this.name;
  }

  public getPropertyId(): string {
    return this.propertyId;
  }

  public getPropertyName(): string {
    return this.propertyName;
  }

  public getPropertyType(): string {
    return this.propertyType;
  }
}
