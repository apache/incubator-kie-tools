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
package org.uberfire.ext.wires.core.api.factories;

/**
 * Helper to identify the exact nature of a Shape being dragged from the Palette into a Canvas. The purpose of this is to
 * allow a single ShapeFactory to create multiple (but similar) different Shapes. For example you might have a ShapeFactory
 * that creates "Letters of the Alphabet". It is a single factory and it's "Shape" is a single letter. You could add multiple
 * PaletteShapes to the Palette, one for each letter of the alphabet, and use FactoryHelper to specify which letter has been selected.
 */
public interface FactoryHelper<T> {

    T getContext();

    void setContext( final T context );

}
