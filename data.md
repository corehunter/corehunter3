## Supported data types
Core Hunter 3 supports multiple types of genetic marker data, phenotypic traits and precomputed distance matrices. Data can be loaded from files and, when using the R package, from data frames and matrices. Both comma separated `csv` and tab separated `txt` files can be used.

### Genetic marker data

Genotypes can be provided in various formats.

#### Default format

The default format contains one row per accession and one or more columns per marker. It is suited for data with a fixed number of allele observations per specific marker in each individual. The values are allele names or numbers, or in fact any token used to identify the detected alleles. Common cases are those with one or two columns per marker, e.g. suited for homozygous/haploid and diploid data, respectively. The number of observed alleles may vary across markers.

There is one compulsory header column `ID` specifying unique accession identifiers. Optionally, a second header column `NAME` can be included to provide names as well, which need not be unique nor defined for all accessions. The single header row contains marker names which are repeated for each column corresponding to the same marker. Optionally, column names may include a suffix added to the marker name, starting with a `.`, `-` or `_` character. For example, in the case of diploid data, it is allowed that the two columns corresponding to the same marker have names like `M5.1` & `M5.2`, `M17-a` & `M17-b` or  `X_1` & `X_2`, for markers named `M5`, `M17` and `X`, respectively. The column name prefix up to before the last occurrence of any `.`, `-` or `_` character is taken to be the marker name.

This format can not be used for bulk samples. For such data the frequency format should be used.

##### Examples

Diploid example data with five accessions and four markers:

ID | mk1.a | mk1.b | mk2.a | mk2.b | mk3.a | mk3.b | mk4.a | mk4.b
---|-------|-------|-------|-------|-------|-------|-------|-------
A  | 1     | 3     | B     | B     | a1    | a1    | -     | +
B  | 2     | 2     | C     | A     | a1    | a2    | +     | -
C  | 1     | 2     | D     | D     | a2    | a2    | +     | +
D  | 2     | 3     | B     | B     | a2    | a1    | +     | -
E  | 1     | 1     | A     | A     | a1    | a1    | -     | -

Including accession names and with some missing data:

ID | NAME  | mk1-1 | mk1-2 | mk2-1 | mk2-2 | mk3-1 | mk3-2 | mk4-1 | mk4-2
---|-------|-------|-------|-------|-------|-------|-------|-------|------
A  | Alice | 1     | 3     | B     | B     | a1    | a1 
B  | Bob   | 2     | 2     | C     | A     | a1    | a2    | +     | -
C  | Carol | 1     | 2     | D     | D     | a2    | a2    | +     | +
D  | Dave  | 2     | 3     | B     | B     | a2    | a1    | +     | -
E  | Eve   | 1     | 1     |       |       | a1    | a1    | -     | -

Homozygous data:

ID | mk1 | mk2 | mk3 | mk4
---|-----|-----|-----|----
A  | 1   | B   | a1  | -
B  | 2   | C   | a1  | + 
C  | 1   | D   | a2  | +   
D  | 2   | B   | a2  | +   
E  | 1   | A   | a1  | - 

#### Frequency data

This is the original format from Core Hunter 1 and 2, but it has been transposed to follow the convention that rows are observations (accessions) and columns are variables (marker alleles). This format contains one row per accession and one column per combination of marker and allele. The values are allele frequencies which should sum to one for each marker in each accession. At least one column (allele) is provided per marker and the number of alleles per marker may vary.

There is one compulsory header column `ID` specifying unique accession identifiers. Optionally, a second header column `NAME` can be included to provide names as well, which need not be unique nor defined for all accessions. The first, compulsory header row contains marker names, which are repeated for each consecutive column corresponding to the same marker. Optionally, column names may include a suffix added to the marker name, starting with a `.`, `-` or `_` character. An optional second header row `ALLELE` can be included to provide allele names per marker.

This format is suitable for entries of individual or bulk samples.

##### Examples

Example data with five accessions and three markers, having three, two and again three alleles, respectively:

ID | mk1_1 | mk1_2 | mk1_3 | mk2_1 | mk2_2 | mk3_1 | mk3_2 | mk3_3
---|-------|-------|-------|-------|-------|-------|-------|------
A  |       |       |       | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
B  | 1.00  | 0.00  | 0.00  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
C  | 0.60  | 0.00  | 0.40  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
D  |       |       |       | 1.00  | 0.00  |       |       |      
E  | 0.33  | 0.33  | 0.33  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50

Including allele names:

