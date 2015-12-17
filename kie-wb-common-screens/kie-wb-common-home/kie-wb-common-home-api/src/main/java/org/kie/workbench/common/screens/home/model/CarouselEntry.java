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

import org.uberfire.commons.validation.PortablePreconditions;

/**
 * An Entry in the Carousel
 */
public class CarouselEntry {

    private final String heading;
    private final String subHeading;
    private final String imageUrl;

    public CarouselEntry( final String heading,
                          final String subHeading,
                          final String imageUrl ) {
        this.heading = PortablePreconditions.checkNotNull( "heading",
                                                           heading );
        this.subHeading = PortablePreconditions.checkNotNull( "subHeading",
                                                              subHeading );
        this.imageUrl = PortablePreconditions.checkNotNull( "imageUrl",
                                                            imageUrl );
    }

    public String getHeading() {
        return heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
