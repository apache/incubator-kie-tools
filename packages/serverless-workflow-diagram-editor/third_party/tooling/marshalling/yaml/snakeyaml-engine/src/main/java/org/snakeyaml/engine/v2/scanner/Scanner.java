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

import java.util.Iterator;

import org.snakeyaml.engine.v2.exceptions.ScannerException;
import org.snakeyaml.engine.v2.tokens.Token;

/**
 * This interface represents an input stream of {@link Token}s.
 *
 * <p>The scanner and the parser form together the 'Parse' step in the loading process.
 *
 * @see <a href="https://yaml.org/spec/1.2.2/#31-processes">3.1. Processes</a>
 */
public interface Scanner extends Iterator<Token> {

  /**
   * Check if the next token is one of the given types.
   *
   * @param choices token IDs to match with
   * @return <code>true</code> if the next token is one of the given types. Returns <code>false
   *     </code> if no more tokens are available.
   * @throws ScannerException Thrown in case of malformed input.
   */
  boolean checkToken(Token.ID... choices);

  /**
   * Return the next token, but do not delete it from the stream.
   *
   * @return The token that will be returned on the next call to {@link #next}
   * @throws ScannerException Thrown in case of malformed input.
   * @throws IndexOutOfBoundsException if no more token left
   */
  Token peekToken();

  /**
   * Returns the next token.
   *
   * <p>The token will be removed from the stream. (Every invocation of this method must happen
   * after calling either {@link #checkToken} or {@link #peekToken()}
   *
   * @return the coming token
   * @throws ScannerException Thrown in case of malformed input.
   * @throws IndexOutOfBoundsException if no more token left
   */
  Token next();

  /** Set the document index to 0 after a document end */
  void resetDocumentIndex();
}
