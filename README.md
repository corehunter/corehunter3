Core Hunter 3
=============

### Latest release

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/master.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/master.svg)](https://travis-ci.org/corehunter/corehunter3)

### Development snapshot

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/develop.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/develop.svg)](https://travis-ci.org/corehunter/corehunter3)

Core Hunter is a tool to sample diverse, representative subsets from large germplasm collections, with minimum redundancy. Such so-called core collections have applications in plant breeding and genetic resource management in general. Core Hunter can construct cores based on genetic marker data, phenotypic traits or precomputed distance matrices, optimizing one of many provided evaluation measures depending on the precise purpose of the core (e.g. high diversity, representativeness, or allelic richness). In addition, multiple measures can be simultaneously optimized as part of a weighted index to bring the different perspectives closer together. The Core Hunter library is implemented in Java 8 as an open source project (see 
<http://www.corehunter.org>).

Version 3 has been recoded from scratch using the [JAMES framework](http://www.jamesframework.org) which provides the applied optimization algorithms.

Running Core Hunter
-------------------

Core Hunter 3 is available as an [R package](https://github.com/corehunter/corehunter3-r) `corehunter` that can be installed from CRAN with

```R
> install.packages("corehunter")
```

A graphical interface is also being developed. For more information, see <http://www.corehunter.org>.

Supported data types
--------------------

Core Hunter 3 supports multiple types of genetic marker data, phenotypic traits and precomputed distance matrices. See <http://www.corehunter.org> for more details.

Evaluation measures
-------------------

One of the main strengths of Core Hunter is that it can directly optimize a number of different evaluation measures. If desired, multiple measures can be simultaneously optimized as part of a weighted index. The measures included in Core Hunter 3 are listed below.

#### Distance based measures

- Average entry-to-nearest-entry distance (diversity)
- Average accession-to-nearest-entry distance (representativeness)
- Average entry-to-entry distance (provided for historical reasons, not preferred)

Gower's distance is used to compute distances from phenotypic traits, and both the Modified Roger's as well as Cavalli-Sforza & Edwards distances are supported for genetic marker data. Alternatively, a precomputed distance matrix can be used.

#### Allelic richness

- Shannon's index
- Expected heterozygosity
- Allele coverage

Available for genetic marker data only.




