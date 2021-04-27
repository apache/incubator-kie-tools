/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.server;

import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.server.json.impl.ErraiJSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * High-performance stream JSON parser. Provides the decoding algorithm to interpret the Errai Wire Protcol,
 * including serializable types.  This parser always assumes the outer payload is a Map. So it probably shouldn't
 * be used as a general parser.
 *
 * @author Mike Brock
 * @since 1.1
 */
public class JSONStreamDecoder {
  private final CharBuffer buffer;
  private final BufferedReader reader;

  private char carry;
  private int read;
  private boolean initial = true;

  /**
   * Decodes the JSON payload by reading from the given stream of UTF-8 encoded
   * characters. Reads to the end of the input stream unless there are errors,
   * in which case the current position in the stream will not be at EOF, but
   * may possibly be beyond the character that caused the error.
   *
   * @param inStream
   *          The input stream to read from. It must contain character data
   *          encoded as UTF-8, and it must be positioned to read from the start
   *          of the JSON message to be parsed.
   */
  public JSONStreamDecoder(final InputStream inStream) {
    this.buffer = CharBuffer.allocate(25);
    try {
      this.reader = new BufferedReader(
              new InputStreamReader(inStream, "UTF-8")
      );
    }
    catch (UnsupportedEncodingException e) {
      throw new Error("UTF-8 is not supported by this JVM?", e);
    }
  }

  public static EJValue decode(final InputStream instream) throws IOException {
    return new JSONStreamDecoder(instream).parse();
  }

  public char read() throws IOException {
    if (carry != 0) {
      final char oldCarry = carry;
      carry = 0;
      return oldCarry;
    }
    if (read <= 0) {
      if (!initial) buffer.rewind();
      initial = false;
      if ((read = reader.read(buffer)) <= 0) {
        return 0;
      }
      buffer.rewind();
    }
    read--;
    return buffer.get();
  }

