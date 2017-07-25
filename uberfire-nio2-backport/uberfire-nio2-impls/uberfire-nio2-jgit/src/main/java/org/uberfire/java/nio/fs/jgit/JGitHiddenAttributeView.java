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

package org.uberfire.java.nio.fs.jgit;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.attributes.HiddenAttributeView;
import org.uberfire.java.nio.base.attributes.HiddenAttributes;
import org.uberfire.java.nio.base.attributes.HiddenAttributesImpl;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.fs.jgit.daemon.filters.HiddenBranchRefFilter;

/**
 * This is the JGit implementation of the {@link HiddenAttributeView}.
 * It builds the HiddenAttributes object with "isHidden" attribute information.
 * That attribute lets you know if the branch you are querying is a hidden branch or not.
 * Hidden branches should not be used, are just a mechanism to merge.
 */
public class JGitHiddenAttributeView extends HiddenAttributeView<JGitPathImpl> {

    private HiddenAttributes attrs = null;

    public JGitHiddenAttributeView(final JGitPathImpl path) {
        super(path);
    }

    @Override
    public HiddenAttributes readAttributes() throws IOException {
        if (attrs == null) {
            attrs = buildAttrs(path.getFileSystem(),
                               path.getRefTree(),
                               path.getPath());
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{HiddenAttributeView.class, JGitVersionAttributeView.class};
    }

    private HiddenAttributes buildAttrs(final JGitFileSystem fileSystem,
                                        final String refTree,
                                        final String path) {
        return new HiddenAttributesImpl(new JGitBasicAttributeView(this.path).readAttributes(),
                                        HiddenBranchRefFilter.isHidden(refTree));
    }
}
