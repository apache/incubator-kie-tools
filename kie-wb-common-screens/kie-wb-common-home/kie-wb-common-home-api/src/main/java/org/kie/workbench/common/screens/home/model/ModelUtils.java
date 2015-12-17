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

package org.kie.workbench.common.screens.home.model;

import java.util.Collection;

import org.uberfire.mvp.Command;

/**
 * Utility methods for creating the HomeModel.
 */
public class ModelUtils {

    public static CarouselEntry makeCarouselEntry( final String heading,
                                                   final String subHeading,
                                                   final String imageUri ) {
        final CarouselEntry item = new CarouselEntry( heading,
                                                      subHeading,
                                                      imageUri );
        return item;
    }

    public static SectionEntry makeSectionEntry( final String caption,
                                                 final Command command ) {
        final SectionEntry entry = new SectionEntry( caption,
                                                     command );
        return entry;
    }

    public static SectionEntry makeSectionEntry( final String caption,
                                                 final Command command,
                                                 final Collection<String> roles ) {
        final SectionEntry entry = new SectionEntry( caption,
                                                     command );
        entry.setRoles( roles );
        return entry;
    }

}