  public EJValue parse() {
    try {
      return new ErraiJSONValue(_parse(new OuterContext()));
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Object _parse(Context ctx) throws IOException {
    char c;
    StringBuilder appender;
    while ((c = read()) != 0) {
      switch (c) {
        case '[':
          ctx.addValue(_parse(new ArrayContext(new ArrayList<Object>())));
          break;

        case '{':
          ctx.addValue(_parse(new ObjectContext(new LinkedHashMap<Object, Object>())));
          break;

        case ']':
        case '}':
          return ctx.record();

        case ',':
          ctx.record();
          break;

        case '"':
        case '\'':
          char term = c;
          appender = new StringBuilder(100);
          StrCapture:
          while ((c = read()) != 0) {
            switch (c) {
              case '\\':
                appender.append(handleEscapeSequence());
                break;
              case '"':
              case '\'':
                if (c == term) {
                  ctx.addValue(appender.toString());
                  term = 0;
                  break StrCapture;
                }
              default:
                appender.append(c);
            }
          }

          if (term != 0) {
            throw new RuntimeException("unterminated string literal");
          }
          break;

        case ':':
          continue;

        default:
          if (isNumberStart(c)) {
            carry = c;
            ctx.addValue(parseDouble());
            break;
          }
          else if (Character.isJavaIdentifierPart(c)) {
            appender = new StringBuilder(100).append(c);

            while (((c = read()) != 0) && Character.isJavaIdentifierPart(c)) {
              appender.append(c);
            }

            String s = appender.toString();

            if (s.length() > 5) ctx.addValue(s);
            else if ("null".equals(s)) {
              ctx.addValue(null);
            }
            else if ("true".equals(s)) {
              ctx.addValue(Boolean.TRUE);
            }
            else if ("false".equals(s)) {
              ctx.addValue(Boolean.FALSE);
            }
            else {
              ctx.addValue(s);
            }

            if (c != 0) carry = c;
          }
      }
    }

    return ctx.record();
  }

  private char handleEscapeSequence() throws IOException {
    char c;
    switch (c = read()) {
      case '\\':
        return '\\';
      case '/':
        return '/';
      case 'b':
        return '\b';
      case 'f':
        return '\f';
      case 't':
        return '\t';
      case 'r':
        return '\r';
      case 'n':
        return '\n';
      case '\'':
        return '\'';
      case '"':
        return '\"';
      case 'u':
        //handle unicode
        char[] unicodeSeq = new char[4];
        int i = 0;
        for (; i < 4 && isValidHexPart(c = read()); i++) {
          unicodeSeq[i] = c;
        }
        if (i != 4) {
          throw new RuntimeException("illegal unicode escape sequence: expected 4 hex characters after \\u");
        }

        return (char) Integer.decode("0x" + new String(unicodeSeq)).intValue();

      default:
        throw new RuntimeException("illegal escape sequence: " + c);
    }
  }

  /** The states the double recognizer can go through while attempting to parse a JSON numeric value. */
  private static enum State { READ_SIGN, READ_INT, READ_FRAC, READ_EXP_SIGN, READ_EXP };

  /**
   * Parses a JSON numeric literal <b>with the side effect of consuming
   * characters from the input</b> up until a character is encountered that
   * cannot be used to form a JSON number. JSON numbers have the following
   * grammar:
   *
   * <dl>
   * <dt><i>number</i>
   * <dd><i>int</i>
   * <dd><i>int frac</i>
   * <dd><i>int exp</i>
   * <dd><i>int frac exp</i>
   *
   * <dt><i>int</i>
   * <dd><i>digit</i>
   * <dd><i>digit1-9</i> <i>digits</i>
   * <dd><b>'-'</b> <i>digit</i>
   * <dd><b>'-'</b> <i>digit1-9</i> <i>digits</i>
   *
   * <dt><i>frac</i>
   * <dd><b>'.'</b> <i>digits</i>
   *
   * <dt><i>exp</i>
   * <dd><i>e digits</i>
   *
   * <dt><i>digits</i>
   * <dd><i>digit</i>
   * <dd><i>digit digits</i>
   *
   * <dt><i>digit1-9</i>
   * <dd><b>'1'</b> | <b>'2'</b> | <b>'3'</b> | <b>'4'</b> | <b>'5'</b> |
   * <b>'6'</b> | <b>'7'</b> | <b>'8'</b> | <b>'9'</b>
   *
   * <dt><i>digit</i>
   * <dd><b>'0'</b> | <b>'1'</b> | <b>'2'</b> | <b>'3'</b> | <b>'4'</b> |
   * <b>'5'</b> | <b>'6'</b> | <b>'7'</b> | <b>'8'</b> | <b>'9'</b>
   *
   * <dt><i>e</i>
   * <dd><b>'e'</b> | <b>'e+'</b> | <b>'e-'</b> | <b>'E'</b> | <b>'E+'</b> |
   * <b>'E-'</b>
   * </dl>
   *
   * @return The number that was parsed from the input stream.
   * <p><i>Note on side effects:</i>after this method returns, the next
   * @throws IOException
   */
  private double parseDouble() throws IOException {
    final StringBuilder sb = new StringBuilder(25);

    State state = State.READ_SIGN;

    char c;

    recognize:
    while ((c = read()) != 0) {
      switch (state) {

      case READ_SIGN:
        if (c == '-' || ('0' <= c && c <= '9')) {
          sb.append(c);
          state = State.READ_INT;
        }
        else {
          throw new NumberFormatException("Found '" + c + "' but expected '-' or a digit 1-9");
        }
        break;

      case READ_INT:
        if ('0' <= c && c <= '9') {
          sb.append(c);
        }
        else if (c == '.') {
          sb.append(c);
          state = State.READ_FRAC;
        }
        else if (c == 'E' || c == 'e') {
          sb.append(c);
          state = State.READ_EXP_SIGN;
        }
        else {
          // found the end of the numeric literal
          carry = c;
          break recognize;
        }
        break;

      case READ_FRAC:
        if ('0' <= c && c <= '9') {
          sb.append(c);
        }
        else if (c == 'E' || c == 'e') {
          sb.append(c);
          state = State.READ_EXP_SIGN;
        }
        else {
          // found the end of the numeric literal
          carry = c;
          break recognize;
        }
        break;

      case READ_EXP_SIGN:
        if (c == '-' || c == '+' || ('0' <= c && c <= '9')) {
          sb.append(c);
          state = State.READ_EXP;
        }
        else {
          throw new NumberFormatException("The numeric literal \"" + sb + "\" is malformed (can't end with e or E)");
        }
        break;

      case READ_EXP:
        if ('0' <= c && c <= '9') {
          sb.append(c);
        }
        else {
          // found the end of the numeric literal
          carry = c;
          break recognize;
        }
        break;
      }
    }

    return Double.parseDouble(sb.toString());
  }

  /**
   * Returns true if c could be the start of a JSON number. Note that a return
   * value of true does not indicate that the value will be a valid number. JSON
   * numbers are not permitted to begin with a '0' or a '.', so in those cases
   * {@link #parseDouble()} will throw an error even though this method returned
   * true. This is an acceptable outcome, though, because there is nothing else
   * the errant character could represent in the JSON stream.
   *
   * @param c
   *          the character to test
   * @return true if c is a numeric digit, '-', or '.'.
   */
  private static boolean isNumberStart(char c) {
    switch (c) {
      case '.':
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return true;
      default:
        return false;
    }
  }

  private static boolean isValidHexPart(char c) {
    switch (c) {
      case '.':
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
        return true;
      default:
        return false;
    }
  }

  private static abstract class Context<T> {
    abstract T record();

    abstract void addValue(Object val);
  }

  private static class OuterContext extends Context<Object> {
    private Context _wrapped;
    private Object col;

    @Override
    Object record() {
      return col;
    }

    @SuppressWarnings("unchecked")
    @Override
    void addValue(Object val) {
      if (_wrapped == null) {
        if (val instanceof List) {
          _wrapped = new ArrayContext((List<Object>) (col = val));
        }
        else if (val instanceof Map) {
          _wrapped = new ObjectContext((Map<Object, Object>) (col = val));
        }
        else if (val instanceof String) {
            _wrapped = new StringContext((String) (col = val));
        }
        else {
          throw new RuntimeException("expected list or map but found: " + (val == null ? null : val.getClass().getName()));
        }
      }
      else {
        _wrapped.addValue(val);
      }
    }
  }

  private static class ArrayContext extends Context<List> {
    List<Object> collection;

    private ArrayContext(List<Object> collection) {
      this.collection = collection;
    }

    @Override
    void addValue(Object val) {
      collection.add(val);
    }

    @Override
    public List record() {
      return collection;
    }
  }

  private static class StringContext extends Context<String> {
    String value;

    private StringContext(String value) {
      this.value = value;
    }

    @Override
    void addValue(Object val) {}

    @Override
    public String record() {
      return value;
    }
  }

  private static class ObjectContext extends Context<Map> {
    protected Object lhs;
    protected Object rhs;

    Map<Object, Object> collection;

    private ObjectContext(Map<Object, Object> collection) {
      this.collection = collection;
    }

    @Override
    void addValue(Object val) {
      if (lhs == null) {
        lhs = val;
      }
      else {
        rhs = val;
      }
    }

    @Override
    Map record() {
      if (lhs != null) {
        collection.put(lhs, rhs);
      }
      lhs = rhs = null;
      return collection;
    }
  }
}
