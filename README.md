# Welcome to PowerDale

PowerDale is a small town with around 100 residents.  Most houses have a smart meter installed that can save and send information about how much energy a house has used.

There are three major providers of energy in town that charge different amounts for the power they supply.

- _Dr Evil's Dark Energy_
- _The Green Eco_
- _Power for Everyone_

# Introducing JOI Energy

JOI Energy is a new start-up in the energy industry.  Rather than selling energy they want to differentiate themselves from the market by recording their customers' energy usage from their smart meters and recommending the best supplier to meet their needs.

You have been placed into their development team, whose current goal is to produce an API which their customers and smart meters will interact with.

Unfortunately, two members of the team are on annual leave, and another one has called in sick!  You are left with a ThoughtWorker to progress with the current user stories on the story wall.  This is your chance to make an impact on the business, improve the code base and deliver value.

## Story Wall

At JOI energy the development team use a story wall or Kanban board to keep track of features or "stories" as they are worked on.

The wall you will be working from today has 7 columns:

- Backlog
- Ready for Dev
- In Dev
- Ready for Testing
- In Testing
- Ready for sign off
- Done

Examples can be found here [https://leankit.com/learn/kanban/kanban-board/](https://leankit.com/learn/kanban/kanban-board/)

## Users

To trial the new JOI software 5 people from the JOI accounts team have agreed to test the service and share their energy data.

| User    | Smart Meter ID  | Power Supplier        |
|---------|-----------------|-----------------------|
| Sarah   | `smart-meter-0` | Dr Evil's Dark Energy |
| Peter   | `smart-meter-1` | The Green Eco         |
| Charlie | `smart-meter-2` | Dr Evil's Dark Energy |
| Andrea  | `smart-meter-3` | Power for Everyone    |
| Alex    | `smart-meter-4` | The Green Eco         |

## Overview

JOI Energy is a new energy company that uses data to ensure customers are able to be on the best price plan for their energy consumption.

## API

Below is a list of API endpoints with their respective input and output.

### Store Readings

#### Endpoint

```
POST
/readings/store
```

#### Input

```json
{
    "smartMeterId": <smartMeterId>,
    "electricityReadings": [
        { "time": <timestamp>, "reading": <reading> },
        ...
    ]
}
```

`timestamp`: Unix timestamp example: `1504777098`
`reading`: kW reading of meter at that time example: `0.0503`

### Get Stored Readings

#### Endpoint

```
GET
/readings/read/<smartMeterId>
```

`smartMeterId`: A string value example: `smart-meter-0`

#### Output

```json
[
    { "time": "2017-09-07T10:37:52.362Z", "reading": 1.3524882598124337 },
    ...
]
```

### View Current Price Plan and Compare Usage Cost Against all Price Plans

#### Endpoint

```
GET
/price-plans/compare-all/<smartMeterId>
```

`smartMeterId`: A string value example. `smart-meter-0`

#### Output

```json
{
    "pricePlanId": "price-plan-2",
    "pricePlanComparisons": {
        "price-plan-0": 21.78133785680731809,
        ...
    }
}
```

### View Recommended Price Plans for Usage

#### Endpoint

```
GET
/price-plans/recommend/<smartMeterId>[?limit=<limit>]
```

`smartMeterId`: A string value example. `smart-meter-0`

`limit`: Optional limit to display only a number of price plans, example. `2`

#### Output

```json
[
    {
        "price-plan-0": 15.084324881035297
    },
    ...
]
```

## Requirements

- [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

The project makes use of Gradle and uses the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html), which means you don't need Gradle installed.

## Tasks

List all the tasks that Gradle can do, such as `build` and `test`.

```console
$ ./gradlew tasks
```

## Build

Compiles the project, runs the test and then created an executable JAR file

```console
$ ./gradlew build
```

Run the application using Java and the executable JAR file produced by the Gradle `build` task.  The application will be listening to port `8080`.

```console
$ java -jar build/libs/tw-energy.jar
```

## Test

There are two types of tests, the unit tests and the functional tests.  These can be executed as follows.

- Run unit tests only

  ```console
  $ ./gradlew test
  ```

- Run functional tests only

  ```console
  $ ./gradlew functionalTest
  ```

- Run both unit and functional tests

  ```console
  $ ./gradlew check
  ```

## Run

Run the application which will be listening on port `8080`.

```console
$ ./gradlew bootRun
```
