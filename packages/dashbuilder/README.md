# DashBuilder

DashBuilder is a general purpose dashboard and reporting web app which allows for:

- Visual configuration and personalization of dashboards
- Support for different types of visualizations using several charting libraries
- Full featured editor for the definition of chart visualizations
- Definition of interactive report tables
- Data extraction from external systems, through different protocols
- Support for both analytics and real-time dashboards

Licensed under the Apache License, Version 2.0

For further information, please visit the project web site <a href="http://dashbuilder.org" target="_blank">dashbuilder.org</a>

# Architecture

- Not tied to any chart rendering technology. Pluggable renderers and components
- No tied to any data storage.
- Ability to read data from: CSV files, Databases, Elastic Search, Prometheus, Kafka orJava generators.
- Decoupled client & server layers. Ability to build pure lightweight client dashboards.
- Ability to push & handle data sets on client for better performance.
- Based on <a href="http://www.uberfireframework.org" target="_blank">Uberfire</a>, a framework for building rich workbench styled apps on the web.
- Cloud-native Runtime environment.
