SQL data provider integration tests
------------------------------------------

The *dashbuilder-dataset-sql* module contains all the SQL data provider tests. By default, those tests are executed
against an H2 in memory database. The maven modules under this directory just get all those all tests and execute them
against an specific database configuration.

The procedure for running the test battery against a given DB is always the same.

For instance, to execute the tests under Oracle:

1. Download the driver from the Oracle web site

2. *cd dashbuilder/dashbuilder-backend/dashbuilder-dataset-sql-tests/dashbuilder-dataset-sql-tests-oracle*

3. Edit *pom.xml* and change the driver's path property *oracle.driver.path*

4. Edit *src/test/resources/testdb-oracle.properties*" and configure the data source connection properties

5. Run the tests

        mvn clean install

  (append *-Dorg.slf4j.simpleLogger.log.org.dashbuilder.dataprovider.sql.JDBCUtils=debug*
   if you want to see the actual SQL logs being executed)

     
        NOTE: The tests do not change the database contents since all the temporal data created is removed once finished.


The procedure above can be applied to any of the commercial DBs supported: Oracle, SQLServer, DB2 or Sybase.

For the rest of open source DBs you can skip steps #1 to #3 since the driver is added as a module dependency.

