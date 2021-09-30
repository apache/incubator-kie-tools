# DataSet API usage

This is a quick reference guide on the setup and usage of the DataSet API.

## Prerequisites

The DataSet library is available as a maven artifact. You need to import the library's BOM within your _dependencyManagement_
section as well as to add a dependency to the _dashbuilder-dataset-core_ artifact. As you can see below, the SQL, CVS, and
ElasticSearch dependencies are optional. Add them depending on what kind of datasets you want to support.

      <dependencyManagement>
        <dependencies>
          <dependency>
            <groupId>org.dashbuilder</groupId>
            <artifactId>dashbuilder-bom</artifactId>
            <version>0.5.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
          </dependency>
        </dependencies>
      </dependencyManagement>

      <dependencies>

        <dependency>
          <groupId>org.dashbuilder</groupId>
          <artifactId>dashbuilder-dataset-core</artifactId>
        </dependency>

        <!-- Optional -->
        <dependency>
          <groupId>org.dashbuilder</groupId>
          <artifactId>dashbuilder-dataset-sql</artifactId>
        </dependency>
        <dependency>
          <groupId>org.dashbuilder</groupId>
          <artifactId>dashbuilder-dataset-csv</artifactId>
        </dependency>
        <dependency>
          <groupId>org.dashbuilder</groupId>
          <artifactId>dashbuilder-dataset-elasticsearch</artifactId>
        </dependency>

      </dependencies>

## Data Set Definitions

First thing is to create a _DataSetDef_ instance. Here is an example on how to define a CSV data set:

    DataSetDef csvDef = DataSetDefFactory.newCSVDataSetDef()
        .uuid("myDataSet")
        .fileURL("http://myHost.com/myData.csv")
        .label("id")
        .label("office")
        .label("department")
        .label("author")
        .date("date", "MM-dd-yyyy")
        .number("amount", "#,###.##")
        .separatorChar(';')
        .quoteChar('\"')
        .escapeChar('\\')
        .buildDef());


Java Bean generated data sets are supported as well:

    DataSetDef beanDef = DataSetDefFactory.newBeanDataSetDef()
        .uuid("salesStats")
        .label("customer")
        .label("salesman")
        .label("expectedAmount")
        .label("probability")
        .generatorClass("org.mycompany.SalesDataSetGenerator")
        .generatorParam("startYear", "-1")
        .generatorParam("endYear", "2")
        .generatorParam("oppsPerMonth", "30")
        .buildDef();

Or even those based on SQL database queries:

    DataSetDef sqlDef = DataSetDefFactory.newSQLDataSetDef()
        .uuid("expenses")
        .dataSource("jdbc:mysql://myserver:3306/mydb")
        .dbSQL("SELECT * FROM EXPENSE_REPORT", false)
        .number("EXPENSES_ID")
        .label("CITY")
        .label("DEPARTMENT")
        .label("EMPLOYEE")
        .number("AMOUNT")
        .date("DATE")
        .buildDef();


## Data Set Registration

The _DataSetCore_ interface is the main entry point to the API. From this interface you can get access to any of the
components and services provided by the DataSet subsystem.

To register a data set definition:

    DataSetDef myDataSetDef = ... (see above section)
    DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
    dataSetDefRegistry.registerDataSetDef(myDataSetDef);

There exists an additional way to register definitions using an auto-deploy mechanism which scans a given file system
folder and registers any data set flagged as "deploy"

    DataSetDefDeployer dataSetDefDeployer = DataSetCore.get().getDataSetDefDeployer();
    dataSetDefDeployer.setScanIntervalInMillis(5000);
    dataSetDefDeployer.deploy("/home/datasets/deployments");

This is enough to get the deployment scanner up and running. Every 5 seconds, it will scan the target folder looking for ".dset" files
like the following:

    {
        "uuid": "expenseReports",
        "name": "Expense Reports",
        "provider": "CSV",
        "filePath": "expenseReports.csv",
        "separatorChar": ";",
        "quoteChar": "\"",
        "escapeChar": "\\",
        "datePattern": "MM-dd-yyyy",
        "numberPattern": "#,###.##",
        "columns": [
          {"id": "office", "type": "label"},
          {"id": "department", "type": "label"},
          {"id": "employee", "type": "label"},
          {"id": "amount", "type": "number", "pattern": "#,###.##"},
          {"id": "date", "type": "date", "pattern": "MM-dd-yyyy"}
        ]
    }

Along with the above file you also need to copy the _expenseReports.csv_ file plus an empty file named
_expenseReports.dset.deploy_. This last file serves to trigger the auto-deploy of the data set definition.

Likewise, to remove any existing data set from the system you just need to copy a file named _myDataSet.dset.undeploy_
(where _myDataSet_ is the uuid of the data set to remove).

## Data Lookup Calls

Once registered, a data set definition is ready to accept data lookup calls:

    DataSet result = dataSetManager.lookupDataSet(
        DataSetFactory.newDataSetLookupBuilder()
            .dataset("expenses")
            .filter("amount", lowerThan(1000))
            .group("department")
            .column("department")
            .column(AggregateFunctionType.COUNT, "#items")
            .column("amount", AggregateFunctionType.SUM)
            .sort("amount", "desc")
            .buildLookup());

Which returns a data set with the result of applying the filter, group and sort operations specified:

        {"Engineering", "16.00", "6,547.56"}
        {"Services", "5.00", "2,504.50"}
        {"Sales", "8.00", "3,213.53"}
        {"Support", "6.00", "2,343.70"}
        {"Management", "11.00", "6,017.47"}

The lookup API works on top of any data set definition, regardless its type and data storage location.

## Data sets providers

The only data set type supported by default is the Java Bean generated. If you want to support CSV, SQL or ELasticSearch
then, aside from adding the proper maven dependency (see the Prerequisites section above), you also need
to register the target provider component:

    DataSetProviderRegistry providerRegistry = DataSetCore.get().getDataSetProviderRegistry();
    dataSetProviderRegistry.registerDataProvider(CSVDataSetProvider.get());
    dataSetProviderRegistry.registerDataProvider(SQLDataSetProvider.get());
    dataSetProviderRegistry.registerDataProvider(ElasticSearchDataSetProvider.get());
