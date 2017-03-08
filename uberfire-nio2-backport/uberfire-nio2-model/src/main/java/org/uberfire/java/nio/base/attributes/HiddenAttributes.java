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

import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

/**
 * Represents files attributes with the addition of a hidden field.
 * That hidden attribute tell if a branch is hidden or not.
 * I.E.: A Pull Request hidden branch.
 * You should not use those branches unless you have to use them.
 */
public interface HiddenAttributes extends BasicFileAttributes {

    boolean isHidden();
}
