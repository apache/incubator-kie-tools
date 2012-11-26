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

package org.uberfire.java.nio.fs.jgit;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

public class URITest {

    @Test
    public void testURI() throws URISyntaxException {
        final URI uri = new URI("git://branch@repo-name/path/to/file.txt");

        assertThat(uri.getScheme()).isEqualTo("git");
        assertThat(uri.getAuthority()).isEqualTo("branch@repo-name");
        assertThat(uri.getPath()).isEqualTo("/path/to/file.txt");
        assertThat(uri.getQuery()).isNull();

        final URI uri2 = new URI("git://repo-name");
        assertThat(uri2).isNotNull();
        assertThat(uri2.getAuthority()).isEqualTo("repo-name");

        final URI uri3 = URI.create("git://branch@repo-name/path/to/file.txt");
        assertThat(uri3).isNotNull();
        assertThat(uri3.getScheme()).isEqualTo("git");
        assertThat(uri3.getAuthority()).isEqualTo("branch@repo-name");
        assertThat(uri3.getPath()).isEqualTo("/path/to/file.txt");
        assertThat(uri3.getQuery()).isNull();

        final URI uri4 = URI.create("git://master@my-repo/:path/to/some/place.txt");
        assertThat(uri4).isNotNull();
        assertThat(uri4.getScheme()).isEqualTo("git");
        assertThat(uri4.getAuthority()).isEqualTo("master@my-repo");
        assertThat(uri4.getPath()).isEqualTo("/:path/to/some/place.txt");
        assertThat(uri4.getQuery()).isNull();

        final URI uri5 = URI.create("git://origin/master@my-repo/:path/to/some/place.txt");
        assertThat(uri5).isNotNull();
        assertThat(uri5.getScheme()).isEqualTo("git");
        assertThat(uri5.getAuthority()).isEqualTo("origin");
        assertThat(uri5.getPath()).isEqualTo("/master@my-repo/:path/to/some/place.txt");
        assertThat(uri5.getQuery()).isNull();

        final URI uri6 = URI.create("git://origin/master@my-repo/path/to/some/place.txt");
        assertThat(uri6).isNotNull();
        assertThat(uri6.getScheme()).isEqualTo("git");
        assertThat(uri6.getAuthority()).isEqualTo("origin");
        assertThat(uri6.getPath()).isEqualTo("/master@my-repo/path/to/some/place.txt");
        assertThat(uri6.getQuery()).isNull();
    }

}
