/* Copyright (c) 2008-2010, Avian Contributors

Permission to use, copy, modify, and/or distribute this software
for any purpose with or without fee is hereby granted, provided
that the above copyright notice and this permission notice appear
in all copies.

There is NO WARRANTY for this software.  See license.txt for
details. */

package java.io;

public class InputStreamReader extends Reader {
  private final InputStream in;

  private final Utf8Decoder utf8Decoder;

  public InputStreamReader(InputStream in) {
    this.in = in;
    this.utf8Decoder = new Utf8Decoder();
  }

  public InputStreamReader(InputStream in, String encoding) throws UnsupportedEncodingException {
    this(in);
  }

  public int read(char[] b, int offset, int length) {
    byte[] buffer = new byte[length];
    int c = 0;
    try {
      c = in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return c <= 0 ? c : utf8Decoder.decode(buffer, 0, c, b, offset);
  }

  public void close() throws IOException {
    in.close();
  }

  /**
   * Utf8Decoder converts UTF-8 encoded bytes into characters properly handling buffer boundaries.
   *
   * <p>This class is stateful and up to 4 calls to {@link #decode(byte)} may be needed before a
   * character is appended to the char buffer.
   *
   * <p>The UTF-8 decoding is done by this class and no additional buffers are created. The UTF-8
   * code was inspired by http://bjoern.hoehrmann.de/utf-8/decoder/dfa/
   *
   * @author davebaol
   */
  public static class Utf8Decoder {

    private static final char REPLACEMENT = '\ufffd';
    private static final int UTF8_ACCEPT = 0;
    private static final int UTF8_REJECT = 12;

    // This table maps bytes to character classes to reduce
    // the size of the transition table and create bitmasks.
    private static final byte[] BYTE_TABLE = {
      // @off - disable libGDX formatter
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
          9,
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
          7,
      8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
          2,
      10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
          8
      // @on - enable libGDX formatter
    };

    // This is a transition table that maps a combination of a
    // state of the automaton and a character class to a state.
    private static final byte[] TRANSITION_TABLE = {
      // @off - disable libGDX formatter
      0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
      12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12,
      12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12,
          12,
      12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12,
          12,
      12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12
      // @on - enable libGDX formatter
    };

    private int codePoint;
    private int state;
    private final char[] utf16Char = new char[2];
    private char[] charBuffer;
    private int charOffset;

    public Utf8Decoder() {
      this.state = UTF8_ACCEPT;
    }

    protected void reset() {
      state = UTF8_ACCEPT;
    }

    public int decode(byte[] b, int offset, int length, char[] charBuffer, int charOffset) {
      this.charBuffer = charBuffer;
      this.charOffset = charOffset;
      int end = offset + length;
      for (int i = offset; i < end; i++) decode(b[i]);
      return this.charOffset - charOffset;
    }

    private void decode(byte b) {

      if (b > 0 && state == UTF8_ACCEPT) {
        charBuffer[charOffset++] = (char) (b & 0xFF);
      } else {
        int i = b & 0xFF;
        int type = BYTE_TABLE[i];
        codePoint = state == UTF8_ACCEPT ? (0xFF >> type) & i : (i & 0x3F) | (codePoint << 6);
        int next = TRANSITION_TABLE[state + type];

        switch (next) {
          case UTF8_ACCEPT:
            state = next;
            if (codePoint < Character.MIN_HIGH_SURROGATE) {
              charBuffer[charOffset++] = (char) codePoint;
            } else {
              // The code below is equivalent to
              // for (char c : Character.toChars(codePoint)) charBuffer[charOffset++] = c;
              // but does not allocate a char array.
              int codePointLength = Character.toChars(codePoint, utf16Char, 0);
              charBuffer[charOffset++] = utf16Char[0];
              if (codePointLength == 2) charBuffer[charOffset++] = utf16Char[1];
            }
            break;

          case UTF8_REJECT:
            codePoint = 0;
            state = UTF8_ACCEPT;
            charBuffer[charOffset++] = REPLACEMENT;
            break;

          default:
            state = next;
            break;
        }
      }
    }
  }
}
