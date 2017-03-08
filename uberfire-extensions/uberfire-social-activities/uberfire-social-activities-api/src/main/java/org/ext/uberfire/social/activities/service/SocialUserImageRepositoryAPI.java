/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.ext.uberfire.social.activities.service;

import org.ext.uberfire.social.activities.model.SocialUser;

public interface SocialUserImageRepositoryAPI {

    /**
     * Return the url containing either the location of the image or base 64 encoded image.
     * @param user
     * @param imageSize
     * @return
     */
    String imageUrlForSocialUser(SocialUser user,
                                 ImageSize imageSize);

    enum ImageSize {
        SMALL,
        BIG,
        MICRO
    }
}
