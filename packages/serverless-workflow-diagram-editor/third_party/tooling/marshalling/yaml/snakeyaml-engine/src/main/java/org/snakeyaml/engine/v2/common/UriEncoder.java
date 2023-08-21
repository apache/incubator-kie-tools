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
package org.snakeyaml.engine.v2.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.snakeyaml.engine.external.com.google.gdata.util.common.base.Escaper;
import org.snakeyaml.engine.external.com.google.gdata.util.common.base.PercentEscaper;
import org.snakeyaml.engine.v2.GwtIncompatible;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/** To be decided */
public abstract class UriEncoder {

  // private static final CharsetDecoder UTF8Decoder =
  //    StandardCharsets.UTF_8.newDecoder(); //TODO .onMalformedInput(CodingErrorAction.REPORT);
  // Include the [] chars to the SAFEPATHCHARS_URLENCODER to avoid
  // its escape as required by spec. See
  private static final String SAFE_CHARS = PercentEscaper.SAFEPATHCHARS_URLENCODER + "[]/";
  private static final Escaper escaper = new PercentEscaper(SAFE_CHARS, false);

  private UriEncoder() {}

  /**
   * Escape special characters with '%'
   *
   * @param uri URI to be escaped
   * @return encoded URI
   */
  public static String encode(String uri) {
    return escaper.escape(uri);
  }

  /**
   * Decode '%'-escaped characters. Decoding fails in case of invalid UTF-8
   *
   * @param buff data to decode
   * @return decoded data
   * @throws Exception if cannot be decoded
   */
  public static String decode(ByteBuffer buff) throws Exception {
    throw new UnsupportedOperationException("Not implemented");
    /*CharBuffer chars = UTF8Decoder.decode(buff);
    return chars.toString();*/
  }

  /**
   * Decode with URLDecoder
   *
   * @param buff - the source
   * @return decoded with UTF-8
   */
  @GwtIncompatible
  public static String decode(String buff) {
    try {
      return java.net.URLDecoder.decode(buff, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new YamlEngineException(e);
    }
  }
}
