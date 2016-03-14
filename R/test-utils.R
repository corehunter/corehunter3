data.dir <- "corehunter-base/src/test/resources"

#############
# LOAD DATA #
#############

load.multiallelic.data <- function(){
  freqs <- read.csv(paste(data.dir, "multiallelic/names-and-ids.csv", sep="/"), header = F, skip = 2)
  freqs <- freqs[,3:ncol(freqs)]
  colnames(freqs) <- 1:ncol(freqs)
  list(freqs = freqs, m = 7)
}

################
# MULTIALLELIC #
################

modified.rogers <- function(freqs.1, freqs.2, num.markers){
  sqrt(sum((freqs.1 - freqs.2)^2, na.rm = TRUE) / (2*num.markers))
}

cavalli.sforza.edwards <- function(freqs.1, freqs.2, num.markers){
  modified.rogers(sqrt(freqs.1), sqrt(freqs.2), num.markers)
}

coverage <- function(freqs){
  sum(colSums(freqs, na.rm = TRUE) > 0.0) / ncol(freqs)
}

proportion.non.informative.alleles <- function(freqs){
  1.0 - coverage(freqs)
}

# TODO: handle missing data
shannon <- function(freqs, num.markers){
  p <- colMeans(freqs)
  p <- p/num.markers
  if(!isTRUE(all.equal(sum(p), 1.0, tol = 0.001))){
    stop(sprintf(
      "Something is wrong: normalized frequencies should sum to one. Got: %.5f.",
      sum(p)
    ))
  }
  p <- p[p > 0.0]
  sh <- -sum(p*log(p))
  return(sh)
}

###########
# GENERAL #
###########

distance.matrix <- function(allele.frequencies, num.markers, distance.measure){
  n <- nrow(allele.frequencies)
  dist <- matrix(NA, nrow = n, ncol = n)
  for(i in 1:n){
    for(j in 1:n){
      dist[i,j] <- distance.measure(allele.frequencies[i,],
                                    allele.frequencies[j,],
                                    num.markers)
    }
  }
  return(dist)
}
