Looking for the old Core Hunter 2 website? It's [here](v2/).

## What is Core Hunter?
Core Hunter is a flexible tool to sample diverse, representative subsets from large germplasm collections, with minimum redundancy. Such so-called core collections have applications in plant breeding and genetic resource management in general. Core Hunter can construct cores based on genetic marker data, phenotypic traits or precomputed distance matrices, optimizing one of many provided evaluation measures depending on the precise purpose of the core (e.g. high diversity, representativeness, or allelic richness). In addition, multiple measures can be simultaneously optimized as part of a weighted index to bring the different perspectives closer together.

Version 3 has been recoded from scratch using the [JAMES framework](http://www.jamesframework.org) which provides the applied optimization algorithms.

## User group

For the latest information on Core Hunter please subscribe to the [user group](https://groups.google.com/d/forum/corehunter-users).

## Evaluation measures
One of the main strengths of Core Hunter is that it can directly optimize a number of different evaluation measures.
If desired, multiple measures can be simultaneously optimized as part of a weighted index.
The measures included in Core Hunter 3 are listed below.

### Distance based measures

1. Average entry-to-nearest-entry distance, i.e. the mean distance between each selected accession and the closest other selected accession. Maximizing this measure yields high diversity in the core (maximum dissimilarity of core accessions). Tends to include cluster edges.
2. Average accession-to-nearest-entry distance, i.e. the mean distance between each accession in the entire collection and the closest selected accession. Minimizing this measure yields cores that maximally represent all individual accessions from the full collection. Tends to focus on cluster centers.
3. Average entry-to-entry distance (provided for historical reasons, not preferred).

Gower's distance is used to compute distances from phenotypic traits, and both the Modified Roger's as well as Cavalli-Sforza & Edwards distances are supported for genetic marker data. Alternatively, a precomputed distance matrix can be used.

### Allelic richness

1. Shannon's index.
2. Expected heterozygosity.
3. Allele coverage.

Available for genetic marker data only.

## Getting started with Core Hunter
Core Hunter is implemented in Java and can be executed in R or using the graphical interface. In both cases you will need to have installed a [Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) version 8 or later.

### R Package
The package `corehunter` is available on CRAN and can be installed with

```R
> install.packages("corehunter")
```

Afterwards, load the package

```R
> library(corehunter)
```

and your genotypes, phenotypes and/or distance matrix, for example using one of the following commands

```R
# default genotype format (e.g. SSR)
> my.data <- genotypes(file = "geno.csv", format = "default")
# frequency format (e.g. SSR)
> my.data <- genotypes(file = "geno.csv", format = "frequency")
# bi-allelic data (e.g. SNP)
> my.data <- genotypes(file = "geno.csv", format = "biparental")
# phenotypic traits
> my.data <- phenotypes(file = "pheno.csv")
# precomputed distance matrix
> my.data <- distances(file = "dist.csv")
# genotypes and phenotypes
> my.data <- coreHunterData(
    genotypes = genotypes(file = "geno.csv", format = "biparental"),
    phenotypes = phenotypes(file = "pheno.csv")
)
```

More information about the supported data file formats is provided [here](data). Alternatively data can also be loaded from an R matrix or data frame. Sampling a core collection is then as easy as

```R
> core <- sampleCore(my.data)
```

There are numerous options when sampling a core. For example, you can change the size of the core (defaults to 20%), optimize a specific measure (defaults to average entry-to-nearest-entry distance), maximize a weighted index including multiple measures, change stop conditions (by default, the algorithm stops when it was unable to further improve the core during the last 10 seconds), etc. All functions have detailed documentation, for example try

```R
> ?genotypes
> ?phenotypes
> ?distances
> ?coreHunterData
> ?sampleCore
> ?objective
```

Many examples are included in the R package as well.

#### Memory limits
Core Hunter uses the `rJava` package to execute Java code from R. By default, only part of the available memory is reserved for the Java Virtual Machine. To sample cores from large datasets you may need to increase the memory limit to several gigabytes. For example, to use 8 GB, assuming that your computer has at least that much RAM memory, set the option

```R
> options(java.parameters = "-Xmx8G")
``` 

*before* `rJava` is loaded (or any other package using this library, like Core Hunter). It is preferred to set this option in your R profile. To verify whether the memory limit was successfully increased, you may retrieve the runtime value while Core Hunter is loaded, with

```R
> library(corehunter)
> J("java.lang.Runtime")$getRuntime()$maxMemory() / (1024^3)
```

This should print a value close to what you specified using the `-Xmx...` option. If it prints a much lower value `rJava` was probably already loaded when you tried to increase the memory, which does not work. If you are sure that you set the option before `rJava` was loaded, consider shutting down any other Java applications that may be running on your computer before loading Core Hunter in R.

When dealing with genetic marker datasets having many more markers than individuals, an alternative option to reduce the memory pressure is to precompute a distance matrix for analysis in Core Hunter. This solution however restricts the analysis to distance-based measures, since allelic richness can only be computed and maximized if full marker data is available.

### Graphical interface
A simple graphical interface is under development. More information will be posted here soon.
