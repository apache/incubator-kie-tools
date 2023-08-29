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
 * ErrorProcessor runs selenium code and creates detailed stack trace which is swallowed otherwise.
 */
export default class ErrorProcessor {
  /**
   * Runs specified asynchonous selenium function and produces error with detailed stack trace.
   * @param fn Asychonous function to be run.
   * @param errorMessage Error message to be displayed together with detailed stack trace if function fails.
   */
  public static async run<T>(fn: () => Promise<T>, errorMessage: string): Promise<T> {
    // error must be created before selenium fails, otherwise the stack trace is swallowed
    const customError: Error = new Error(errorMessage);

    try {
      // call the function
      return await fn();
    } catch (err) {
      // print detailed stack trace
      console.error(customError.stack);
      throw err;
    }
  }
}
