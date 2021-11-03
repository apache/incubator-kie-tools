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
package org.dashbuilder.navigation.layout;

public interface LayoutRecursionIssueI18n {

    String navRefPerspectiveI18n(String name);

    String navRefPerspectiveInGroupI18n(String name);

    String navRefPerspectiveDefaultI18n(String name);

    String navRefPerspectiveFoundI18n(String name);

    String navRefGroupDefinedI18n(String name);

    String navRefGroupContextI18n(String name);

    String navRefComponentI18n(String name);

    String navRefDefaultItemDefinedI18n(String name);

    String navRefDefaultItemFoundI18n(String name);

    String navRefPerspectiveRecursionEndI18n();

    String navCarouselDragComponentI18n();

    String navTabListDragComponentI18n();

    String navTilesDragComponentI18n();

    String navTreeDragComponentI18n();

    String navMenubarDragComponentI18n();
}
