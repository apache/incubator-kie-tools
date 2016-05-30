/*
 *
 *    Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */

package com.ait.lienzo.test.translator;

import com.ait.lienzo.test.settings.Settings;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translator interceptor for stub classes.
 * 
 * It replaces the stub classes given at compile time by the Lienzo original classes.
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
public class LienzoStubTranslatorInterceptor 
        implements LienzoMockitoClassTranslator.TranslatorInterceptor, HasSettings {

    private final Map<String, String> stubs = new LinkedHashMap<String, String>();

    public LienzoStubTranslatorInterceptor() {
    }

    public boolean interceptBeforeParent( ClassPool classPool, 
                                          String name ) throws NotFoundException, CannotCompileException {
        
        // Check if the concrete class can be translated using our concrete stubbed ones with method implementations.
        if ( stubs.keySet().contains( name ) ) {

            String translationClass = stubs.get( name );

            if ( null != translationClass && translationClass.trim().length() > 0 ) {

                try {

                    CtClass ctClass = classPool.getCtClass( name );
                    
                    if ( ctClass.isFrozen() ) {
                        
                        ctClass.defrost();
                        
                    }
                    
                    classPool.getAndRename( translationClass, name );

                } catch ( NotFoundException e ) {
                    
                    throw new RuntimeException( e );
                    
                }

            }
            
        }

        // Let parent loader do additional job.
        return false;
    }

    public void useSettings( Settings settings ) {
        assert null != settings;
        this.stubs.putAll( settings.getStubs() );
    }
    
    public void interceptAfterParent( ClassPool classPool, 
                                      String name ) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
    }
    
}
