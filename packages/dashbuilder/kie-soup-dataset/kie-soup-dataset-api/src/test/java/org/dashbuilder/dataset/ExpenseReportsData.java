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
package org.dashbuilder.dataset;

import java.util.Date;

public class ExpenseReportsData extends RawDataSet {

    public static final String COLUMN_ID = "EXPENSES_ID";
    public static final String COLUMN_CITY = "CITY";
    public static final String COLUMN_DEPARTMENT = "DEPARTMENT";
    public static final String COLUMN_EMPLOYEE = "EMPLOYEE";
    public static final String COLUMN_DATE = "CREATION_DATE";
    public static final String COLUMN_AMOUNT = "AMOUNT";

    public static final ExpenseReportsData INSTANCE = new ExpenseReportsData(
            new String[] {COLUMN_ID, COLUMN_CITY, COLUMN_DEPARTMENT, COLUMN_EMPLOYEE, COLUMN_DATE, COLUMN_AMOUNT},
            new Class[] {Integer.class, String.class, String.class, String.class, Date.class, Double.class}, new String[][] {
            {"1","Barcelona","Engineering","Roxie Foraker","12/11/15 12:00","120.35"},
            {"2","Barcelona","Engineering","Roxie Foraker","12/1/15 12:00","1100.1"},
            {"3","Barcelona","Engineering","Roxie Foraker","11/1/15 12:00","900.1"},
            {"4","Barcelona","Services","Jamie Gilbeau","10/12/15 12:00","340.34"},
            {"5","Barcelona","Services","Jamie Gilbeau","09/15/15 12:00","300"},
            {"6","Barcelona","Services","Jamie Gilbeau","08/17/15 12:00","152.25"},
            {"7","Madrid","Services","Roxie Foraker","07/1/15 12:00","800.8"},
            {"8","Madrid","Services","Roxie Foraker","06/1/15 12:00","911.11"},
            {"9","Madrid","Sales","Nita Marling","05/11/15 12:00","75.75"},
            {"10","Madrid","Sales","Nita Marling","03/11/15 12:00","100"},
            {"11","Madrid","Sales","Nita Marling","03/16/15 12:00","220.8"},
            {"12","Madrid","Sales","Nita Marling","03/2/15 12:00","344.9"},
            {"13","Brno","Support","Darryl Innes","02/9/15 12:00","567.89"},
            {"14","Brno","Support","Darryl Innes","01/13/15 12:00","400.4"},
            {"15","Brno","Support","Darryl Innes","01/11/15 12:00","1001.9"},
            {"16","Brno","Engineering","Julio Burdge","11/2/14 12:00","200.2"},
            {"17","Brno","Engineering","Julio Burdge","09/1/14 12:00","159.01"},
            {"18","Brno","Engineering","Julio Burdge","08/22/14 12:00","300"},
            {"19","Brno","Engineering","Julio Burdge","07/23/14 12:00","800.24"},
            {"20","Brno","Sales","Neva Hunger","06/11/14 12:00","995.3"},
            {"21","Brno","Sales","Neva Hunger","06/11/14 12:00","234.3"},
            {"22","Westford","Engineering","Kathrine Janas","05/17/14 12:00","233.49"},
            {"23","Westford","Engineering","Kathrine Janas","04/12/14 12:00","1.1"},
            {"24","Westford","Engineering","Kathrine Janas","03/13/14 12:00","1.402"},
            {"25","Westford","Engineering","Kathrine Janas","02/13/14 12:00","490.1"},
            {"26","Westford","Engineering","Kathrine Janas","02/9/14 12:00","600.34"},
            {"27","Westford","Sales","Jerri Preble","12/23/13 12:00","899.03"},
            {"28","Westford","Sales","Jerri Preble","11/30/13 12:00","343.45"},
            {"29","Westford","Management","Donald M. Stanton","10/29/13 12:00","983.03"},
            {"30","Westford","Management","Donald M. Stanton","10/11/13 12:00","43.03"},
            {"31","Raleigh","Management","Hannah B. Mackey","09/1/13 12:00","234.34"},
            {"32","Raleigh","Management","Hannah B. Mackey","07/2/13 12:00","543.45"},
            {"33","Raleigh","Management","Hannah B. Mackey","06/2/13 12:00","193.45"},
            {"34","Raleigh","Management","Loretta R. Havens","05/3/13 12:00","992.2"},
            {"35","Raleigh","Management","Loretta R. Havens","04/23/13 12:00","494.4"},
            {"36","Raleigh","Engineering","Tony L. Crawford","02/18/13 12:00","233.09"},
            {"37","Raleigh","Engineering","Tony L. Crawford","02/22/13 12:00","293.49"},
            {"38","Raleigh","Engineering","Tony L. Crawford","12/23/12 12:00","401.4"},
            {"39","Raleigh","Engineering","Tony L. Crawford","07/19/12 12:00","209.55"},
            {"40","Raleigh","Support","Eileen L. Pereira","06/12/12 12:00","300.01"},
            {"41","Raleigh","Support","Eileen L. Pereira","06/13/12 12:00","450.6"},
            {"42","Raleigh","Support","Eileen L. Pereira","06/14/12 12:00","320.9"},
            {"43","Raleigh","Support","Eileen L. Pereira","06/15/12 12:00","303.9"},
            {"44","London","Engineering","Alan P. Adamson","06/12/12 12:00","404.3"},
            {"45","London","Engineering","Alan P. Adamson","05/12/12 12:00","868.45"},
            {"46","London","Engineering","Alan P. Adamson","05/13/12 12:00","333.45"},
            {"47","London","Management","Patricia J. Behr","04/14/12 12:00","565.56"},
            {"48","London","Management","Patricia J. Behr","03/2/12 12:00","345.45"},
            {"49","London","Management","Patricia J. Behr","02/3/12 12:00","700.66"},
            {"50","London","Management","Patricia J. Behr","01/4/12 12:00","921.9"}});

    public ExpenseReportsData(String[] columnIds, Class[] types, String[][] data) {
        super(columnIds, types, data);
    }

}
