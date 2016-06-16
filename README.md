# README

## Description

This project is the implementation of a small proxy server in Java allowing to
cache request response in a Redis database.

The main purpose of this project is to avoid API rate-limits by caching
response and allowing to redo requests without hitting the cap.

Currently, it only works with GET requests and caching is always activated. It
is not possible to do a request without having the result cached or using the
cached result if it exists.

## Build

To build the project you have to use *gradle*. Gradle is a dependency manager
and build system for Java programs.

You must have Redis installed on your system before you can launch the program.
Redis is used for caching all requests.

```
gradle build
```

## Usage

To use the program you can use the following command. You can use any port as
the server port.

```
java -jar proxy-server-1.0.jar [port]
```
