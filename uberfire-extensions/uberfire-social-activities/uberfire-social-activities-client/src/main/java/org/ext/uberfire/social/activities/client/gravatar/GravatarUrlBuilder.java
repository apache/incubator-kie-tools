/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client.gravatar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.googlecode.gwt.crypto.bouncycastle.util.encoders.Hex;

/**
 * Url Builder
 * @author francois wauquier
 */
public class GravatarUrlBuilder {

    private static GravatarUrlBuilder instance;

    private GravatarUrlBuilder() {
    }

    /**
     * Get unique instance
     * @return
     */
    public static GravatarUrlBuilder get() {
        if (instance == null) {
            instance = new GravatarUrlBuilder();
        }
        return instance;
    }

    /**
     * Build the url
     * @param email
     * @return
     */
    public String build(final String email,
                        final int size) {
        return "http://www.gravatar.com/avatar/" + hash(email) + "?s=" + size + "&d=mm";
    }

    private String hash(String email) {
        try {
            String cleanEmail = email.trim().toLowerCase();
            return new String(Hex.encode(MessageDigest.getInstance("MD5").digest(cleanEmail.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 implementation not found",
                                       e);
        }
    }
}
