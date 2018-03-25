Core Hunter 3
=============

### Latest release

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/master.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/master.svg)](https://travis-ci.org/corehunter/corehunter3)

### Development snapshot

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/develop.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/develop.svg)](https://travis-ci.org/corehunter/corehunter3)

Core Hunter is a tool to sample diverse, representative subsets from large germplasm collections, with minimum redundancy. Such so-called core collections have applications in plant breeding and genetic resource management in general. Core Hunter can construct cores based on genetic marker data, phenotypic traits or precomputed distance matrices, optimizing one of many provided evaluation measures depending on the precise purpose of the core (e.g. high diversity, representativeness, or allelic richness). In addition, multiple measures can be simultaneously optimized as part of a weighted index to bring the different perspectives closer together. 
Version 3 has been recoded from scratch using the [JAMES framework](http://www.jamesframework.org) which provides the applied optimization algorithms.

The Core Hunter library is implemented in Java 8 as an open source project (see 
<http://www.corehunter.org>). The latest releases can be found on Maven Central or JCenter with your favourite build tool

For example for base library version 3.2.0 using maven, you can use.

```
<dependency>
    <groupId>org.corehunter</groupId>
    <artifactId>corehunter-base</artifactId>
    <version>3.2.0</version>
</dependency>
```
