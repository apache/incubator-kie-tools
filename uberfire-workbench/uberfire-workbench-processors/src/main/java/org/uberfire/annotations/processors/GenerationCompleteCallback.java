/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.annotations.processors;

/**
 * A call-back used for testing. When javac invokes the Annotation Processor the
 * generated source code is returned to the miscfeatures via this call-back rather than
 * having javac write the generated source code to a file and compiled.
 */
interface GenerationCompleteCallback {

    /**
     * The source code has been generated.
     * 
     * @param code
     *            The generated source code.
     */
    void generationComplete(final String code);

}
