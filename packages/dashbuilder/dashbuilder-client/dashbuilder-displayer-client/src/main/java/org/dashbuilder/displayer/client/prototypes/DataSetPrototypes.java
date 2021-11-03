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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.group.AggregateFunctionType;

@ApplicationScoped
public class DataSetPrototypes {

    public static final String CITY = "City / Urban area";
    public static final String COUNTRY = "Country";
    public static final String GDP_2014 = "GDP 2014";
    public static final String GDP_2013 = "GDP 2013";
    public static final String CONTINENT = "Continent";
    public static final String AREA_SIZE = "Area size (km2)";
    public static final String DENSITY = "Density (people / km2)";
    public static final String REGION = "Region";
    public static final String POPULATION = "Population";

    ClientDataSetManager dataSetManager;

    public DataSet getWorldPopulation() {
        return dataSetManager.getDataSet("worldPopulation");
    }

    public DataSet getTotalPopulation() {
        return dataSetManager.getDataSet("totalPopulation");
    }

    public DataSet getCountryPopulation() {
        return dataSetManager.getDataSet("countryPopulation");
    }

    public DataSet getTopRichCountries() {
        return dataSetManager.getDataSet("topRichCountries");
    }

    public DataSet getContinentPopulation() {
        return dataSetManager.getDataSet("continentPopulation");
    }

    public DataSet getPopulationLimits() {
        return dataSetManager.getDataSet("populationLimits");
    }

    public DataSet getContinentPopulationExt() {
        return dataSetManager.getDataSet("continentPopulationExt");
    }

    public DataSetPrototypes() {
    }

    @Inject
    public DataSetPrototypes(ClientDataSetManager dataSetManager) {
        this.dataSetManager = dataSetManager;
        init();
    }

