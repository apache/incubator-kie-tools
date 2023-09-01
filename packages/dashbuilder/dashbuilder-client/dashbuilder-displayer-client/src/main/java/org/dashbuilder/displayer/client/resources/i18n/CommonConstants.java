/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.resources.i18n;

public class CommonConstants {

    private CommonConstants() {

    }

    public static String dataset_lookup_dataset_notfound(String dataSetUUID) {
        return "Data set " + dataSetUUID + " not found";
    }

    public static String displayerlocator_default_renderer_undeclared(String displayer) {
        return displayer + " displayer default renderer not declared.";
    }

    public static String displayerlocator_unsupported_displayer_renderer(String displayer, String rendererUuid) {
        return displayer + " displayer not supported in the " + rendererUuid + " renderer.";
    }

    public static String rendererliblocator_renderer_not_found(String renderer) {
        return renderer + " renderer not found.";
    }

    public static String renderermanager_renderer_not_available(String name) {
        return "No renderer is available for type: " + name + ".";
    }

    public static String componentConfigDefaultMessage() {
        return "Read the component configuration to find which columns it supports.";
    }

    public static String displayerviewer_displayer_not_created() {
        return "It was not possible to create the displayer";
    }

}
