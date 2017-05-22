/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.processors.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FormGenerationUtils {

    public static void sort(String firstName,
                            List<Map<String, String>> elements) {

        if (elements == null || elements.isEmpty()) {
            return;
        }

        List<Map<String, String>> backup = new ArrayList<>(elements);

        elements.clear();

        Map<String, String> firstElement = backup.stream().filter(formElement ->
                                                                          formElement.get("elementName").equals(firstName)
        ).findFirst().orElse(backup.get(0));

        backup.remove(firstElement);

        elements.add(firstElement);

        buildChain(firstElement,
                   backup,
                   elements);

        if (!backup.isEmpty()) {
            for (Iterator<Map<String, String>> it = backup.iterator(); it.hasNext(); ) {
                Map<String, String> formElement = it.next();
                it.remove();
                if (!elements.contains(formElement)) {
                    elements.add(formElement);
                    buildChain(formElement,
                               new ArrayList<>(backup),
                               elements);
                }
            }
        }
    }

    protected static void buildChain(Map<String, String> previous,
                                     List<Map<String, String>> elements,
                                     List<Map<String, String>> originalList) {
        if (elements.isEmpty() || previous == null) {
            return;
        }

        Map<String, String>[] aux = elements.stream().filter(formElement -> previous.get("elementName").equals(formElement.get("afterElement"))).toArray(size -> new Map[size]);

        elements.removeAll(Arrays.asList(aux));

        for (Map<String, String> element : aux) {
            originalList.add(originalList.indexOf(previous) + 1,
                             element);
            buildChain(element,
                       elements,
                       originalList);
        }
    }
    /*
    public static void main( String[] args ) {
        List<Map<String, String>>
        sort( "label",  );
    }*/
}
