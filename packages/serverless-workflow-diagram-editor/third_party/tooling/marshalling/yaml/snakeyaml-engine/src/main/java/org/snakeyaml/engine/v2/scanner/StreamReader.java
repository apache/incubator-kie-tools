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
package org.snakeyaml.engine.v2.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;

import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.CharConstants;
import org.snakeyaml.engine.v2.exceptions.Mark;
import org.snakeyaml.engine.v2.exceptions.ReaderException;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Read the provided stream of code points into String and implement look-ahead operations. Checks
 * if code points are in the allowed range.
 */
public final class StreamReader {

  private final String name;
  private final Reader stream;
  private final int bufferSize;
  // temp buffer for one read operation (to avoid creating the array in stack)
  private final char[] buffer;
  private final boolean useMarks;
  /** Read data (as a moving window for input stream) */
  private int[] codePointsWindow;
  /** Real length of the data in dataWindow */
  private int dataLength;
  /** The variable points to the current position in the data array */
  private int pointer = 0;

  private boolean eof;
  /**
   * index is only required to implement 1024 key length restriction and the total length
   * restriction
   */
  private int index = 0; // in code points

  private int documentIndex = 0; // current document index in code points (only for limiting)

  private int line = 0;
  private int column = 0; // in code points

  /**
   * @param loadSettings - configuration options
   * @param reader - the input
   * @deprecated use the other constructor with LoadSettings first
   */
  @Deprecated
  public StreamReader(Reader reader, LoadSettings loadSettings) {
    this(loadSettings, reader);
  }

  /**
   * Create
   *
   * @param loadSettings - configuration options
   * @param reader - the input
   */
  public StreamReader(LoadSettings loadSettings, Reader reader) {
    this.name = loadSettings.getLabel();
    this.codePointsWindow = new int[0];
    this.dataLength = 0;
    this.stream = reader;
    this.eof = false;
    this.bufferSize = loadSettings.getBufferSize();
    this.buffer = new char[bufferSize];
    this.useMarks = loadSettings.getUseMarks();
  }

  /**
   * @param stream - the input
   * @param loadSettings - configuration options
   * @deprecated use the other constructor with LoadSettings first
   */
  @Deprecated
  public StreamReader(String stream, LoadSettings loadSettings) {
    this(loadSettings, new StringReader(stream));
  }

  /**
   * Create
   *
   * @param loadSettings - configuration options
   * @param stream - the input
   */
  public StreamReader(LoadSettings loadSettings, String stream) {
    this(loadSettings, new StringReader(stream));
  }

  /**
   * Check if the all the data is human-readable (used in Representer)
   *
   * @param data - content to be checked for human-readability
   * @return true only when everything is human-readable
   */
  public static boolean isPrintable(final String data) {
    final int length = data.length();
    int offset = 0;
    while (offset < length) {
      final int codePoint = data.codePointAt(offset);
      if (!isPrintable(codePoint)) {
        return false;
      }
      offset += Character.charCount(codePoint);
    }
    return true;
  }

  /**
   * Check if the code point is human-readable
   *
   * @param c - code point to be checked for human-readability
   * @return true only when the code point is human-readable
   */
  public static boolean isPrintable(final int c) {
    return (c >= 0x20 && c <= 0x7E)
        || c == 0x9
        || c == 0xA
        || c == 0xD
        || c == 0x85
        || (c >= 0xA0 && c <= 0xD7FF)
        || (c >= 0xE000 && c <= 0xFFFD)
        || (c >= 0x10000 && c <= 0x10FFFF);
  }

  /**
   * Generate {@link Mark} if it is configured
   *
   * @return {@link Mark} of the current position or empty {@link Optional} otherwise
   */
  public Optional<Mark> getMark() {
    if (useMarks) {
      return Optional.of(
          new Mark(name, this.index, this.line, this.column, this.codePointsWindow, this.pointer));
    } else {
      return Optional.empty();
    }
  }

  /**
   * read the next character and move the pointer. if the last character is high surrogate one more
   * character will be read
   */
  public void forward() {
    forward(1);
  }

