/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.sql;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataprovider.sql.dialect.DB2Dialect;
import org.dashbuilder.dataprovider.sql.dialect.DefaultDialect;
import org.dashbuilder.dataprovider.sql.dialect.Dialect;
import org.dashbuilder.dataprovider.sql.dialect.H2Dialect;
import org.dashbuilder.dataprovider.sql.dialect.MonetDBDialect;
import org.dashbuilder.dataprovider.sql.dialect.MySQLDialect;
import org.dashbuilder.dataprovider.sql.dialect.OracleDialect;
import org.dashbuilder.dataprovider.sql.dialect.OracleLegacyDialect;
import org.dashbuilder.dataprovider.sql.dialect.PostgresDialect;
import org.dashbuilder.dataprovider.sql.dialect.SQLServerDialect;
import org.dashbuilder.dataprovider.sql.dialect.SybaseASEDialect;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCUtils {

    public static final Dialect DEFAULT = new DefaultDialect();
    public static final Dialect H2 = new H2Dialect();
    public static final Dialect MYSQL = new MySQLDialect();
    public static final Dialect POSTGRES = new PostgresDialect();
    public static final Dialect ORACLE = new OracleDialect();
    public static final Dialect ORACLE_LEGACY = new OracleLegacyDialect();
    public static final Dialect SQLSERVER = new SQLServerDialect();
    public static final Dialect DB2 = new DB2Dialect();
    public static final Dialect SYBASE_ASE = new SybaseASEDialect();
    public static final Dialect MONETDB = new MonetDBDialect();

    private static final Logger log = LoggerFactory.getLogger(JDBCUtils.class);

    public static List<SQLDataSourceDef> listDatasourceDefs() {
        List<SQLDataSourceDef> result = new ArrayList<>();
        String[] namespaces = {"java:comp/env/jdbc/", "java:jboss/datasources/"};
        for (String namespace : namespaces) {
            try {
                InitialContext ctx = new InitialContext();
                NamingEnumeration<NameClassPair> list = ctx.list(namespace);
                while (list.hasMoreElements()) {
                    NameClassPair next = list.next();
                    String name = next.getName();
                    String jndiPath = namespace + name;
                    SQLDataSourceDef dsDef = new SQLDataSourceDef(jndiPath, name);
                    result.add(dsDef);
                }
            } catch (NamingException e) {
                log.warn("JNDI namespace {} error: {}", namespace, e.getMessage());
            }
        }
        return result;
    }

    public static void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            if (log.isDebugEnabled()) {
                log.debug(sql);
            }
            statement.execute(sql);
        } catch (SQLException e) {
            log.error(sql);
            throw e;
        }
    }

    public static <T> T metadata(Connection connection, String sql, Function<ResultSetMetaData, T> callback) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            if (log.isDebugEnabled()) {
                log.debug(sql);
            }
            return callback.apply(ps.getMetaData());
        } catch (SQLException e) {
            log.error(sql);
            throw e;
        }
    }

    public static ResultSetHandler executeQuery(Connection connection, String sql) throws SQLException {
        try {
            if (log.isDebugEnabled()) {
                log.debug(sql);
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return new ResultSetHandler(resultSet, statement);
        } catch (SQLException e) {
            log.error(sql);
            throw e;
        }
    }

    public static Dialect dialect(Connection connection) {
        try {
            DatabaseMetaData m = connection.getMetaData();
            String url = m.getURL();
            if (!StringUtils.isBlank(url)) {
                return dialect(url, m.getDatabaseMajorVersion());
            }
            String dbName = m.getDatabaseProductName();
            return dialect(dbName.toLowerCase());
        }
        catch (SQLException e) {
            log.error("Exception while getting dialect from connection: {}", e);
            return DEFAULT;
        }
    }

    public static Dialect dialect(String url, int majorVersion) {

        if (url.contains(":h2:")) {
            return H2;
        }
        if (url.contains(":mysql:")) {
            return MYSQL;
        }
        if (url.contains(":mariadb:")) {
            return MYSQL;
        }
        if (url.contains(":postgresql:")) {
            return POSTGRES;
        }
        if (url.contains(":oracle:")) {
            if (majorVersion < 12) {
                return ORACLE_LEGACY;
            } else {
                return ORACLE;
            }
        }
        if (url.contains(":sqlserver:")) {
            return SQLSERVER;
        }
        if (url.contains(":db2:")) {
            return DB2;
        }
        if (url.contains(":sybase:")) {
            return SYBASE_ASE;
        }
        if (url.contains(":monetdb:")) {
            return MONETDB;
        }
        return DEFAULT;
    }

    public static Dialect dialect(String dbName) {
        if (dbName.contains("h2")) {
            return H2;
        }
        if (dbName.contains("mysql")) {
            return MYSQL;
        }
        if (dbName.contains("postgre") || dbName.contains("enterprisedb")) {
            return POSTGRES;
        }
        if (dbName.contains("oracle")) {
            return ORACLE;
        }
        if (dbName.contains("microsoft") || dbName.contains("sqlserver") || dbName.contains("sql server")) {
            return SQLSERVER;
        }
        if (dbName.contains("db2")) {
            return DB2;
        }
        if (dbName.contains("ase") || dbName.contains("adaptive")) {
            return SYBASE_ASE;
        }
        if (dbName.contains("monet")) {
            return MONETDB;
        }
        return DEFAULT;
    }

    public static List<Column> getColumns(ResultSet resultSet, String[] exclude) {
        try {
            List<Column> columnList = new ArrayList<>();
            List<String> columnExcluded = exclude == null ? new ArrayList<String>() : Arrays.asList(exclude);
        
            ResultSetMetaData meta = resultSet.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
            String name = meta.getColumnName(i);
            String alias = meta.getColumnLabel(i);
            if (alias != null && !alias.trim().isEmpty()) {
                name = alias.trim();
            }
            if (!columnExcluded.contains(name) && !columnExcluded.contains(alias)) {
                ColumnType type = JDBCUtils.calculateType(meta.getColumnType(i));
                if (type != null) {
                    int size = meta.getColumnDisplaySize(i);
                    Column column = SQLFactory.column(name, type, size);
                    columnList.add(column);
                }
            }
             }
            return columnList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String fixCase(Connection connection, String id) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            if (meta.storesLowerCaseIdentifiers()) {
                return changeCaseExcludeQuotes(id, false);
            }
            if (meta.storesUpperCaseIdentifiers()) {
                return changeCaseExcludeQuotes(id, true);
            }
        } catch (SQLException e) {
            log.error("SQLException while fixing case of connection metadata.");
        }
        return id;
    }

    public static final String[] QUOTES = new String[]{"\"", "'", "`", "´"};

    public static List<String> getWordsBetweenQuotes(String s) {
        List<String> result = new ArrayList<>();
        if (s != null) {
            for (int i = 0; i < QUOTES.length; i++) {
                String quote = QUOTES[i];
                String[] words = StringUtils.substringsBetween(s, quote, quote);
                if (words != null) {
                    result.addAll(Arrays.asList(words));
                }
            }
        }
        return result;

    }
    public static String changeCaseExcludeQuotes(String s, boolean upper) {
        List<String> keepList = getWordsBetweenQuotes(s);
        String tmpStr = upper ? s.toUpperCase() : s.toLowerCase();
        for (String word : keepList) {
            String tmpWord = upper ? word.toUpperCase() : word.toLowerCase();
            for (int i = 0; i < QUOTES.length; i++) {
                String quote = QUOTES[i];
                tmpStr = StringUtils.replace(tmpStr, quote + tmpWord + quote, quote + word + quote);
            }
        }
        return tmpStr;
    }

    public static ColumnType calculateType(int sqlDataType) {
        switch (sqlDataType) {

            // Category-like columns.
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.BIT:
            case Types.BOOLEAN: {
                return ColumnType.LABEL;
            }

            // Text-like columns.
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.LONGNVARCHAR: {
                return ColumnType.TEXT;
            }

            // Number-like columns.
            case Types.TINYINT:
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT: {
                return ColumnType.NUMBER;
            }

            // Date-like columns.
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP: {
                return ColumnType.DATE;
            }

            // Unsupported
            default: {
                return null;
            }
        }
    }

    /**
     * Converts a clob value to String
     * @param value
     *  The clob value to be converted
     * @return
     * The clob String value or an empty String if there's any problem converting it 
     */
    public static String clobToString(Clob value) {
        String result = "";
        try (Reader valueReader = value.getCharacterStream()) {
            result = IOUtils.toString(valueReader);
        } catch (Exception e) {
            log.debug("Not able to convert Clob", e);
        }
        return result;
    }
}
