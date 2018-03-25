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
> ?corehunter
> ?sampleCore
> ?objective
> ?coreHunterData
> ?genotypes
> ?phenotypes
> ?distances
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
