/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.base.attributes;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.file.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

/**
 * This is a view that extends a Basic Attribute View and adds the "isHidden" attribute.
 * That attribute lets you know if the branch you are querying is a hidden branch or not.
 * Hidden branches should not be used, are just a mechanism to merge.
 */
public abstract class HiddenAttributeView<P extends Path>
        extends AbstractBasicFileAttributeView<P> {

    public static final String HIDDEN = "hidden";

    public HiddenAttributeView(final P path) {
        super(path);
    }

    @Override
    public String name() {
        return HIDDEN;
    }

    public abstract HiddenAttributes readAttributes() throws IOException;

    @Override
    public Map<String, Object> readAttributes(final String... attributes) {
        final HiddenAttributes attrs = readAttributes();

        return new HashMap<String, Object>(super.readAttributes(attributes)) {{

            for (final String attribute : attributes) {
                checkNotEmpty("attribute",
                              attribute);

                if (attribute.equals("*") || attribute.equals(HIDDEN)) {
                    put(HIDDEN,
                        attrs.isHidden());
                }

                if (attribute.equals("*")) {
                    break;
                }
            }
        }};
    }
}