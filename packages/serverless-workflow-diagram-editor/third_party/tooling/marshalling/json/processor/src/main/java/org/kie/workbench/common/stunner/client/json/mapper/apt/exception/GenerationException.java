/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kie.workbench.common.stunner.client.json.mapper.apt.exception;

public class GenerationException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private String additionalFailureInfo = "";

  public GenerationException() {}

  public GenerationException(String msg) {
    super(msg);
  }

  public GenerationException(Throwable t) {
    super(t);
  }

  public GenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public void appendFailureInfo(String info) {
    this.additionalFailureInfo = this.additionalFailureInfo + "\n" + info;
  }

  @Override
  public String getMessage() {
    return super.getMessage() + this.additionalFailureInfo;
  }
}
