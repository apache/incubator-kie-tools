/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ait.lienzo.client.core.image.ImageStrips;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry.getName;

@ApplicationScoped
public class LienzoImageStrips {

    private final ImageStrips imageStrips;
    private final Map<String, Integer> registered;

    public LienzoImageStrips() {
        this(ImageStrips.get());
    }

    LienzoImageStrips(final ImageStrips imageStrips) {
        this.imageStrips = imageStrips;
        this.registered = new HashMap<>();
    }

    public void register(final org.kie.workbench.common.stunner.core.client.shape.ImageStrip[] strips,
                         final Command callback) {

        List<org.kie.workbench.common.stunner.core.client.shape.ImageStrip> candidates = new LinkedList<>();
        for (org.kie.workbench.common.stunner.core.client.shape.ImageStrip strip : strips) {
            final String name = getName(strip);
            Integer count = registered.get(name);
            if (null == count) {
                count = 1;
                candidates.add(strip);
            } else {
                count++;
            }
            registered.put(name, count);
        }

        if (!candidates.isEmpty()) {
            imageStrips.register(candidates.stream()
                                         .map(LienzoImageStrips::convert)
                                         .toArray(com.ait.lienzo.client.core.image.ImageStrip[]::new),
                                 callback::execute);
        } else {
            callback.execute();
        }
    }

    public void remove(final org.kie.workbench.common.stunner.core.client.shape.ImageStrip[] strips) {
        for (org.kie.workbench.common.stunner.core.client.shape.ImageStrip strip : strips) {
            final String name = getName(strip);
            final Integer count = registered.get(name);
            if (count == 1) {
                removeFromLienzo(name);
                registered.remove(name);
            } else {
                registered.put(name, count - 1);
            }
        }
    }

    void removeFromLienzo(final String name) {
        Optional.ofNullable(imageStrips.get(name))
                .ifPresent(imageStrip -> imageStrips.remove(name));
    }

    @PreDestroy
    public void destroy() {
        new HashMap<>(registered).keySet().forEach(this::removeFromLienzo);
        registered.clear();
    }

    Map<String, Integer> getRegistered() {
        return registered;
    }

    private static com.ait.lienzo.client.core.image.ImageStrip convert(final org.kie.workbench.common.stunner.core.client.shape.ImageStrip strip) {
        return new com.ait.lienzo.client.core.image.ImageStrip(getName(strip),
                                                               strip.getImage().getSrc(),
                                                               strip.getWide(),
                                                               strip.getHigh(),
                                                               strip.getPadding(),
                                                               convert(strip.getOrientation()));
    }

    private static com.ait.lienzo.client.core.image.ImageStrip.Orientation convert(final org.kie.workbench.common.stunner.core.client.shape.ImageStrip.Orientation orientation) {
        return org.kie.workbench.common.stunner.core.client.shape.ImageStrip.Orientation.HORIZONTAL.equals(orientation) ?
                com.ait.lienzo.client.core.image.ImageStrip.Orientation.HORIZONTAL :
                com.ait.lienzo.client.core.image.ImageStrip.Orientation.VERTICAL;
    }
}
