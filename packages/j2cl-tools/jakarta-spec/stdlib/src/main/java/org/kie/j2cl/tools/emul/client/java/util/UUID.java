/*
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
package java.util;

import java.io.Serializable;

/**
 * UUID class.
 *
 * @author senk.christian@gmail.com
 * @version $Id: $Id
 */
public class UUID implements Serializable, Comparable<UUID> {

  private static final long serialVersionUID = 7373345728974414241L;
  private static final char[] CHARS =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

  private String value;

  /** */
  private UUID() {}

  /**
   * fromString.
   *
   * @param uuidString a {@link String} object.
   * @return a {@link UUID} object.
   */
  public static UUID fromString(String uuidString) {
    // TODO: Validation

    final UUID uuid = new UUID();
    uuid.value = uuidString;

    return uuid;
  }

  /**
   * randomUUID.
   *
   * @return a {@link UUID} object.
   */
  public static UUID randomUUID() {
    return fromString(generateUUIDString());
  }

  /** Generate a RFC4122, version 4 ID. Example: "92329D39-6F5C-4520-ABFC-AAB64544E172" */
  private static String generateUUIDString() {
    char[] uuid = new char[36];
    int r;

    // rfc4122 requires these characters
    uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    uuid[14] = '4';

    // Fill in random data.  At i==19 set the high bits of clock sequence as
    // per rfc4122, sec. 4.1.5
    for (int i = 0; i < 36; i++) {
      if (uuid[i] == 0) {
        r = (int) (Math.random() * 16);
        uuid[i] = CHARS[(i == 19) ? (r & 0x3) | 0x8 : r & 0xf];
      }
    }
    return new String(uuid);
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  /** {@inheritDoc} */
  @Override
  public int compareTo(UUID arg0) {
    return value.compareTo(arg0.value);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    UUID other = (UUID) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /** {@inheritDoc} */
  @Override
  public String toString() {
    return value;
  }
}
