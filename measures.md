## Evaluation measures
One of the main strengths of Core Hunter is that it can directly optimize a number of different evaluation measures.
If desired, multiple measures can be simultaneously optimized as part of a weighted index. For more information on evaluation measures 
The measures included in Core Hunter 3 are listed below.

### Distance based measures

1. Average entry-to-nearest-entry distance, i.e. the mean distance between each selected accession and the closest other selected accession. Maximizing this measure yields high diversity in the core expressed through maximum dissimilarity of selected core accessions. Aims to represents the whole range of values including the extremes.
2. Average accession-to-nearest-entry distance, i.e. the mean distance between each accession in the entire collection and the closest selected accession. Minimizing this measure yields cores that maximally represent all individual accessions from the full collection. Tends to focus on cluster centers.
3. Average entry-to-entry distance (provided for historical reasons, not preferred).

Gower's distance is used to compute distances from phenotypic traits, and both Modified Roger's as well as Cavalli-Sforza & Edwards distance is supported for genetic marker data. Alternatively, a precomputed distance matrix can be provided by the user.

### Allelic richness

Available for genetic marker data only:

1. Shannon's diversity index.
2. Expected heterozygosity.
3. Allele coverage, i.e. the percentage of marker alleles observed in the full collection that are retained in the core.

Formulas for these three allelic richness measures can be found in the original Core Hunter publication [(Thachuk et al., 2009)](http://www.biomedcentral.com/1471-2105/10/243).
