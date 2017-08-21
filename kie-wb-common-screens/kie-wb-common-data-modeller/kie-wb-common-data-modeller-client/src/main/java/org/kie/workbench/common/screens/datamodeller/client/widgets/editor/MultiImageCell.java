/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.kie.workbench.common.screens.datamodeller.model.editor.ImageWrapper;

public class MultiImageCell extends AbstractCell<List<ImageWrapper>> {

    public static int IMAGE_WIDTH = 15;
    public static int IMAGE_HEIGHT = 15;

    @Override
    public void render(final Context context,
                       final List<ImageWrapper> imageWrappers,
                       final SafeHtmlBuilder sb) {
        for (ImageWrapper imageWrapper : imageWrappers) {
            String imageBuilder = "<img src='" + imageWrapper.getUri() +
                    "' title='" + imageWrapper.getDescription() +
                    "' width='" + IMAGE_WIDTH +
                    "' height='" + IMAGE_HEIGHT +
                    "' style=\"margin-right: 5px;\">";
            sb.appendHtmlConstant(imageBuilder);
        }
    }
}