ID         | mk1_1 | mk1_2 | mk1_3 | mk2_1 | mk2_2 | mk3_1 | mk3_2 | mk3_3
-----------|-------|-------|-------|-------|-------|-------|-------|------
**ALLELE** | a     | b     | c     | +     | -     | 1     | 2     | 3
A          |       |       |       | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
B          | 1.00  | 0.00  | 0.00  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
C          | 0.60  | 0.00  | 0.40  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50 
D          |       |       |       | 1.00  | 0.00  |       |       |      
E          | 0.33  | 0.33  | 0.33  | 0.50  | 0.50  | 0.00  | 0.50  | 0.50

Including accession and allele names (first two markers only):

ID         | NAME  | mk1_1 | mk1_2 | mk1_3 | mk2_1 | mk2_2 
-----------|-------|-------|-------|-------|-------|-------
**ALLELE** |       | a     | b     | c     | +     | -     
A          | Alice |       |       |       | 0.50  | 0.50  
B          | Bob   | 1.00  | 0.00  | 0.00  | 0.50  | 0.50 
C          | Carol | 0.60  | 0.00  | 0.40  | 0.50  | 0.50  
D          | Dave  |       |       |       | 1.00  | 0.00    
E          | Eve   | 0.33  | 0.33  | 0.33  | 0.50  | 0.50 

#### Biparental data

This format describes genetic marker data with two alleles per marker. It contains one row per accession and one column per marker with values `0`, `1` and `2` denoting the number of detected occurrences of an arbitrary reference allele. Thus, the values `0` and `2` indicate homozygotes of the two alleles, respectively, while `1` is used for a heterozygote.

There is one compulsory header column `ID` specifying unique accession identifiers. Optionally, a second header column `NAME` can be included to provide names as well, which need not be unique nor defined for all accessions. The single header row may optionally provide marker names.

This format is only suited for data where each marker has (at most) two possible alleles and can not be used to describe bulked data.

##### Examples

Example data with five accessions and seven markers:

ID | mk1 | mk2 | mk3 | mk4 | mk5 | mk6 | mk7
---|-----|-----|-----|-----|-----|-----|----
A  | 1   | 0   | 2   | 1   | 1   | 0   | 0
B  | 2   | 0   | 2   | 0   | 1   | 2   | 1
C  | 1   | 0   |     | 0   | 1   | 1   | 0
D  | 1   | 0   | 1   | 1   | 1   | 2
E  | 1   | 0   |     | 0   |     | 2   | 0

Including accession names:

ID | NAME  | mk1 | mk2 | mk3 | mk4 | mk5 | mk6 | mk7
---|-------|-----|-----|-----|-----|-----|-----|----
A  | Alice | 1   | 0   | 2   | 1   | 1   | 0   | 0
B  | Bob   | 2   | 0   | 2   | 0   | 1   | 2   | 1
C  | Carol | 1   | 0   |     | 0   | 1   | 1   | 0
D  | Dave  | 1   | 0   | 1   | 1   | 1   | 2
E  | Eve   | 1   | 0   |     | 0   |     | 2   | 0

### Precomputed distance matrix

Core Hunter can load an arbitrary distance matrix computed from any type of data using any distance measure. The matrix contains one row and one column per accession, in the same order, which effectively means that the matrix should be symmetric.

There is one compulsory header column `ID` which provides unique accession identifiers. Optionally, a second header column `NAME` can be included to provide names as well, which need not be unique nor defined for all items. Accession identifiers can optionally be repeated on the single header row.

Any row in the matrix can be truncated at or after the diagonal. The diagonal values should always be zero, when included. If some or all entries of the upper triangular part of the matrix are included, it is verified whether they match the lower triangular part, i.e. whether the matrix is effectively symmetric. Truncated values in the upper triangular part are copied from the lower triangular part.

This data type is particularly interesting when dealing with genetic marker data having a very large number of markers, as precomputing the distance matrix is then also an effective way to compress the data. It can also be used to run Core Hunter with data types and/or distance measures that are otherwise not supported.

#### Examples

Example distance matrix with five accessions:

ID | A   | B   | C   | D   | E
---|-----|-----|-----|-----|----
A  | 0.0 | 0.2 | 0.4 | 0.6 | 0.8
B  | 0.2 | 0.0 | 0.2 | 0.4 | 0.6
C  | 0.4 | 0.2 | 0.0 | 0.1 | 0.4
D  | 0.6 | 0.4 | 0.1 | 0.0 | 0.2
E  | 0.8 | 0.6 | 0.4 | 0.2 | 0.0

Including accession names:

