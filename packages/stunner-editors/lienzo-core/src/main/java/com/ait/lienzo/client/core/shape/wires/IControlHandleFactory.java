/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires;

import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;

public interface IControlHandleFactory {

    Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types);

    Map<ControlHandleType, IControlHandleList> getControlHandles(List<ControlHandleType> types);
}
