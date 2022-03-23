/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility for parsing .properties files in GWT code.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class Properties {

  /**
   * @param data
   *          The contents of a .properties file.
   * @return A map of key-value pairs parsed from the .properties file contents.
   */
  public static Map<String, String> load(final String data) {
    final ParseState parseState = new ParseState(data);
    while (parseState.next());

    return parseState.properties;
  }

  private static class ParseState {
    private static enum State {
      LINE_START {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (input == '!' || input == '#') {
            state.state = COMMENT;
            state.index += 1;
          }
          else {
            state.state = PRE_KEY_WHITESPACE;
          }

          return state;
        }
      },
      COMMENT {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (input == '\n') {
            state.state = LINE_START;
          }
          state.index += 1;

          return state;
        }
      },
      PRE_KEY_WHITESPACE {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (Character.isWhitespace(input)) {
            state.index += 1;
          }
          else {
            state.state = KEY;
          }
          return state;
        }
      },
      KEY {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (Character.isWhitespace(input)) {
            state.state = POST_KEY_WHITESPACE;
          }
          else if (isSeparator(input)) {
            state.state = PRE_VALUE_WHITESPACE;
            state.index += 1;
          }
          else {
            state.key.append(input);
            state.index += 1;
          }
          return state;
        }
      },
      POST_KEY_WHITESPACE {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (Character.isWhitespace(input)) {
            state.index += 1;
          }
          else if (isSeparator(input)) {
            state.index += 1;
            state.state = PRE_VALUE_WHITESPACE;
          }
          else {
            state.state = VALUE_UNESCAPED;
          }

          return state;
        }
      },
      PRE_VALUE_WHITESPACE {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (Character.isWhitespace(input)) {
            state.index += 1;
          }
          else {
            state.state = VALUE_UNESCAPED;
          }

          return state;
        }
      },
      VALUE_UNESCAPED {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (input == '\\') {
            state.index += 1;
            state.state = VALUE_ESCAPED;
          }
          else if (input == '\n' || input == '\r') {
            state.state = LINE_ENDING;
          }
          else {
            state.index += 1;
            state.value.append(input);
          }

          return state;
        }
      },
      VALUE_ESCAPED {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (Character.isWhitespace(input)) {
            state.state = VALUE_IGNORED;
          }
          else {
            state.index += 1;
            state.state = VALUE_UNESCAPED;
            switch (input) {
              case 'r':
                state.value.append('\r');
                break;
              case 't':
                state.value.append('\t');
                break;
              case 'n':
                state.value.append('\n');
                break;
              case '=':
                state.value.append('=');
                break;
              case ':':
                state.value.append(':');
                break;
              case '\\':
                state.value.append('\\');
                break;
              case 'u':
                final String hexDigits = state.data.substring(state.index, state.index+4);
                state.index += 4;
                final int escapedChar = Integer.parseInt(hexDigits, 16);
                state.value.append((char) escapedChar);
                break;
              default:
                state.state = VALUE_IGNORED;
                return state;
            }

          }

          return state;
        }
      },
      VALUE_IGNORED {
        @Override
        ParseState accept(final char input, final ParseState state) {
          if (input == '\n') {
            state.index += 1;
            state.state = PRE_VALUE_WHITESPACE;
          }
          else {
            state.index += 1;
          }

          return state;
        }
      },
      LINE_END {
        @Override
        ParseState accept(final char input, final ParseState state) {
          state.state = LINE_START;
          state.index += 1;

          return state;
        }
      },
      LINE_ENDING {
        @Override
        ParseState accept(final char input, final ParseState state) {
          state.state = LINE_END;
          if (input == '\r') {
            if (state.data.charAt(state.index+1) == '\n') {
              state.index += 1;
            }
          }

          return state;
        }
      };

      abstract ParseState accept(char input, ParseState state);
      static boolean isSeparator(final char input) {
        return input == '=' || input == ':';
      }
    }

    final StringBuilder key = new StringBuilder(), value = new StringBuilder();
    State state = State.LINE_START;
    final String data;
    int index = 0;
    final Map<String, String> properties = new HashMap<>();

    ParseState(final String date) {
      this.data = date;
    }

    boolean next() {
      if (index > data.length()) {
        return false;
      }

      if (State.LINE_END.equals(state) || index == data.length()) {
        if (key.length() > 0 || value.length() > 0) {
          properties.put(key.toString(), value.toString());
          key.delete(0, key.length());
          value.delete(0, value.length());
        }
        if (index == data.length()) {
          index += 1;
          return false;
        }
      }

      final char c = data.charAt(index);
      state.accept(c, this);

      return true;
    }
  }

  private Properties() {}

}
