# schema-convert

[![Build Status](https://travis-ci.org/modcloth-labs/schema-convert.png?branch=master)](https://travis-ci.org/modcloth-labs/schema-convert)

Things it does when converting:

1. recreate structure in postgres (w/ primary keys and appropriate column types)
2. recreate indices in postgres
3. delete and replace existing tables in postgres

Things it doesn't do (yet):

1. foreign keys
2. be more flexible with data type conversions on per table basis
3. go from postgres -> mysql
4. lots of other database fanciness

### Dependencies

1. jdk 6 or higher
2. maven 3 or higher

### Building

After cloning the repo

    $ cd <REPODIR>

To package to a jar (and run tests)

    $ mvn test package

maven will download a bunch of stuff and build a single jar

### Usage

    $ java -jar target/schema-convert-<VERSION>.jar

With no arguments, this will display a help message.

Basically,

1. Supply a JDBC URL for mysql source
2. Supply a JDBC URL for postgres destination
3. Supply a name of the mysql database
4. Set the other options to sensible values

run it!
