/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.preferences.shared.bean.mock;

import javax.enterprise.inject.Vetoed;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.impl.validation.NotEmptyValidator;

/**
 * Created to be used in tests, and to avoid Errai errors due to unimplemented
 * interfaces used in portable classes.
 */
@Vetoed
public class PortablePreferenceMock implements BasePreference<PortablePreferenceMock> {

    @Property(validators = NotEmptyValidator.class)
    String property;
}
