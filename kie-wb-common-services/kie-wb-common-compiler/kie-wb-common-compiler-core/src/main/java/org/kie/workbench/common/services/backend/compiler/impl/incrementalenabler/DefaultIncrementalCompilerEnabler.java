/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationContextProvider;
import org.kie.workbench.common.services.backend.compiler.impl.pomprocessor.DefaultPomEditor;
import org.kie.workbench.common.services.backend.compiler.impl.pomprocessor.PomPlaceHolder;
import org.kie.workbench.common.services.backend.compiler.impl.pomprocessor.ProcessedPoms;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

/***
 * It process all the poms found into a prj changing the build tag accordingly to the internal algo
 */
public class DefaultIncrementalCompilerEnabler implements IncrementalCompilerEnabler {

    private final String POM_NAME = "pom.xml";
    protected String FILE_URI = "file://";
    private boolean isValidConfiguration;
    private DefaultPomEditor editor;

    public DefaultIncrementalCompilerEnabler() {
        ConfigurationContextProvider confProvider = new ConfigurationContextProvider();
        isValidConfiguration = confProvider.isValid();
        if(isValidConfiguration){
            editor = new DefaultPomEditor(new HashSet<>(), confProvider);
        }
    }

    @Override
    public ProcessedPoms process(final CompilationRequest req) {
        if(!isValidConfiguration){
            return new ProcessedPoms(Boolean.FALSE, Collections.emptyList());
        }
        Path mainPom = Paths.get(URI.create(FILE_URI + req.getKieCliRequest().getWorkingDirectory() + "/" + POM_NAME));

        if (!Files.isReadable(mainPom)) {
            return new ProcessedPoms(Boolean.FALSE, Collections.emptyList());
        }

        PomPlaceHolder placeHolder = editor.readSingle(mainPom);
        Boolean isPresent = isPresent(placeHolder);   // check if the main pom is already scanned and edited
        if (placeHolder.isValid() && !isPresent) {
            List<String> pomsList = MavenUtils.searchPoms(mainPom.getParent()); // recursive NIO search in all subfolders
            boolean result = false;
            if (pomsList.size() > 0) {
                result = processFoundPoms(pomsList,
                                 req);
            }
            return new ProcessedPoms(result,
                                     pomsList);
        } else {
            return new ProcessedPoms(Boolean.FALSE,
                                     Collections.emptyList());
        }
    }

    private boolean processFoundPoms(List<String> poms,
                                  CompilationRequest request) {

        boolean result = true;
        for (String pom : poms) {
            Path tmpPom = Paths.get(URI.create(FILE_URI + pom));
            PomPlaceHolder tmpPlaceHolder = editor.readSingle(tmpPom);
            if (!isPresent(tmpPlaceHolder)) {
                result = result && editor.write(tmpPom, request);
            }
        }
        return result;
    }

    /**
     * Check if the artifact is in the hisotry
     */
    private Boolean isPresent(PomPlaceHolder placeholder) {
        return editor.getHistory().contains(placeholder);
    }

    /***
     * Return a unmodifiable history
     * @return
     */
    public Set<PomPlaceHolder> getHistory() {
        return Collections.unmodifiableSet(editor.getHistory());
    }

    public Boolean cleanHistory() {
        return editor.cleanHistory();
    }
}
