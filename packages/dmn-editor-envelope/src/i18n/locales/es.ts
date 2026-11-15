/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Destino: packages/dmn-editor-envelope/src/i18n/locales/es.ts

import { TranslatedDictionary } from "@kie-tools-core/i18n/dist/core";
import { es as es_common } from "@kie-tools/i18n-common-dictionary";
import { DmnEditorEnvelopeI18n } from "../DmnEditorEnvelopeI18n";

export const es: TranslatedDictionary<DmnEditorEnvelopeI18n> = {
  ...es_common,
  edit: "Editar",
  misc: "Varios",
  move: "Mover",
  navigate: "Navegar",
  unselect: "Deseleccionar",
  deleteSelection: "Eliminar selección",
  selectDeselectAll: "Seleccionar/Deseleccionar todo",
  createGroupWrappingSelection: "Crear un grupo que contenga la selección",
  hideFromDrd: "Ocultar del DRD",
  copyNodes: "Copiar nodos",
  cutNodes: "Cortar nodos",
  pasteNodes: "Pegar nodos",
  openClosePropertiesPanel: "Abrir/cerrar el panel de propiedades",
  toggleHierarchyHighlights: "Alternar el resaltado de jerarquía",
  selectionUp: "Mover la selección hacia arriba",
  selectionDown: "Mover la selección hacia abajo",
  selectionRight: "Mover la selección hacia la derecha",
  selectionLeft: "Mover la selección hacia la izquierda",
  selectionUpBigDistance: "Mover la selección una gran distancia hacia arriba",
  selectionDownBigDistance: "Mover la selección una gran distancia hacia abajo",
  selectionLeftBigDistance: "Mover la selección una gran distancia hacia la izquierda",
  selectionRightBigDistance: "Mover la selección una gran distancia hacia la derecha",
  focusOnSelection: "Enfocar la selección",
  resetPositionToOrigin: "Restablecer la posición al origen",
  rightMouseButton: "Botón derecho del mouse",
  holdAndDragtoPan: "Mantenga presionado y arrastre para desplazar",
  holdAndScrollToZoomInOut: "Mantenga presionado y use la rueda para acercar/alejar",
  holdAndScrollToNavigateHorizontally: "Mantenga presionado y use la rueda para desplazarse horizontalmente",
};