ID | NAME  | A   | B   | C   | D   | E
---|-------|-----|-----|-----|-----|----
A  | Alice | 0.0 | 0.2 | 0.4 | 0.6 | 0.8
B  | Bob   | 0.2 | 0.0 | 0.2 | 0.4 | 0.6
C  | Carol | 0.4 | 0.2 | 0.0 | 0.1 | 0.4
D  | Dave  | 0.6 | 0.4 | 0.1 | 0.0 | 0.2
E  | Eve   | 0.8 | 0.6 | 0.4 | 0.2 | 0.0

Truncated at diagonal:

ID | NAME  | A   | B   | C   | D   | E
---|-------|-----|-----|-----|-----|----
A  | Alice | 0.0 
B  | Bob   | 0.2 | 0.0 
C  | Carol | 0.4 | 0.2 | 0.0 
D  | Dave  | 0.6 | 0.4 | 0.1 | 0.0
E  | Eve   | 0.8 | 0.6 | 0.4 | 0.2 | 0.0

### Phenotypic trait data

This data describes observed phenotypic traits and can accommodate several variable types. The format used by Core Hunter includes one row per accession and one column per trait.

There must be one compulsory header column `ID` with unique accession identifiers. A second optional column `NAME` can be added to provide names as well, which need not be unique nor defined for all items. The first row contains the trait names, which need not be unique, but should be unique for later identification. Optionally, a second header row with header `TYPE` can be added which specifies the variable type of each trait (nominal, ordinal, interval, ratio) and optionally the data type. Please refer to the tables below. If this row is not included in the file, variable types are automatically inferred or can be set manually when using the R package, but will default to nominal strings when using the GUI. It is recommended to always specify variable types.

Two more optional rows can be added for ordinal, interval and ratio data to provide indicative minimum and maximum values, with the row headers `MIN` and `MAX`, respectively. If indicative minimum and maximum values are not provided these are calculated from the data. If the data exceed these minimum and maximum values, the actual minimum and maximum values are adjusted accordingly.

#### Variable types

Variable type | Code | Default data type
------------- | ---- | -----------------
Nominal       | N    | String
Ordinal       | O    | Integer
Interval      | I    | Integer
Ratio         | R    | Double

#### Data types

Data type   | Code
----------- | ----
Boolean     | B
Short       | T
Integer     | I
Long        | L
Float       | F
Double      | D
Big Integer | R
Big Decimal | M
Date        | A
String      | S
None        | X

#### Examples

Example phenotypic trait data with five accessions and five traits of differing types.
The data includes one nominal (N), one ordinal (O), one interval (I), one ratio (R) and one nominal boolean (NB) variable.
The latter is treated as an asymmetric binary variable when calculating the Gower distance.
Qualitative traits are commonly encoded as nominal variables while quantitative traits are most often expressed with a ratio variable, or an interval variable in case of integer values.

ID       | trait 1 | trait 2 | trait 3 | trait 4 | trait 5
---------|---------|---------|---------|---------|--------
**TYPE** | N       | O       | I       | R       | NB
A        | A       | 3       | 4       | 1.4     | false
B        | B       | 1       | 5       | 0.5     | true
C        | A       | 0       | 6       | 0.5     | true
D        | C       | 2       | 9       | 0.5     | false
E        | B       | 2       | 1       | 1.3     | true

Including accession names:

ID       | NAME  | trait 1 | trait 2 | trait 3 | trait 4 | trait 5
---------|-------|---------|---------|---------|---------|--------
**TYPE** |       | N       | O       | I       | R       | NB
A        | Alice | A       | 3       | 4       | 1.4     | false
B        | Bob   | B       | 1       | 5       | 0.5     | true
C        | Carol | A       | 0       | 6       | 0.5     | true
D        | Dave  | C       | 2       | 9       | 0.5     | false
E        | Eve   | B       | 2       | 1       | 1.3     | true

With explicit minimum and maximum:

ID       | NAME  | trait 1 | trait 2 | trait 3 | trait 4 | trait 5
---------|-------|---------|---------|---------|---------|--------
**TYPE** |       | N       | O       | I       | R       | NB
**MIN**  |       |         |         | 0       | 0.0     |
**MAX**  |       |         |         | 10      | 2.0     |
A        | Alice | A       | 3       | 4       | 1.4     | false
B        | Bob   | B       | 1       | 5       | 0.5     | true
C        | Carol | A       | 0       | 6       | 0.5     | true
D        | Dave  | C       | 2       | 9       | 0.5     | false
E        | Eve   | B       | 2       | 1       | 1.3     | true

Real phenotypic trait data including 39 accessions for which seven quantitative and seven qualitative traits were recorded.
Here, quantitative traits are ratio variables (R) encoded as double values (default for ratio variables) while qualitative traits are integer valued nominal variables (NI).

