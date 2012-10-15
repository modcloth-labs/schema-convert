package com.modcloth.core;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Parses command line options and arguments.
 * 
 * @author modcloth
 *
 */
public class OptionParser {
    @Option(name="--my-url", usage="JDBC connection url for MySQL database", metaVar="URL")
    private String mysqlUrl;

    @Option(name="--pg-url", usage="JDBC connection url for PostgreSQL database", metaVar="URL")
    private String postgresUrl;

    @Option(name="--my-db", usage="Name of source MySQL database", metaVar="NAME")
    private String sourceDbName;

    @Option(name="--key-pattern", usage="Pattern for matching table surrogate keys", metaVar="PATTERN")
    private String surrogateKeyPattern;
 
    // TODO make me work
    @Option(name="--use-small-ints", usage="Convert MySQL TINYINT to PG SMALLINT (default is BOOLEAN)")
    private boolean tinyIntToSmallInt;

    @Option(name="--delete-tables", usage="Delete existing tables in destination database (default: false)")
    private boolean deleteAllTables;

    @Argument
    private List<String> arguments = new ArrayList<String>();

    /**
     * Default constructor
     */
    public OptionParser() {
        tinyIntToSmallInt = false;
        deleteAllTables = false;
    }

    /**
     * Parse the command-line options and arguments passed to the program.
     * 
     * @param args the list of command-line arguments passed to the program
     * @return true if the options are parsed, false if they failed to be parsed
     */
    public boolean parse(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);

            if (arguments.isEmpty()) {
                throw new CmdLineException(parser, "No table names were given");
            }
            if (mysqlUrl == null || mysqlUrl.equals("")) {
                throw new CmdLineException(parser, "No MySQL connection URL was given");
            }
            if (postgresUrl == null || postgresUrl.equals("")) {
                throw new CmdLineException(parser, "No PostgreSQL connection URL was given");
            }
            if (sourceDbName == null || sourceDbName.equals("")) {
                throw new CmdLineException(parser, "No source database name was given");
            }
            if (surrogateKeyPattern == null || surrogateKeyPattern.equals("")) {
                throw new CmdLineException(parser, "No surrogate key pattern was given");
            }
        } catch(CmdLineException cle) {
            System.err.println(cle.getMessage());
            System.err.println("java schema-convert [options...] arguments...");
            parser.printUsage(System.err);
            return false;
        }
        return true;
    }

    /**
     * @return the MySQL connection URL
     */
    public String getMysqlUrl() {
      return mysqlUrl;
    }

    /**
     * @return the PostgreSQL connection URL
     */
    public String getPostgresUrl() {
        return postgresUrl;
    }

    /**
     * @return the name of the source database
     */
    public String getSourceDbName() {
        return sourceDbName;
    }

    /**
     * @return the surrogate key pattern to be matched in the source database
     */
    public String getKeyPattern() {
        return surrogateKeyPattern;
    }

    /**
     * @return the flag that indicates how MySQL TINYINT will be converted in PostgreSQL
     */
    public boolean getTinyIntToSmallInt() {
        return tinyIntToSmallInt;
    }

    /**
     * @return the flag that indicats whether the tables in the destination database should be deleted
     */
    public boolean getDeleteAllTables() {
        return deleteAllTables;
    }

    /**
     * @return the list of command-line arguments passed to the program after options have been parsed
     */
    public List<String> getArguments() {
        return arguments;
    }
}
