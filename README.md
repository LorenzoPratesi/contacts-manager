# Contacts Manager

[![Java CI with Maven in Linux](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven.yml/badge.svg)](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven.yml)
[![Java CI with Maven and Docker in Windows](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven-windows.yml/badge.svg)](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven-windows.yml)
[![Java CI with Maven and Docker in macOS](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven-mac.yml/badge.svg)](https://github.com/LorenzoPratesi/contacts-manager/actions/workflows/maven-mac.yml)

[![Coverage Status](https://coveralls.io/repos/github/LorenzoPratesi/contacts-manager/badge.svg?branch=main)](https://coveralls.io/github/LorenzoPratesi/contacts-manager?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=LorenzoPratesi_contacts-manager&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LorenzoPratesi_contacts-manager)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LorenzoPratesi_contacts-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=LorenzoPratesi_contacts-manager)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=LorenzoPratesi_contacts-manager&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=LorenzoPratesi_contacts-manager)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=LorenzoPratesi_contacts-manager&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=LorenzoPratesi_contacts-manager)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LorenzoPratesi_contacts-manager&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=LorenzoPratesi_contacts-manager)


This repository holds the project for the course Advanced Techniques and Tools for Software Development (ATTSD) Course of Data Science in the University of Florence.  
This project resembles a skeleton of a contact managing application that can store, delete, update and search contacts from a storage or equivalent. The application is intended to be kept simple because the focus of the project was the usage of the techniques learned during the course, such as TDD, Continuous Integration, GIT, Docker, Mutation Testing and more.

## System Requirements

- Maven (version 3.8.2 used during development)
- Docker (version 20.10.10 used during development)
- Java (version 8 or higher)

## How to Build the Project
1. Clone the Repository.
```bash
git clone https://github.com/LorenzoPratesi/contacts-manager
```
2. Set Project Main Directory.
```bash
cd contacts-manager/org.unifi.lorenzopratesi.app.contacts
```

3. Build the Project - Compile code, Unit, Integration and End To End tests
```bash
mvn clean verify
```
or With Also JaCoCo and Pit Report
```bash
mvn clean verify -Pjacoco,mutation-testing
```

## Run the application
### Jar file creation
If you want to build by yourself the application, you have to do the following steps:
1. Clone the Repository.
```bash
git clone https://github.com/LorenzoPratesi/contacts-manager
```
2. Set Project Main Directory.
```bash
cd contacts-manager/org.unifi.lorenzopratesi.app.contacts
```

3. Run a maven build, with clean and package goals
```bash
mvn clean package
```

### Usage
Before launching the application, you have to launch a MongoDB instance. You can launch the docker container using
```bash
docker run -p 27017:27017 --rm mongo:4.4.3
```
Set Project Main Directory.
```bash
cd contacts-manager/org.unifi.lorenzopratesi.app.contacts
```

The application can be launched executing the jar file
```bash
java -jar target/contacts-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

   