ID | Fruit_diam | Seed_wght | nr_flow_axila | Fruit_wght | Plant_hght | Fruit_length | Seed_nr | Corola_color | Anther_color | Corola_spot_color | Fruit_epid | Fruit_form | Flower_position | Corola_form
---|------------|-----------|---------------|------------|------------|--------------|---------|--------------|----------------|-------------------|------------|------------|-----------------|------------
**TYPE**|R|R|R|R|R|R|R|NI|NI|NI|NI|NI|NI|NI
10|20|0.3|1|69.4|40.8|37.3|32.1|4|2|0|2|2|5|1
11|9.5|0.2|2|6.7|33.7|10.7|17.5|4|2|0|2|2|5|1
18|24.9|0.4|1.3|122.4|38.3|51|42.1|4|2|0|2|2|5|1
41|16.3|0.4|1.7|28|43.8|47.1|15.8|4|2|0|2|2|5|1
43|14.3|0.4|2|35.5|42.7|55.3|43.3|4|2|0|2|2|5|1
50|29.2|0.4|2|104.1|55.6|51.8|84.6|4|2|0|2|2|5|1
51|21.9|0.4|2|85.8|50.6|57.2|46.7|4|2|0|2|2|5|1
54|27|0.4|2|79.4|56.6|55|54.4|4|2|0|2|2|5|1
67|14.9|0.5|1.7|30.4|62.7|38.3|17|4|2|0|2|2|5|1
68|14.7|0.3|2|21.3|56.7|21.9|43.1|4|2|0|2|2|5|1
85|19.2|0.5|1.3|41.8|62.2|27.3|43.7|4|2|0|2|2|5|1
147|26.5|0.7|1|73.5|51.9|23.5|93.7|1|2|0|2|2|5|1
149|22.4|0.6|1|55.8|54|21.7|83.5|1|2|0|2|2|5|1
167|21.7|0.5|1|63.3|63.4|36.9|101.1|1|2|0|2|2|5|1
175|18.3|0.6|1|146.5|58.6|82.2|82.3|1|2|0|2|2|5|1
179|11.8|0.5|1|38.8|69.8|68.1|84.7|1|2|0|2|2|5|1
181|20|0.6|1|51.4|62.8|35.8|105.5|1|2|1|2|2|5|1
205|33|0.5|1.3|198.7|59.1|63.8|77.3|1|2|1|2|2|5|1
212|11.3|0.3|2|15.9|40.6|19.7|22.1|1|2|1|2|2|5|1
215|11.1|0.4|2|22.1|49.6|44|43.2|1|2|1|2|2|5|1
216|4.9|0.4|2|5.1|65.4|19.5|16.7|1|2|1|2|2|5|2
221|11.8|0.3|2|13.5|63.1|18.3|29.1|1|2|1|2|2|6|2
224|17.7|0.5|2.3|36.7|51|48.1|47.7|1|3|1|1|2|6|2
225|13.1|0.3|2|12.9|34.7|12.8|14.6|1|3|1|1|2|6|2
233|28|0.3|2.7|86.7|40.9|46.7|80.7|1|3|1|1|2|6|2
239|10.3|0.4|1.3|22.9|58|38.2|20.5|1|3|1|1|3|6|2
242|4.4|0.4|1.3|5.7|71.6|25.3|9.7|1|3|1|1|3|6|2
246|15.2|0.3|2|33.9|46.5|42.2|38.9|1|3|1|1|3|6|2
250|16.3|0.4|2|41.7|58.5|48|37.5|1|3|1|1|3|6|2
252|18.6|0.4|1.3|32.3|51.3|20.6|48.5|1|3|1|1|3|6|2
268|10.6|0.4|1.7|12.4|59.4|42.6|22.4|4|3|1|1|3|6|2
275|18.8|0.4|2|102.9|58.9|82.9|75.3|4|3|1|1|3|6|3
298|11.1|0.3|2|6.8|56.4|11|18.9|4|3|1|1|3|6|3
301|15.3|0.3|2|17.1|48|31.5|34.7|4|3|1|1|3|7|3
340|12.7|0.4|1|31.9|53.3|43.9|53.8|4|3|1|1|3|7|3
347|21.5|0.6|1|160.3|66.1|97.1|118.4|4|3|1|1|3|7|3
363|27.3|0.7|1|127.4|64.1|48.2|58|4|3|1|1|3|7|3
406|22.5|0.3|1.3|28.5|54.5|16.6|46.1|4|3|1|1|3|7|3
407|22.1|0.5|1.3|53.7|54|29.3|53.9|4|3|1|1|3|7|3
