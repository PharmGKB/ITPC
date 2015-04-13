[![DOI](https://zenodo.org/badge/6285/PharmGKB/ITPC.svg)](http://dx.doi.org/10.5281/zenodo.16801)

ITPC Data Parser
================

This project contains code for processing data collected by the International Tamoxifen Pharmacogenomics Consortium.

The consortium's site can be found at [the PharmGKB website](http://www.pharmgkb.org/views/project.jsp?pId=63).

This project was written and is maintained using the IntelliJ IDEA 9 IDE.

Building and Running
-------------------
To build the code

1. Update _build.properties_ to point to your JDK 1.6 home
2. Build the ItpcParser.jar file by running: _ant makeJar_ (it will be in the _out_ directory)
3. To run the parser execute: _java -jar ItpcParser.jar -f /path/to/itpc.xls_

To get a full list of command line parameters use the command _java -jar ItpcParser.jar -h_

About the Code
--------------
This project was written in the JDK 1.6.0 Update 20

The supporting library versions are listed below:

* commons-cli.jar :: Apache Commons CLI 2.0
* commons-io.jar :: Apache Commons I/O 1.4
* commons-lang.jar :: Apache Commons Lang 2.4
* log4j.jar :: Log4J 1.2.15
* poi-ooxml.jar, poi.jar :: Apache POI 3.6

Contact
-------
For questions and requests, please contact [Ryan Whaley](mailto:ryan.whaley@stanford.edu).
