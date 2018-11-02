/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
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

package org.uberfire.ext.security.management.wildfly.properties;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.as.domain.management.security.PropertiesFileLoader;
import org.jboss.as.domain.management.security.UserPropertiesFileLoader;

/**
 * In order to ensure as much as possible the compatibility and behavior between
 * this security management provider and the Wildfly's script <code>add-user.sh</code>,
 * each users and group security provider implementation delegates the handling for the properties file to
 * the Wildfly's libraries, which are the ones that the <code>add-user.sh </code> script relies on as well.
 * <p>
 * On the other hand, in order to follow the security management capabilities and restrictions,
 * it behaves different rather than the default implementation in a few aspects, so this Predicate
 * provides a shared way to override the default implementations and test if lines
 * should be finally skipped or written into the output stream.
 * <p>
 * See parent property file handler implementations here:
 * @see UserPropertiesFileLoader
 * @see PropertiesFileLoader
 * See security management implementations:
 * @see WildflyUserPropertiesManager
 * @see WildflyGroupPropertiesManager
 * <p>
 * This predicate considers the following behaviors than differ from the default implementation ones:
 * <p>
 * - Allows using empty values for the entries. For the users file, it implies empty passwords are supported.
 * <p>
 * - User and group deletion is allowed by the security management providers.
 * <p>
 * - The <code>add-user.sh</code> supports enable/disabling users, but the security management does not.
 */
public class PropertiesLineWriterPredicate implements Predicate<String> {

    public static final Pattern PROPERTY_PATTERN = Pattern.compile("#?+((?:[,.\\-@/a-zA-Z0-9]++|(?:\\\\[=\\\\])++)++)=(.++)");
    public static final Pattern EMPTY_PROPERTY_PATTERN = Pattern.compile("#?+((?:[,.\\-@/a-zA-Z0-9]++|(?:\\\\[=\\\\])++)++)=(\\[\\].*?)*");

    private final Function<String, String> keyRawValueProvider;
    private final boolean allowEmptyEntryValue;
    private Properties properties;

    public PropertiesLineWriterPredicate(final Function<String, String> keyRawValueProvider,
                                         final boolean allowEmptyEntryValue) {
        this.keyRawValueProvider = keyRawValueProvider;
        this.allowEmptyEntryValue = allowEmptyEntryValue;
    }

    public PropertiesLineWriterPredicate begin(Properties p) {
        this.properties = p;
        return this;
    }

    public PropertiesLineWriterPredicate end() {
        this.properties = null;
        return this;
    }

    /**
     * This method is being extended for 2 reasons:
     * 1.- This implementation support deleting a valid key-value pair.
     * 2.- This implementation allows use of empty values for keys.
     * <p>
     * <b>Support for deleting valid key-value pairs.</b>
     * The parent's behavior is to not support deleting users/groups, just disabling
     * is allowed. Sp this implementation considers removed entries from the in-memory
     * properties instance.
     * <p>
     * <b>Support for empty key's value.</b>
     * Notice only makes sense to use this feature for the users property file.
     * The default implementation considers a key-value pair valid in case the value is not empty.
     * For example <code>user1=</code> is not considered valid key-value pairs for users, which
     * implies that the super-type implementation will write the line in the resulting output stream.
     * In order to allow empty passwords as well, this checks if the content is really
     * a valid key-pair too but considering empty values.
     * @param line The key-value pair line
     * @return true if the line must be written into the out stream, false otherwise.
     */
    @Override
    public boolean test(final String line) {
        boolean result = false;
        final String trimmed = line.trim();
        if (trimmed.length() > 0) {
            Matcher userLineMatcher = null;
            boolean isEmptyValue = false;
            final Matcher defaultPatternMatcher = PROPERTY_PATTERN.matcher(trimmed);
            if (defaultPatternMatcher.matches()) {
                userLineMatcher = defaultPatternMatcher;
            } else if (allowEmptyEntryValue) {
                final Matcher emptyPatternMatcher = EMPTY_PROPERTY_PATTERN.matcher(trimmed);
                if (emptyPatternMatcher.matches()) {
                    userLineMatcher = emptyPatternMatcher;
                    isEmptyValue = true;
                }
            }
            // By default, if the line does not match any know valid key-pair for a user/group,
            // write it into the output stream (eg: to keep the original file comments,
            // custom realm keywords, etc).
            result = null == userLineMatcher;
            if (!result) {
                final String username = keyRawValueProvider.apply(userLineMatcher.group(1));
                final String inMemoryValue = properties.getProperty(username);
                // At this point it ensures the line will be written in case only the user/group exists
                // in the in-memory properties instance.
                if (null != inMemoryValue) {
                    final boolean isMemoryValueEmpty = 0 == inMemoryValue.trim().length();
                    // Ensure only write the line if the key has some value or it's empty.
                    result = !isEmptyValue || isMemoryValueEmpty;
                }
            }
        }
        return result;
    }
}