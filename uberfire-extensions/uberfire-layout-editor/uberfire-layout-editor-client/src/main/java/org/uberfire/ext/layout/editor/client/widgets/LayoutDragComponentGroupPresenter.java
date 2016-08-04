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
package org.uberfire.ext.layout.editor.client.widgets;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
public class LayoutDragComponentGroupPresenter {

    public interface View extends UberElement<LayoutDragComponentGroupPresenter> {

        void setTitle( String name );

        void addComponents( Map<String, LayoutDragComponent> components );

        void addComponent( String componentId, LayoutDragComponent component );

        void removeComponent( String componentId );
    }

    private final View view;

    private LayoutDragComponentGroup group;

    @Inject
    public LayoutDragComponentGroupPresenter( final View view ) {
        this.view = view;
        view.init( this );
    }

    public void init( LayoutDragComponentGroup group ) {
        this.group = group;
        view.setTitle( group.getName() );
        view.addComponents( group.getComponents() );
    }

    public void add( String componentId, LayoutDragComponent component ) {
        view.addComponent( componentId, component );
    }

    public void removeDraggableComponentFromGroup( String componentId ) {
        view.removeComponent( componentId );
    }

    public UberElement<LayoutDragComponentGroupPresenter> getView() {
        return view;
    }

}