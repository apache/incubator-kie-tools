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
package org.dashbuilder.backend;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetGenerator;

import static org.dashbuilder.shared.sales.SalesConstants.*;

/**
 * Generates a random data set containing sales opportunity records.
 */
@ApplicationScoped
public class SalesDataSetGenerator implements DataSetGenerator {

    private static String[] DIC_PIPELINE = {"EARLY", "STANDBY", "ADVANCED"};

    private static String[] DIC_STATUS = {"CONTACTED", "STANDBY", "DEMO", "SHORT LISTED",
                                            "LOST", "WIN", "VERBAL COMMITMENT", "QUALIFIED"};

    private static String[] DIC_COUNTRIES = {"United States", "China", "Japan", "Germany", "France", "United Kingdom",
                                            "Brazil", "Italy", "India", "Canada", "Russia", "Spain", "Australia",
                                            "Mexico", "South Korea", "Netherlands", "Turkey", "Indonesia", "Switzerland",
                                            "Poland", "Belgium", "Sweden", "Saudi Arabia", "Norway"};

    private static String[] DIC_PRODUCT = {"PRODUCT 1", "PRODUCT 2", "PRODUCT 3", "PRODUCT 4", "PRODUCT 5", "PRODUCT 6",
                                            "PRODUCT 7", "PRODUCT 8", "PRODUCT 8", "PRODUCT 10", "PRODUCT 11"};

    private static String[] DIC_SALES_PERSON = {"Roxie Foraker", "Jamie Gilbeau", "Nita Marling", "Darryl Innes",
                                                "Julio Burdge", "Neva Hunger", "Kathrine Janas", "Jerri Preble"};

    private static String[] DIC_CUSTOMER = {"Company 1", "Company 2", "Company 3", "Company 3", "Company 4",
                                            "Company 5", "Company 6", "Company 7", "Company 8", "Company 9"};

    private static String[] DIC_SOURCE = {"Customer", "Reference", "Personal contact", "Partner",
                                        "Website", "Lead generation", "Event"};

    private static double MAX_AMOUNT = 15000;

    private static double MIN_AMOUNT = 8000;

    private static double AVG_CLOSING_DAYS = 90;

    private Random random = new Random(System.currentTimeMillis());

    public DataSet buildDataSet(Map<String,String> params) {
        int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
        int startYear = currentYear + parseParam(params.get("startYear"), -2);
        int endYear = currentYear + parseParam(params.get("endYear"), 2);
        int opportunitiesPerMonth = parseParam(params.get("oppsPerMonth"), 30);

        DataSetBuilder builder = DataSetFactory.newDataSetBuilder()
                .number(AMOUNT)
                .date(CREATION_DATE)
                .date(CLOSING_DATE)
                .label(PIPELINE)
                .label(STATUS)
                .label(CUSTOMER)
                .label(COUNTRY)
                .label(PRODUCT)
                .label(SALES_PERSON)
                .number(PROBABILITY)
                .label(SOURCE)
                .number(EXPECTED_AMOUNT)
                .label(COLOR);

        for (int year = startYear; year <= endYear; year++) {
            for (int month = 0; month < 12; month++) {
                for (int i = 0; i < opportunitiesPerMonth; i++) {

                    double amount = MIN_AMOUNT + random.nextDouble() * (MAX_AMOUNT - MIN_AMOUNT);
                    double probability = random.nextDouble() * 100.0;
                    Date creationDate = buildDate(month, year);
                    String color = "GREEN";
                    if (probability < 25) color = "RED";
                    else if (probability < 50) color = "GREY";
                    else if (probability < 75) color = "YELLOW";

                    builder.row(amount,
                            creationDate,
                            addDates(creationDate, (int) (AVG_CLOSING_DAYS + random.nextDouble() * AVG_CLOSING_DAYS * 0.5)),
                            randomValue(DIC_PIPELINE),
                            randomValue(DIC_STATUS),
                            randomValue(DIC_CUSTOMER),
                            randomValue(DIC_COUNTRIES),
                            randomValue(DIC_PRODUCT),
                            randomValue(DIC_SALES_PERSON),
                            probability,
                            randomValue(DIC_SOURCE),
                            amount * (1 + (random.nextDouble() * ((month*i)%10)/10)),
                            color);
                }
            }
        }
        return builder.buildDataSet();
    }

    protected int parseParam(String param, int defaultValue) {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Date buildDate(int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, random.nextInt(28)); // No sales on 29, 30 and 31 ;-)
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // Some genius thought that the first month is 0
        c.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
        c.set(Calendar.MINUTE, random.nextInt(60));
        return c.getTime();
    }

    private Date addDates(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    private String randomValue(String[] dic) {
        return dic[random.nextInt(dic.length)];
    }
}
