/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.validation;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

import javax.lang.model.SourceVersion;

public class ValidationUtils {

    public static Boolean isJavaIdentifier(String s) {
        if (StringUtils.isBlank(s)) return false;
        if (!SourceVersion.isName(s)) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!CharUtils.isAsciiPrintable(s.charAt(i))) return false;
        }
        return true;
    }
}
