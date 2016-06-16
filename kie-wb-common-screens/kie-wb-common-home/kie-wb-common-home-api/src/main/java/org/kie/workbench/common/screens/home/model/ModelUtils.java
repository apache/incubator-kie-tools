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

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.mvp.Command;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;

/**
 * Utility methods for creating the HomeModel.
 */
@ApplicationScoped
public class ModelUtils {

    public static CarouselEntry makeCarouselEntry( final String heading,
                                                   final String subHeading,
                                                   final String imageUri ) {
        final CarouselEntry item = new CarouselEntry( heading,
                                                      subHeading,
                                                      imageUri );
        return item;
    }

    public static SectionEntry makeSectionEntry( final String caption ) {
        return new SectionEntry( caption );
    }

    public static SectionEntry makeSectionEntry( final String caption,
                                                 final Command command ) {
        return new SectionEntry( caption, command );
    }

    public static SectionEntry makeSectionEntry(final String caption,
                                                final Command command,
                                                final String resourceId,
                                                final ResourceType resourceType ) {
        final SectionEntry entry = new SectionEntry( caption, command );
        ResourceRef resource = new ResourceRef( resourceId, resourceType );
        entry.setResource( resource );
        return entry;
    }


    public static SectionEntry makeSectionEntry(final String caption,
                                                final Command command,
                                                final String permission ) {
        final SectionEntry entry = new SectionEntry( caption, command );
        entry.setPermission( permission );
        return entry;
    }

}
