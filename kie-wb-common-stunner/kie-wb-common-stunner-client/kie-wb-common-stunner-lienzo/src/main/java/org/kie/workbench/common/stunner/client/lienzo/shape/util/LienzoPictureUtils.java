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

package org.kie.workbench.common.stunner.client.lienzo.shape.util;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Picture;
import org.uberfire.mvp.Command;

public class LienzoPictureUtils {

    public static void forceLoad(final Picture picture,
                                 final String data,
                                 final Command loadCallback) {
        final Command loadHandler = () -> {
            picture.getImageProxy().setImageShapeLoadedHandler(p -> loadCallback.execute());
            picture.getImageProxy().load(data);
        };
        if (picture.getImageProxy().isLoaded()) {
            loadHandler.execute();
        } else {
            picture.getImageProxy().setImageShapeLoadedHandler(p -> loadHandler.execute());
        }
    }

    public static void tryDestroy(final Picture picture,
                                  final Consumer<Picture> retryCallback) {
        // Destroy the <img..> related to the Glyph added to the DOM root by Picture's use of ImageLoader.
        // If the image has not completed loading attempts to remove from the DOM result in an error; therefore
        // schedule successive attempts until success.
        if (!retryDestroy(picture)) {
            retryCallback.accept(picture);
        }
    }

    public static boolean retryDestroy(final Picture picture) {
        if (picture != null && picture.isLoaded()) {
            picture.removeFromParent();
            picture.getImageProxy().getImage().removeFromParent();
            return true;
        }
        return false;
    }
}