  /**
   * read the next length characters and move the pointer. if the last character is high surrogate
   * one more character will be read
   *
   * @param length amount of characters to move forward
   */
  public void forward(int length) {
    for (int i = 0; i < length && ensureEnoughData(); i++) {
      int c = codePointsWindow[pointer++];
      moveIndices(1);
      if (CharConstants.LINEBR.has(c)
          // do not count CR if it is followed by LF
          || (c == '\r' && (ensureEnoughData() && codePointsWindow[pointer] != '\n'))) {
        this.line++;
        this.column = 0;
      } else if (c != 0xFEFF) {
        this.column++;
      }
    }
  }

  /**
   * Peek the next code point (look without moving the pointer)
   *
   * @return the next code point or 0 if empty
   */
  public int peek() {
    return (ensureEnoughData()) ? codePointsWindow[pointer] : 0;
  }

  /**
   * Peek the next index-th code point
   *
   * @param index to peek
   * @return the next index-th code point or 0 if empty
   */
  public int peek(int index) {
    return (ensureEnoughData(index)) ? codePointsWindow[pointer + index] : 0;
  }

  /**
   * Create String from code points
   *
   * @param length amount of the characters to convert
   * @return the String representation
   */
  public String prefix(int length) {
    if (length == 0) {
      return "";
    } else if (ensureEnoughData(length)) {
      return new String(this.codePointsWindow, pointer, length);
    } else {
      return new String(this.codePointsWindow, pointer, Math.min(length, dataLength - pointer));
    }
  }

  /**
   * prefix(length) immediately followed by forward(length)
   *
   * @param length amount of characters to get
   * @return the next length code points
   */
  public String prefixForward(int length) {
    final String prefix = prefix(length);
    this.pointer += length;
    moveIndices(length);
    // prefix never contains new line characters
    this.column += length;
    return prefix;
  }

  private boolean ensureEnoughData() {
    return ensureEnoughData(0);
  }

  private boolean ensureEnoughData(int size) {
    if (!eof && pointer + size >= dataLength) {
      update();
    }
    return (this.pointer + size) < dataLength;
  }

  private void update() {
    try {
      int read = stream.read(buffer, 0, bufferSize - 1);
      if (read > 0) {
        int cpIndex = (dataLength - pointer);
        codePointsWindow = Arrays.copyOfRange(codePointsWindow, pointer, dataLength + read);

        if (Character.isHighSurrogate(buffer[read - 1])) {
          if (stream.read(buffer, read, 1) == -1) {
            eof = true;
          } else {
            read++;
          }
        }

        Optional<Integer> nonPrintable = Optional.empty();
        int i = 0;
        while (i < read) {
          int codePoint = Character.codePointAt(buffer, i);
          codePointsWindow[cpIndex] = codePoint;
          if (isPrintable(codePoint)) {
            i += Character.charCount(codePoint);
          } else {
            nonPrintable = Optional.of(codePoint);
            i = read;
          }
          cpIndex++;
        }

        dataLength = cpIndex;
        pointer = 0;
        if (nonPrintable.isPresent()) {
          throw new ReaderException(
              name, cpIndex - 1, nonPrintable.get(), "special characters are not allowed");
        }
      } else {
        eof = true;
      }

      if (false) {
        throw new IOException("j2cl Writer emulation fix");
      }

    } catch (IOException ioe) {
      throw new YamlEngineException(ioe);
    }
  }

  /** @return current position as number (in characters) from the beginning of the current line */
  public int getColumn() {
    return column;
  }

  private void moveIndices(int length) {
    this.index += length;
    this.documentIndex += length;
  }

  /**
   * Get the position of the currect char in the current YAML document
   *
   * @return index of the current position from the beginning of the current document
   */
  public int getDocumentIndex() {
    return documentIndex;
  }

  /** Reset the position to start (at the start of a new document in the stream) */
  public void resetDocumentIndex() {
    documentIndex = 0;
  }

  /** @return current position as number (in characters) from the beginning of the stream */
  public int getIndex() {
    return index;
  }

  /** @return current line from the beginning of the stream */
  public int getLine() {
    return line;
  }
}
