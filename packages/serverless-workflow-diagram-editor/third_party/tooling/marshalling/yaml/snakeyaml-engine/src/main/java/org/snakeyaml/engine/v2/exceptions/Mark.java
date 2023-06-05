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

import java.io.Serializable;

import org.snakeyaml.engine.v2.common.CharConstants;

/**
 * Location of a problem in the YAML document. Its only use is producing nice error messages. Parser
 * does not use it for any other purposes.
 */
public final class Mark implements Serializable {

  private final String name;
  private final int index;
  private final int line;
  private final int column;
  private final int[] buffer;
  private final int pointer;

  /**
   * Creates {@link Mark}
   *
   * @param name - the name to be used as identifier
   * @param index - the index from the beginning of the stream
   * @param line - line of the mark from beginning of the stream
   * @param column - column of the mark from beginning of the line
   * @param buffer - the data
   * @param pointer - the position of the mark from the beginning of the data
   */
  public Mark(String name, int index, int line, int column, int[] buffer, int pointer) {
    super();
    this.name = name;
    this.index = index;
    this.line = line;
    this.column = column;
    this.buffer = buffer;
    this.pointer = pointer;
  }

  /**
   * This constructor is only for test
   *
   * @param name - the name to be used as identifier
   * @param index - the index from the beginning of the stream
   * @param line - line of the mark from beginning of the stream
   * @param column - column of the mark from beginning of the line
   * @param str - the data
   * @param pointer - the position of the mark from the beginning of the data
   */
  public Mark(String name, int index, int line, int column, char[] str, int pointer) {
    this(name, index, line, column, toCodePoints(str), pointer);
  }

  private static int[] toCodePoints(char[] str) {
    int[] codePoints = new int[Character.codePointCount(str, 0, str.length)];
    int i = 0;
    int c = 0;
    while (i < str.length) {
      int cp = Character.codePointAt(str, i);
      codePoints[c] = cp;
      i += Character.charCount(cp);
      c++;
    }
    return codePoints;
  }

  private boolean isLineBreak(int c) {
    return CharConstants.NULL_OR_LINEBR.has(c);
  }

  /**
   * Create readable snippet
   *
   * @param indent - the indent
   * @param maxLength - cut data after this length
   * @return readable piece of YAML where a problem detected
   */
  public String createSnippet(int indent, int maxLength) {
    float half = maxLength / 2f - 1f;
    int start = pointer;
    String head = "";
    while ((start > 0) && !isLineBreak(buffer[start - 1])) {
      start -= 1;
      if (pointer - start > half) {
        head = " ... ";
        start += 5;
        break;
      }
    }
    String tail = "";
    int end = pointer;
    while ((end < buffer.length) && !isLineBreak(buffer[end])) {
      end += 1;
      if (end - pointer > half) {
        tail = " ... ";
        end -= 5;
        break;
      }
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      result.append(" ");
    }
    result.append(head);
    for (int i = start; i < end; i++) {
      result.appendCodePoint(buffer[i]);
    }
    result.append(tail);
    result.append("\n");
    for (int i = 0; i < indent + pointer - start + head.length(); i++) {
      result.append(" ");
    }
    result.append("^");
    return result.toString();
  }

  /**
   * Create readable YAML with indent 4 and n ot longer that 75 characters long
   *
   * @return readable piece of YAML where a problem detected
   */
  public String createSnippet() {
    return createSnippet(4, 75);
  }

  @Override
  public String toString() {
    String snippet = createSnippet();
    return " in " + name + ", line " + (line + 1) + ", column " + (column + 1) + ":\n" + snippet;
  }

  /**
   * getter
   *
   * @return name of the data stream
   */
  public String getName() {
    return name;
  }

  /**
   * starts with 0
   *
   * @return line number
   */
  public int getLine() {
    return line;
  }

  /**
   * starts with 0
   *
   * @return column number
   */
  public int getColumn() {
    return column;
  }

  /**
   * starts with 0
   *
   * @return character number
   */
  public int getIndex() {
    return index;
  }

  /**
   * getter
   *
   * @return data with the error
   */
  public int[] getBuffer() {
    return buffer;
  }

  /**
   * getter
   *
   * @return pointer in the data to the error
   */
  public int getPointer() {
    return pointer;
  }
}
