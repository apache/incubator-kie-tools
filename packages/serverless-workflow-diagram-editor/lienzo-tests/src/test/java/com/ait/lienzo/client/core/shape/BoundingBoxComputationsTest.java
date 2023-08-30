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


package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundingBoxComputationsTest {

    private static final String[] PATHS = new String[]{"M352,5779.724c499,2-3-89,496-84",
            "M478,2877.472c247,3-255-88,244-83",
            "M478,2392.817c247,3-246,14,244-83",
            "M478,3361.031c247-69-246,14,244-83",
            "M500.5,1403.542c273-45-220,38,199-37",
            "M500.5,2587.469c215,96-220,38,199-37",
            "M406,1823.559c404,146-31,88,388,13",
            "M406,2064.553c86,150-31,88,388,13",
            "M406,4966.489c261,149-31,88,388,13",
            "M406,4484.496c382,141-31,88,388,13",
            "M394.5,6471.436c405,75-8,22,411-53",
            "M394.5,5279.895c449-4-8,22,411-53",
            "M394.5,6247.762c449-4,0,0,411-53",
            "M489.5,3565.342c449-4,0,0,221-8",
            "M479.072,3809.049c529-26,0,0,167-6",
            "M516.5,1644.744c352-80,0,0,167-6",
            "M798.002,4082.546c-529,7-523-141.999-12-38.999",
            "M801.469,6774.896c-550.999-16-522.999-142-1-140",
            "M799.765,4354.818c-542,32-523-142-1-140",
            "M770.439,7017.016c-380.001-148-523.001-142-1-140",
            "M769.988,274.009C390.988,266.009,247.988,272.009,769.988,274.009",
            "M794.813,1141.56C277.813,1150.56,272.813,1139.56,794.813,1141.56",
            "M785.217,901.998C320.216,900.998,263.217,899.998,785.217,901.998",
            "M347.5,462.172c505-17-17-18,505-16",
            "M339.5,663.235c521,0-1-1,521,1",
            "M377.5,5495.454c445,0-77-1,445,1",
            "M377.5,5978.951c372,1-77-1,445,1",
            "M564.756,3078.079c372,1-77-1-77-1",
            "M602.874,4750.803c196.999,91-77-1-77-1",
            "M731.169,7500.317c-551-16-74.463,21-1-140",
            "M731.169,7209.412c-551-4.777-74.463,6.271-1-41.811",
            "M1075.169,7682.533c-1996.043-2.379-269.747,3.121-3.622-20.811",
            "M612.657,8228.399c-57.988-464.354,2.249,314.834-1-140",
            "M748.791,9246.203c-48.561-445.45,0,0,0-246.418",
            "M407.051,9488.014c8.771-403.029,0,0,0-246.416",
            "M492,7954.201c258.471-128.029-126-68.002,216-56",
            "M529.006,8937.162c232.271-130.145,105.271-167.076,123.23-0.48",
            "M532.461,8453.83c213.563-131.8,105.271-167.076,123.232-0.479",
            "M538.385,8695.068c169.485-128.53,105.27-167.075,123.23-0.479",
            "M177.365,9657.754 C2314.484,9433.758-164.635,9645.75,177.365,9657.754",
            "M177.365,9863.064 C2314.484,9798.949-164.635,9859.629,177.365,9863.064",
            "M177.365,10098.412 C2314.482,10062.607-164.635,10096.492,177.365,10098.412",
            "M121.108,10352.402c2137.118-35.807,82,41-10.136-40.705",
            "M163.396,10885.141 C453.589,10902.539,162.471,10562.539,163.396,10885.141",
            "M228.071,10627.99 C1915.305,10622.137,202.129,10387.359,228.071,10627.99",
            "M152.222,10991.809 C1084.453,10959.602,1582.453,10868.578,152.222,10991.809"};

    private static final BoundingBox[] BOXES = new BoundingBox[]{
            BoundingBox.fromDoubles(352.0d, 5695.525214290144d, 848.0d, 5779.75649697214d),
            BoundingBox.fromDoubles(478.0d, 2794.273195153579d, 722.0d, 2877.5445998602727d),
            BoundingBox.fromDoubles(478.0d, 2309.817d, 722.0d, 2394.7567572309213d),
            BoundingBox.fromDoubles(478.0d, 3278.031d, 722.0d, 3361.031d),
            BoundingBox.fromDoubles(500.5d, 1366.542d, 699.5d, 1403.542d),
            BoundingBox.fromDoubles(500.5d, 2550.469d, 699.5d, 2637.7252606205066d),
            BoundingBox.fromDoubles(406.0d, 1823.559d, 794.0d, 1913.769823046537d),
            BoundingBox.fromDoubles(406.0d, 2064.553d, 794.0d, 2156.3984198731023d),
            BoundingBox.fromDoubles(406.0d, 4966.489d, 794.0d, 5057.925091345454d),
            BoundingBox.fromDoubles(406.0d, 4484.496d, 794.0d, 4572.6742567331d),
            BoundingBox.fromDoubles(394.5d, 6418.436d, 805.5d, 6507.712196270606d),
            BoundingBox.fromDoubles(394.5d, 5226.895d, 805.5d, 5281.143285123892d),
            BoundingBox.fromDoubles(394.5d, 6194.762d, 805.5d, 6247.762d),
            BoundingBox.fromDoubles(489.5d, 3557.342d, 710.5d, 3565.342d),
            BoundingBox.fromDoubles(479.072d, 3797.2570996539025d, 720.9278411706007d, 3809.049d),
            BoundingBox.fromDoubles(516.5d, 1608.96192932182d, 683.5d, 1644.744d),
            BoundingBox.fromDoubles(401.99875471767575d, 4007.140343941661d, 798.002d, 4082.786401834108d),
            BoundingBox.fromDoubles(398.5310769443325d, 6634.872447758067d, 801.469d, 6774.896d),
            BoundingBox.fromDoubles(400.2364564538901d, 4214.800883820362d, 799.765d, 4358.747837870381d),
            BoundingBox.fromDoubles(429.55950924246457d, 6876.374329763134d, 770.439d, 7017.016d),
            BoundingBox.fromDoubles(430.01132658520453d, 269.9659300453811d, 769.988d, 274.009d),
            BoundingBox.fromDoubles(405.18574425133596d, 1141.3114493458136d, 794.813d, 1145.1510667239752d),
            BoundingBox.fromDoubles(414.7835431820442d, 900.8432994616209d, 785.217d, 901.998d),
            BoundingBox.fromDoubles(347.5d, 445.42569476439667d, 852.5d, 462.172d),
            BoundingBox.fromDoubles(339.5d, 662.985d, 860.5d, 664.235d),
            BoundingBox.fromDoubles(377.5d, 5495.204d, 822.5d, 5496.454d),
            BoundingBox.fromDoubles(377.5d, 5978.951d, 822.5d, 5979.951d),
            BoundingBox.fromDoubles(487.756d, 3077.079d, 712.2431944943892d, 3078.3590000000017d),
            BoundingBox.fromDoubles(525.874d, 4749.803d, 674.125083775142d, 4790.989803305784d),
            BoundingBox.fromDoubles(468.8317408734713d, 7360.317d, 731.169d, 7500.317d),
            BoundingBox.fromDoubles(468.8317408734713d, 7167.601000000001d, 731.169d, 7209.412d),
            BoundingBox.fromDoubles(124.83097769342879d, 7661.722000000001d, 1075.169d, 7682.533d),
            BoundingBox.fromDoubles(587.3420225061054d, 8074.577776940716d, 612.657d, 8236.922345580811d),
            BoundingBox.fromDoubles(727.2083333333334d, 8999.785d, 748.791d, 9246.203d),
            BoundingBox.fromDoubles(407.051d, 9241.598d, 410.94922222222226d, 9488.014d),
            BoundingBox.fromDoubles(492.0d, 7873.678604160913d, 708.0d, 7954.201d),
            BoundingBox.fromDoubles(529.006d, 8825.205346462062d, 670.9941751455242d, 8937.162d),
            BoundingBox.fromDoubles(532.461d, 8341.292760478815d, 667.5380737049802d, 8453.83d),
            BoundingBox.fromDoubles(538.385d, 8583.676574278068d, 661.615d, 8695.068d),
            BoundingBox.fromDoubles(144.37587253876018d, 9555.478596564393d, 1055.6243695738433d, 9657.754d),
            BoundingBox.fromDoubles(144.37587253876018d, 9833.789642962289d, 1055.6243695738433d, 9863.064d),
            BoundingBox.fromDoubles(144.37584781893887d, 10082.063346207156d, 1055.6234846976138d, 10098.412d),
            BoundingBox.fromDoubles(110.97200000000001d, 10311.697d, 1089.0271176459155d, 10352.402d),
            BoundingBox.fromDoubles(163.39379917540256d, 10745.551091058838d, 292.1653566860507d, 10885.791728235246d),
            BoundingBox.fromDoubles(227.77860373680548d, 10519.730311063244d, 972.221042087081d, 10627.99d),
            BoundingBox.fromDoubles(152.222d, 10929.15672038027d, 1047.7784337683943d, 10991.809d)
    };

    @Test
    public void testCalculateBB() {
        for (int i = 0; i < PATHS.length; i++) {
            String path = PATHS[i];
            BoundingBox box = BOXES[i];
            MultiPath multiPath = new MultiPath(path);
            BoundingBox boundingBox = multiPath.getBoundingBox();
            assertEquals(box, boundingBox);
        }
    }
}
