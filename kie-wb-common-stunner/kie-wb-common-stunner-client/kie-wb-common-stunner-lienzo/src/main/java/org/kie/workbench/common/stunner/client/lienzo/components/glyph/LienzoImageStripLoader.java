/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.image.ImageStrips;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionInitializer;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

@Default
@Dependent
public class LienzoImageStripLoader implements SessionInitializer {

    private final DefinitionUtils definitionUtils;
    private final ImageStripRegistry stripRegistry;
    private final BiConsumer<com.ait.lienzo.client.core.image.ImageStrip[], Runnable> lienzoStripRegistration;
    private final Consumer<String> lienzoStripRemoval;
    private final Set<String> registered;

    private static ImageStrips imageStrips = ImageStrips.get();

    @Inject
    public LienzoImageStripLoader(final DefinitionUtils definitionUtils,
                                  final ImageStripRegistry stripRegistry) {
        this(definitionUtils,
             stripRegistry,
             LienzoImageStripLoader::registerIntoLienzo,
             LienzoImageStripLoader::removeFromLienzo);
    }

    LienzoImageStripLoader(final DefinitionUtils definitionUtils,
                           final ImageStripRegistry stripRegistry,
                           final BiConsumer<com.ait.lienzo.client.core.image.ImageStrip[], Runnable> lienzoStripRegistration,
                           final Consumer<String> lienzoStripRemoval) {
        this.definitionUtils = definitionUtils;
        this.stripRegistry = stripRegistry;
        this.lienzoStripRegistration = lienzoStripRegistration;
        this.lienzoStripRemoval = lienzoStripRemoval;
        this.registered = new HashSet<>();
    }

    @Override
    public void init(final Metadata metadata,
                     final Command completeCallback) {
        final Annotation qualifier = definitionUtils.getQualifier(metadata.getDefinitionSetId());
        final ImageStrip[] strips = stripRegistry.get(DefinitionManager.DEFAULT_QUALIFIER,
                                                      qualifier);
        registerStrips(strips,
                       completeCallback);
    }

    @Override
    public void destroy() {
        registered.forEach(lienzoStripRemoval::accept);
        registered.clear();
    }

    private void registerStrips(final ImageStrip[] strips,
                                final Command callback) {
        final com.ait.lienzo.client.core.image.ImageStrip[] instances =
                Arrays.stream(strips)
                        .filter(strip -> !isRegistered(strip))
                        .map(LienzoImageStripLoader::convert)
                        .toArray(com.ait.lienzo.client.core.image.ImageStrip[]::new);
        lienzoStripRegistration.accept(instances,
                                       () -> {
                                           for (com.ait.lienzo.client.core.image.ImageStrip strip : instances) {
                                               registered.add(strip.getName());
                                           }
                                           callback.execute();
                                       });
    }

    private boolean isRegistered(final ImageStrip strip) {
        return registered.contains(ImageStripRegistry.getName(strip));
    }

    private static void registerIntoLienzo(final com.ait.lienzo.client.core.image.ImageStrip[] instances,
                                           final Runnable callback) {
       imageStrips.register(instances, callback);
    }

    protected static void removeFromLienzo(final String name) {
        Optional.ofNullable(imageStrips.get(name)).ifPresent(imageStrip -> imageStrips.remove(name));
    }

    private static com.ait.lienzo.client.core.image.ImageStrip convert(final ImageStrip strip) {
        return new com.ait.lienzo.client.core.image.ImageStrip(ImageStripRegistry.getName(strip),
                                                               strip.getImage().getSafeUri().asString(),
                                                               strip.getWide(),
                                                               strip.getHigh(),
                                                               strip.getPadding(),
                                                               convert(strip.getOrientation()));
    }

    private static com.ait.lienzo.client.core.image.ImageStrip.Orientation convert(final ImageStrip.Orientation orientation) {
        return ImageStrip.Orientation.HORIZONTAL.equals(orientation) ?
                com.ait.lienzo.client.core.image.ImageStrip.Orientation.HORIZONTAL :
                com.ait.lienzo.client.core.image.ImageStrip.Orientation.VERTICAL;
    }

    protected static void setImageStrips(ImageStrips imageStrips) {
        LienzoImageStripLoader.imageStrips = imageStrips;
    }

    Set<String> getRegistered() {
        return registered;
    }
}