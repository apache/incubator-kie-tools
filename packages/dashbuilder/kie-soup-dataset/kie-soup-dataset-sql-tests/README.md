## Tests for SQL Datasets that can run with any database

Run all tests using a SQL dataset. By default it uses the in-memory H2 database, but you can replace changes in pom.xml for another database. Here's an example of properties for a MySQL database:

```
    <!-- These are properties used in the database profiles. Some of them
      must be initialized to be empty so that Maven applies their values via filtering
      to the resources. -->
    <maven.datasource.classname>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</maven.datasource.classname>
    <maven.jdbc.driver.class>com.mysql.cj.jdbc.Driver</maven.jdbc.driver.class>
    <maven.jdbc.db.name>test</maven.jdbc.db.name>
    <maven.jdbc.db.port>3306</maven.jdbc.db.port>
    <maven.jdbc.db.server>localhost</maven.jdbc.db.server>
    <maven.jdbc.driver.jar>/home/wsiqueir/Downloads/mysql-connector-java-5.1.47.jar</maven.jdbc.driver.jar>
    <maven.jdbc.username>root</maven.jdbc.username>
    <maven.jdbc.password/>
    <maven.jdbc.url>jdbc:mysql://localhost/test</maven.jdbc.url>
    <maven.jdbc.schema>public</maven.jdbc.schema>
```
