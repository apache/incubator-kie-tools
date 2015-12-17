/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHashingUtils {

    private static final String FILE_HASH_EMPTY_TAG = "$HASH()";

    private static final String FILE_HASH_EMPTY_EXPRESSION = "\\$HASH\\(\\)";

    private static final String MD5_VALUE_EXPRESSION = "\\w\\w\\w\\w\\w\\w\\w\\w" +
                                                       "\\w\\w\\w\\w\\w\\w\\w\\w" +
                                                       "\\w\\w\\w\\w\\w\\w\\w\\w" +
                                                       "\\w\\w\\w\\w\\w\\w\\w\\w";

    private static final String FILE_HASH_EXPRESSION = "\\$HASH\\(" + MD5_VALUE_EXPRESSION + "\\)";

    private static final Pattern fileHashEmptyPattern = Pattern.compile(FILE_HASH_EMPTY_EXPRESSION);

    private static final Pattern md5ValuePattern = Pattern.compile(MD5_VALUE_EXPRESSION);

    private static final Pattern fileHashPattern = Pattern.compile(FILE_HASH_EXPRESSION);

    public static String setFileHashValue(String content) {
        return setFileHashValue(content, md5Hex(content));
    }

    public static String extractFileHashValue(String content) {
        if (content == null || "".equals(content)) return null;

        Matcher fileHashMatcher = fileHashPattern.matcher(content);
        Matcher md5ValueMatcher;

        String fileHashValue = null;
        String md5Value = null;

        if (fileHashMatcher.find()) {
            fileHashValue = fileHashMatcher.group();
            md5ValueMatcher = md5ValuePattern.matcher(fileHashValue);
            if (md5ValueMatcher.find()) {
                md5Value = md5ValueMatcher.group();
            }
        }
        return md5Value;
    }

    public static boolean verifiesHash(String content) {
        return verifiesHash(content, extractFileHashValue(content));
    }

    public static boolean verifiesHash(String content, String expectedHashValue) {
        if (content == null || "".equals(content) || expectedHashValue == null || "".equals(expectedHashValue)) return false;

        String hashValue;
        content = content.replace(expectedHashValue, "");
        hashValue = FileHashingUtils.md5Hex(content);
        return expectedHashValue.equals(hashValue);
    }

    private static String setFileHashValue(String content, String hashValue) {
        if (content == null || hashValue == null) return content;
        return content.replaceFirst(FILE_HASH_EMPTY_EXPRESSION, "\\$HASH(" + hashValue + ")");
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     */
    public static String md5Hex(String content) {
        if (content == null) return null;
        return DigestUtils.md5Hex(content);
    }

    public static String getFileHashEmptyTag() {
        return FILE_HASH_EMPTY_TAG;
    }

}