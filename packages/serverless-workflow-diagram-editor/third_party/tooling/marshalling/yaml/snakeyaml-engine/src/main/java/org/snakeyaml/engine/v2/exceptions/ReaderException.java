/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.exceptions;

/** Indicate invalid input stream */
public class ReaderException extends YamlEngineException {

  private final String name;
  private final int codePoint;
  private final int position;

  /**
   * Create
   *
   * @param name - the name of the reader
   * @param position - the position from the beginning of the stream
   * @param codePoint - the invalid character
   * @param message - the problem
   */
  public ReaderException(String name, int position, int codePoint, String message) {
    super(message);
    this.name = name;
    this.codePoint = codePoint;
    this.position = position;
  }

  /**
   * getter
   *
   * @return the name of the reader
   */
  public String getName() {
    return name;
  }

  /**
   * getter
   *
   * @return the invalid char
   */
  public int getCodePoint() {
    return codePoint;
  }

  /**
   * getter
   *
   * @return position of the error
   */
  public int getPosition() {
    return position;
  }

  @Override
  public String toString() {
    final String s = new String(Character.toChars(codePoint));
    return "unacceptable code point '"
        + s
        + "' (0x"
        + Integer.toHexString(codePoint).toUpperCase()
        + ") "
        + getMessage()
        + "\nin \""
        + name
        + "\", position "
        + position;
  }
}
