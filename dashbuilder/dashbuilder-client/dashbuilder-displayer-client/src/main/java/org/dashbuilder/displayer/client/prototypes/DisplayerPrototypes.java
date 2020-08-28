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
package org.dashbuilder.displayer.client.prototypes;

import java.util.EnumMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.MapColorScheme;

import static org.dashbuilder.displayer.client.prototypes.DataSetPrototypes.*;

@ApplicationScoped
public class DisplayerPrototypes {

    protected DataSetPrototypes dataSetPrototypes;

    protected UUIDGenerator uuidGenerator;

    protected Map<DisplayerType, DisplayerSettings> prototypeMap = new EnumMap<>(DisplayerType.class);
    protected Map<DisplayerSubType, DisplayerSettings> subprotoMap = new EnumMap<>(DisplayerSubType.class);

    public DisplayerPrototypes() {
    }

    @Inject
    public DisplayerPrototypes(DataSetPrototypes dataSetPrototypes, UUIDGenerator uuidGenerator) {
        this.dataSetPrototypes = dataSetPrototypes;
        this.uuidGenerator = uuidGenerator;
        init();
    }

    public void init() {
        prototypeMap.put(DisplayerType.PIECHART, DisplayerSettingsFactory
                .newPieChartSettings()
                .uuid("pieChartPrototype")
                .dataset(dataSetPrototypes.getContinentPopulation())
                .title("Population per Continent")
                .titleVisible(false)
                .column(POPULATION)
                .expression("value/1000000")
                .format("Population", "#,### M")
                .width(500).height(300)
                .margins(10, 10, 10, 100)
                .legendOn("right")
                .set3d(true)
                .filterOn(false, true, true)
                .buildSettings());

        prototypeMap.put(DisplayerType.BARCHART, DisplayerSettingsFactory
                .newBarChartSettings()
                .subType_Bar()
                .uuid("barChartPrototype")
                .dataset(dataSetPrototypes.getTopRichCountries())
                .title("Top Rich Countries")
                .titleVisible(false)
                .column(COUNTRY).format("Country")
                .column(GDP_2013).format("2013", "$ #,### M")
                .column(GDP_2014).format("2014", "$ #,### M")
                .width(500).height(250)
                .margins(10, 40, 100, 50)
                .legendOn("right")
                .filterOn(false, true, true)
                .buildSettings());

        prototypeMap.put(DisplayerType.LINECHART, DisplayerSettingsFactory
                .newLineChartSettings()
                .uuid("lineChartPrototype")
                .dataset(dataSetPrototypes.getContinentPopulation())
                .title("Population per Continent")
                .titleVisible(false)
                .column(POPULATION)
                .expression("value/1000000")
                .format("Population", "#,### M")
                .width(500).height(300)
                .margins(10, 40, 90, 10)
                .legendOff()
                .filterOn(false, true, true)
                .buildSettings());

        prototypeMap.put(DisplayerType.AREACHART, DisplayerSettingsFactory
                .newAreaChartSettings()
                .uuid("areaChartPrototype")
                .dataset(dataSetPrototypes.getContinentPopulation())
                .title("Population per Continent")
                .titleVisible(false)
                .column(POPULATION)
                .expression("value/1000000")
                .format("Population", "#,### M")
                .width(500).height(300)
                .margins(10, 40, 90, 10)
                .legendOff()
                .filterOn(false, true, true)
                .buildSettings());

        prototypeMap.put(DisplayerType.BUBBLECHART, DisplayerSettingsFactory
                .newBubbleChartSettings()
                .uuid("bubbleChartPrototype")
                .dataset(dataSetPrototypes.getContinentPopulationExt())
                .title("Population per Continent")
                .titleVisible(false)
                .width(500).height(300)
                .margins(10, 30, 50, 10)
                .legendOff()
                .filterOn(false, true, true)
                .buildSettings());

        prototypeMap.put(DisplayerType.METERCHART, DisplayerSettingsFactory
                .newMeterChartSettings()
                .uuid("meterChartPrototype")
                .dataset(dataSetPrototypes.getContinentPopulation())
                .title("Population per Continent")
                .titleVisible(false)
                .width(400).height(300)
                .column(POPULATION)
                .expression("value/1000000")
                .format("Population", "#,### M")
                .margins(10, 10, 10, 10)
                .meter(0, 1000L, 3000L, 6000L)
                .filterOn(false, true, true)
                .buildSettings());

        DisplayerSettings metricCard = DisplayerSettingsFactory
                .newMetricSettings()
                .uuid("metricCardPrototype")
                .dataset(dataSetPrototypes.getTotalPopulation())
                .column(POPULATION).format("Population", "#,##0 MM")
                .subtype(DisplayerSubType.METRIC_CARD)
                .title("World population")
                .titleVisible(true)
                .width(200).height(100)
                .margins(0, 0, 0, 0)
                .backgroundColor("white")
                .filterOn(false, false, true)
                .htmlTemplate("<div id=\"${this}\" class=\"card-pf card-pf-accented card-pf-aggregate-status\" " +
                        "style=\"background-color:${bgColor}; width:${width}px; height:${height}px; " +
                        "margin-top:${marginTop}px; margin-right:${marginRight}px; margin-bottom:${marginBottom}px; margin-left:${marginLeft}px;\">\n" +
                        "  <h3>${title}</h3>\n" +
                        "  <h2>${value}</h2>\n" +
                        "</div>")
                .jsTemplate("if (${isFilterEnabled}) {  \n" +
                        "  var filterOn = false;\n" +
                        "  ${this}.style.cursor=\"pointer\";\n" +
                        "\n" +
                        "  ${this}.onmouseover = function() {\n" +
                        "    if (!filterOn) ${this}.style.backgroundColor = \"lightblue\";\n" +
                        "  };\n" +
                        "  ${this}.onmouseout = function() {\n" +
                        "    if (!filterOn) ${this}.style.backgroundColor = \"${bgColor}\";\n" +
                        "  };\n" +
                        "  ${this}.onclick = function() {\n" +
                        "    filterOn = !filterOn;\n" +
                        "    ${this}.style.backgroundColor = filterOn ? \"lightblue\" : \"${bgColor}\";\n" +
                        "    ${doFilter};\n" +
                        "  };\n" +
                        "}")
                .buildSettings();

        DisplayerSettings metricCard2 = DisplayerSettingsFactory
                .newMetricSettings()
                .uuid("metricCard2Prototype")
                .dataset(dataSetPrototypes.getTotalPopulation())
                .column(POPULATION).format("Population", "#,##0 MM")
                .subtype(DisplayerSubType.METRIC_CARD2)
                .title("World population")
                .titleVisible(true)
                .width(200).height(100)
                .margins(0, 0, 0, 0)
                .backgroundColor("white")
                .filterOn(false, false, true)
                .htmlTemplate("<h2 class=\"card-pf-title\">\n" +
                        "    ${title}\n" +
                        "  </h2>\n" +
                        "<div class=\"card-pf-body\">\n" +
                        "   <p class=\"card-pf-utilization-details\">\n" +
                        "      <span class=\"card-pf-utilization-card-details-count\">${value}</span>\n" +
                        "        <span class=\"card-pf-utilization-card-details-description\">\n" +
                        "          <span class=\"card-pf-utilization-card-details-line-1\">people</span>\n" +
                        "          <span class=\"card-pf-utilization-card-details-line-2\">in the world</span>\n" +
                        "        </span>\n" +
                        "   </p>\n" +
                        "</div>")
                .buildSettings();

        DisplayerSettings metricQuota = DisplayerSettingsFactory
                .newMetricSettings()
                .uuid("metricQuotaPrototype")
                .dataset(dataSetPrototypes.getTotalPopulation())
                .column(POPULATION).format("Population", "#,##0 MM")
                .subtype(DisplayerSubType.METRIC_QUOTA)
                .title("World population")
                .titleVisible(true)
                .width(200).height(100)
                .margins(0, 0, 0, 0)
                .metric(0, 0, 0, 100000)
                .filterOn(false, false, true)
                .htmlTemplate("<div class=\"progress-description\">\n" +
                        "   ${title}\n" +
                        "</div>\n" +
                        "<div class=\"progress progress-label-top-right\">\n" +
                        "  <div id=\"${bar}\" class=\"progress-bar\" role=\"progressbar\" data-toggle=\"tooltip\" title=\"${value}\">\n" +
                        "    <span><strong>${value}</strong></span>\n" +
                        "  </div>\n" +
                        "  <div class=\"progress-bar progress-bar-remaining\" role=\"progressbar\" data-toggle=\"tooltip\">\n" +
                        "  </div>\n" +
                        "</div>")
                .jsTemplate("var end = ${value.end};\n" +
                        "var current = Math.round(${value.raw} * 100 / end);\n" +
                        "${bar}.style.width = current + \"%\";")
                .buildSettings();

        DisplayerSettings metricPlainText = DisplayerSettingsFactory
                .newMetricSettings()
                .uuid("metricPlainTextPrototype")
                .dataset(dataSetPrototypes.getTotalPopulation())
                .column(POPULATION).format("Population", "#,##0 MM")
                .subtype(DisplayerSubType.METRIC_PLAIN_TEXT)
                .title("World population ")
                .titleVisible(true)
                .width(200).height(100)
                .margins(0, 0, 0, 0)
                .metric(0, 50000, 75000, 100000)
                .filterOn(false, false, true)
                .htmlTemplate("<div>\n" +
                        "  ${title}: <span id=\"${valref}\" style=\"font-weight: bold\">${value}</span>\n" +
                        "</div>\n")
                .jsTemplate("if (${value.raw} > ${value.critical}) {  \n" +
                        "    ${valref}.style.color = \"red\"; \n" +
                        "} else if (${value.raw} > ${value.warning}) {  \n" +
                        "    ${valref}.style.color = \"yellow\"; \n" +
                        "} else {\n" +
                        "    ${valref}.style.color = \"black\";\n" +
                        "}")
                .buildSettings();

        prototypeMap.put(DisplayerType.METRIC, metricCard);
        subprotoMap.put(DisplayerSubType.METRIC_CARD, metricCard);
        subprotoMap.put(DisplayerSubType.METRIC_CARD2, metricCard2);
        subprotoMap.put(DisplayerSubType.METRIC_QUOTA, metricQuota);
        subprotoMap.put(DisplayerSubType.METRIC_PLAIN_TEXT, metricPlainText);

        prototypeMap.put(DisplayerType.MAP, DisplayerSettingsFactory
                .newMapChartSettings()
                .uuid("mapChartPrototype")
                .dataset(dataSetPrototypes.getCountryPopulation())
                .title("World Population")
                .titleVisible(false)
                .width(500).height(300)
                .margins(10, 10, 10, 10)
                .filterOn(false, true, true)
                .colorScheme(MapColorScheme.GREEN)
                .buildSettings());

        prototypeMap.put(DisplayerType.TABLE, DisplayerSettingsFactory
                .newTableSettings()
                .uuid("tablePrototype")
                .dataset(dataSetPrototypes.getWorldPopulation())
                .title("Population per Continent")
                .titleVisible(false)
                .column(POPULATION)
                .expression("value/1000000")
                .format("Population", "#,### M")
                .tableOrderEnabled(true)
                .tableOrderDefault(POPULATION, SortOrder.DESCENDING)
                .tablePageSize(10)
                .filterOn(false, true, true)
                .buildSettings());


        prototypeMap.put(DisplayerType.EXTERNAL_COMPONENT, DisplayerSettingsFactory
                         .newExternalDisplayerSettings()
                         .uuid("externalComponentPrototype")
                         .dataset(dataSetPrototypes.getTopRichCountries())
                         .column(COUNTRY).format("Country")
                         .column(GDP_2014)
                         .column(GDP_2013)
                         .buildSettings());
        
        DisplayerSettings selectorDropDown = DisplayerSettingsFactory
                .newSelectorSettings()
                .uuid("selectorDropDownPrototype")
                .dataset(dataSetPrototypes.getTopRichCountries())
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
                .title("Country selector").titleVisible(true)
                .column(COUNTRY).format("Country")
                .width(200)
                .multiple(true)
                .filterOn(false, true, false)
                .buildSettings();

        DisplayerSettings selectorLabels = DisplayerSettingsFactory
                .newSelectorSettings()
                .uuid("selectorLabelsPrototype")
                .dataset(dataSetPrototypes.getTopRichCountries())
                .subtype(DisplayerSubType.SELECTOR_LABELS)
                .title("Country selector").titleVisible(true)
                .column(COUNTRY).format("Country")
                .width(-1)
                .multiple(true)
                .filterOn(false, true, false)
                .buildSettings();

        DisplayerSettings selectorSlider = DisplayerSettingsFactory
                .newSelectorSettings()
                .uuid("selectorSliderPrototype")
                .dataset(dataSetPrototypes.getPopulationLimits())
                .subtype(DisplayerSubType.SELECTOR_SLIDER)
                .title("World population").titleVisible(true)
                .column(POPULATION).format("Population", "#,##0 MM").expression("value/1000000")
                .margins(0, 0, 0, 0)
                .width(-1)
                .filterOn(false, true, false)
                .buildSettings();

        prototypeMap.put(DisplayerType.SELECTOR, selectorDropDown);
        subprotoMap.put(DisplayerSubType.SELECTOR_DROPDOWN, selectorDropDown);
        subprotoMap.put(DisplayerSubType.SELECTOR_LABELS, selectorLabels);
        subprotoMap.put(DisplayerSubType.SELECTOR_SLIDER, selectorSlider);
    }

    public DisplayerSettings getProto(DisplayerType type) {
        return getProto(type, null);
    }

    public DisplayerSettings getProto(DisplayerType type, DisplayerSubType subType) {
        boolean hasSubproto = subType != null && subprotoMap.containsKey(subType);
        DisplayerSettings proto = hasSubproto ? subprotoMap.get(subType) : prototypeMap.get(type);
        proto = proto.cloneInstance();
        proto.setUUID(uuidGenerator.newUuid());
        if (subType != null) {
            proto.setSubtype(subType);
        }
        return proto;
    }
}