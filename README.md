Core Hunter 3
=============

### Latest release

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/master.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/master.svg)](https://travis-ci.org/corehunter/corehunter3)

### Development snapshot

[![Coverage Status](http://img.shields.io/coveralls/corehunter/corehunter3/develop.svg)](https://coveralls.io/r/corehunter/corehunter3)
[![Build Status](https://img.shields.io/travis/corehunter/corehunter3/develop.svg)](https://travis-ci.org/corehunter/corehunter3)

Core Hunter 3 is a flexible tool for multi-purpose core subset selection. Version 3 has been recoded from scratch using the [JAMES framework](http://www.jamesframework.org) which provides the applied optimization algorithms. A lot of new features have been added such as the ability to sample cores based on multiple types of genetic marker data, phenotypic traits or a precomputed distance matrix. New and improved evaluation measures were also included, that can be separately or simultaneously optimized.

Running Core Hunter
-------------------

...

Supported data types
--------------------

...

Evaluation measures
-------------------

Core Hunter 3 can optimize the following measures, either separately or simultaneously as part of a weighted index.

#### Distance based measures

- Average entry-to-nearest-entry distance (diversity)
- Average accession-to-nearest-entry distance (representativeness)
- Average entry-to-entry distance (provided for historical reasons, not preferred)

Gower's distance is used to compute distances from phenotypic traits, and both the Modified Roger's as well as Cavalli-Sforza & Edwards distances are supported for genetic marker data. Alternatively, a precomputed distance matrix can be used.

#### Allelic richness

- Shannon's index
- Expected heterozygosity

For genetic marker data only.

#### Auxiliary measures

- Allele coverage

For genetic marker data only