    public void init() {

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("topRichCountries")
                .label(COUNTRY)
                .number(GDP_2013)
                .number(GDP_2014)
                .row("United States", 16768100L, 17418925L)
                .row("China", 9240270L, 1038080L)
                .row("Japan", 4919563L, 4616335L)
                .row("Germany", 3730261L, 3859547L)
                .row("United Kingdom", 2678455L, 2945146L)
                .row("France", 2806428L, 2846889L)
                .row("Brazil", 2245673L, 2353025L)
                .buildDataSet());

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("continentPopulation")
                .label(CONTINENT)
                .number(POPULATION)
                .row("Asia", 4298723000L)
                .row("Africa", 1110635000L)
                .row("North America", 972005000L)
                .row("South America", 972005000L)
                .row("Europe", 742452000L)
                .row("Oceania", 38304000L)
                .buildDataSet());

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("populationLimits")
                .number(POPULATION)
                .number(POPULATION + "_max")
                .row(38304000L, 4298723000L)
                .buildDataSet());

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("totalPopulation")
                .number(POPULATION)
                .row(42987L)
                .buildDataSet());

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("continentPopulationExt")
                .label(CONTINENT)
                .number(AREA_SIZE)
                .number(DENSITY)
                .label(REGION)
                .number(POPULATION)
                .row("Asia", 43820000L, 95.0, "Asia", 4298723000L)
                .row("Africa", 30370000L, 33.7, "Africa", 1110635000L)
                .row("North America", 24490000L, 22.1, "North America", 972005000L)
                .row("South America", 17840000L, 22.0, "South America", 972005000L)
                .row("Europe", 10180000L, 72.5, "Europe", 742452000L)
                .row("Oceania", 9008500L, 3.2, "Oceania", 38304000L)
                .buildDataSet());

        dataSetManager.registerDataSet(DataSetFactory
                .newDataSetBuilder()
                .uuid("worldPopulation")
                .label(CITY)
                .label(COUNTRY)
                .number(POPULATION)
                .number(AREA_SIZE)
                .number(DENSITY)
                .row("Tokyo/Yokohama", "Japan", 33200000L, 6993L, 4750L)
                .row("New York Metro", "USA", 17800000L, 8683L, 2050L)
                .row("Sao Paulo", "Brazil", 17700000L, 1968L, 9000L)
                .row("Seoul/Incheon", "South Korea", 17500000L, 1049L, 16700L)
                .row("Mexico City", "Mexico", 17400000L, 2072L, 8400L)
                .row("Osaka/Kobe/Kyoto", "Japan", 16425000L, 2564L, 6400L)
                .row("Manila", "Philippines", 14750000L, 1399L, 10550L)
                .row("Mumbai", "India", 14350000L, 484L, 29650L)
                .row("Delhi", "India", 14300000L, 1295L, 11050L)
                .row("Jakarta", "Indonesia", 14250000L, 1360L, 10500L)
                .row("Lagos", "Nigeria", 13400000L, 738L, 18150L)
                .row("Kolkata", "India", 12700000L, 531L, 23900L)
                .row("Cairo", "Egypt", 12200000L, 1295L, 9400L)
                .row("Los Angeles", "USA", 11789000L, 4320L, 2750L)
                .row("Buenos Aires", "Argentina", 11200000L, 2266L, 4950L)
                .row("Rio de Janeiro", "Brazil", 10800000L, 1580L, 6850L)
                .row("Moscow", "Russia", 10500000L, 2150L, 4900L)
                .row("Shanghai", "China", 10000000L, 746L, 13400L)
                .row("Karachi", "Pakistan", 9800000L, 518L, 18900L)
                .row("Paris", "France", 9645000L, 2723L, 3550L)
                .row("Istanbul", "Turkey", 9000000L, 1166L, 7700L)
                .row("Nagoya", "Japan", 9000000L, 2875L, 3150L)
                .row("Beijing", "China", 8614000L, 748L, 11500L)
                .row("Chicago", "USA", 8308000L, 5498L, 1500L)
                .row("London", "UK", 8278000L, 1623L, 5100L)
                .row("Shenzhen", "China", 8000000L, 466L, 17150L)
                .row("Essen/D�sseldorf", "Germany", 7350000L, 2642L, 2800L)
                .row("Tehran", "Iran", 7250000L, 686L, 10550L)
                .row("Bogota", "Colombia", 7000000L, 518L, 13500L)
                .row("Lima", "Peru", 7000000L, 596L, 11750L)
                .row("Bangkok", "Thailand", 6500000L, 1010L, 6450L)
                .row("Johannesburg/East Rand", "South Africa", 6000000L, 2396L, 2500L)
                .row("Chennai", "India", 5950000L, 414L, 14350L)
                .row("Taipei", "Taiwan", 5700000L, 376L, 15200L)
                .row("Baghdad", "Iraq", 5500000L, 596L, 9250L)
                .row("Santiago", "Chile", 5425000L, 648L, 8400L)
                .row("Bangalore", "India", 5400000L, 534L, 10100L)
                .row("Hyderabad", "India", 5300000L, 583L, 9100L)
                .row("St Petersburg", "Russia", 5300000L, 622L, 8550L)
                .row("Philadelphia", "USA", 5149000L, 4661L, 1100L)
                .row("Lahore", "Pakistan", 5100000L, 622L, 8200L)
                .row("Kinshasa", "Congo", 5000000L, 469L, 10650L)
                .row("Miami", "USA", 4919000L, 2891L, 1700L)
                .row("Ho Chi Minh City", "Vietnam", 4900000L, 518L, 9450L)
                .row("Madrid", "Spain", 4900000L, 945L, 5200L)
                .row("Tianjin", "China", 4750000L, 453L, 10500L)
                .row("Kuala Lumpur", "Malaysia", 4400000L, 1606L, 2750L)
                .row("Toronto", "Canada", 4367000L, 1655L, 2650L)
                .row("Milan", "Italy", 4250000L, 1554L, 2750L)
                .row("Shenyang", "China", 4200000L, 453L, 9250L)
                .row("Dallas/Fort Worth", "USA", 4146000L, 3644L, 1150L)
                .row("Boston", "USA", 4032000L, 4497L, 900L)
                .row("Belo Horizonte", "Brazil", 4000000L, 868L, 4600L)
                .row("Khartoum", "Sudan", 4000000L, 583L, 6850L)
                .row("Riyadh", "Saudi Arabia", 4000000L, 1101L, 3650L)
                .row("Singapore", "Singapore", 4000000L, 479L, 8350L)
                .row("Washington", "USA", 3934000L, 2996L, 1300L)
                .row("Detroit", "USA", 3903000L, 3267L, 1200L)
                .row("Barcelona", "Spain", 3900000L, 803L, 4850L)
                .row("Houston", "USA", 3823000L, 3355L, 1150L)
                .row("Athens", "Greece", 3685000L, 684L, 5400L)
                .row("Berlin", "Germany", 3675000L, 984L, 3750L)
                .row("Sydney", "Australia", 3502000L, 1687L, 2100L)
                .row("Atlanta", "USA", 3500000L, 5083L, 700L)
                .row("Guadalajara", "Mexico", 3500000L, 596L, 5900L)
                .row("San Francisco/Oakland", "USA", 3229000L, 1365L, 2350L)
                .row("Montreal.", "Canada", 3216000L, 1740L, 1850L)
                .row("Monterey", "Mexico", 3200000L, 479L, 6700L)
                .row("Melbourne", "Australia", 3162000L, 2080L, 1500L)
                .row("Ankara", "Turkey", 3100000L, 583L, 5300L)
                .row("Recife", "Brazil", 3025000L, 376L, 8050L)
                .row("Phoenix/Mesa", "USA", 2907000L, 2069L, 1400L)
                .row("Durban", "South Africa", 2900000L, 829L, 3500L)
                .row("Porto Alegre", "Brazil", 2800000L, 583L, 4800L)
                .row("Dalian", "China", 2750000L, 389L, 7100L)
                .row("Jeddah", "Saudi Arabia", 2750000L, 777L, 3550L)
                .row("Seattle", "USA", 2712000L, 2470L, 1100L)
                .row("Cape Town", "South Africa", 2700000L, 686L, 3950L)
                .row("San Diego", "USA", 2674000L, 2026L, 1300L)
                .row("Fortaleza", "Brazil", 2650000L, 583L, 4550L)
                .row("Curitiba", "Brazil", 2500000L, 648L, 3850L)
                .row("Rome", "Italy", 2500000L, 842L, 2950L)
                .row("Naples", "Italy", 2400000L, 583L, 4100L)
                .row("Minneapolis/St. Paul", "USA", 2389000L, 2316L, 1050L)
                .row("Tel Aviv", "Israel", 2300000L, 453L, 5050L)
                .row("Birmingham", "UK", 2284000L, 600L, 3800L)
                .row("Frankfurt", "Germany", 2260000L, 984L, 2300L)
                .row("Lisbon", "Portugal", 2250000L, 881L, 2550L)
                .row("Manchester", "UK", 2245000L, 558L, 4000L)
                .row("San Juan", "Puerto Rico", 2217000L, 2309L, 950L)
                .row("Katowice", "Poland", 2200000L, 544L, 4050L)
                .row("Tashkent", "Uzbekistan", 2200000L, 531L, 4150L)
                .row("Fukuoka", "Japan", 2150000L, 544L, 3950L)
                .row("Baku/Sumqayit", "Azerbaijan", 2100000L, 544L, 3850L)
                .row("St. Louis", "USA", 2078000L, 2147L, 950L)
                .row("Baltimore", "USA", 2076000L, 1768L, 1150L)
                .row("Sapporo", "Japan", 2075000L, 414L, 5000L)
                .row("Tampa/St. Petersburg", "USA", 2062000L, 2078L, 1000L)
                .row("Taichung", "Taiwan", 2000000L, 510L, 3900L)
                .row("Warsaw", "Poland", 2000000L, 466L, 4300L)
                .row("Denver", "USA", 1985000L, 1292L, 1550L)
                .row("Cologne/Bonn", "Germany", 1960000L, 816L, 2400L)
                .row("Hamburg", "Germany", 1925000L, 829L, 2300L)
                .row("Dubai", "UAE", 1900000L, 712L, 2650L)
                .row("Pretoria", "South Africa", 1850000L, 673L, 2750L)
                .row("Vancouver", "Canada", 1830000L, 1120L, 1650L)
                .row("Beirut", "Lebanon", 1800000L, 648L, 2800L)
                .row("Budapest", "Hungary", 1800000L, 702L, 2550L)
                .row("Cleveland", "USA", 1787000L, 1676L, 1050L)
                .row("Pittsburgh", "USA", 1753000L, 2208L, 800L)
                .row("Campinas", "Brazil", 1750000L, 492L, 3550L)
                .row("Harare", "Zimbabwe", 1750000L, 712L, 2450L)
                .row("Brasilia", "Brazil", 1625000L, 583L, 2800L)
                .row("Kuwait", "Kuwait", 1600000L, 544L, 2950L)
                .row("Munich", "Germany", 1600000L, 518L, 3100L)
                .row("Portland", "USA", 1583000L, 1228L, 1300L)
                .row("Brussels", "Belgium", 1570000L, 712L, 2200L)
                .row("Vienna", "Austria", 1550000L, 453L, 3400L)
                .row("San Jose", "USA", 1538000L, 674L, 2300L)
                .row("Damman", "Saudi Arabia", 1525000L, 673L, 2250L)
                .row("Copenhagen", "Denmark", 1525000L, 816L, 1850L)
                .row("Brisbane", "Australia", 1508000L, 1603L, 950L)
                .row("Riverside/San Bernardino", "USA", 1507000L, 1136L, 1350L)
                .row("Cincinnati", "USA", 1503000L, 1740L, 850L)
                .row("Accra", "Ghana", 1500000L, 453L, 3300L)
                .buildDataSet());

        DataSet result = dataSetManager.lookupDataSet(
                DataSetFactory.newDataSetLookupBuilder()
                        .dataset("worldPopulation")
                        .group(COUNTRY)
                        .column(COUNTRY)
                        .column(POPULATION, AggregateFunctionType.SUM)
                        .column(AREA_SIZE, AggregateFunctionType.SUM)
                        .buildLookup());

        result.setUUID("countryPopulation");
        dataSetManager.registerDataSet(result);
    }
}
