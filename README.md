# Explore With Me
Backend of app for sharing events.

# Description
Java 11+, REST based on Spring Boot, Maven, Lombock, PostgreSQL, Hibernate, Docker

This app allow users to public events and related info about it, control requests for participation and rate events. 

Application developed with multimodal architecture:
1) ewm_main_service module - main module for handling endpoints, developed as RESTfull API based on Spring Boot.
2) ewm_stats_service module - using for collection of statistics of events views .

# Deploying

For deploying project you must have Docker. Use command _docker compose up_ from root directory to deploy images and containers.
After that will be possible to send requests to ewm-service container. Project has directory _postman_ where tests for Postman can be taken.