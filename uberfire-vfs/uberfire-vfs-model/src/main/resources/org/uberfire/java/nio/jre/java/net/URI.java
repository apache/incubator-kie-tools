/*
 * Copyright 2012 JBoss Inc
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

package java.net;

import java.io.Serializable;

public class URI implements Serializable, Comparable<URI> {

    private String uriString;

    private URI() {
        // intentionally blank
    }

    public static URI create(final String str) throws URISyntaxException {
        return new URI(str);
    }

    public URI(String str) throws URISyntaxException {
        uriString = str;
    }

    @Override
    public int compareTo(URI o) {
        return uriString.compareTo(o.toString());
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return uriString.equals(((URI) obj).toString());
    }

    @Override
    public String toString() {
        return uriString;
    }

}
