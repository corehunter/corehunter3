data.dir <- "corehunter-base/src/test/resources"

#############
# LOAD DATA #
#############

load.multiallelic.data <- function(){
  freqs <- read.csv(paste(data.dir, "multiallelic/names-and-ids.csv", sep="/"), header = F, skip = 2)
  freqs <- freqs[,3:ncol(freqs)]
  colnames(freqs) <- 1:ncol(freqs)
  list(freqs = freqs, markers = 7, allele.counts = c(3,2,3,4,3,2,2))
}

################
# MULTIALLELIC #
################

modified.rogers <- function(freqs.1, freqs.2, num.markers, allele.counts,
                            missing.data.policy = c("floor", "ceil")){
  missing.data.policy <- match.arg(missing.data.policy)
  cum.allele.counts <- c(0, cumsum(allele.counts))
  dist <- sqrt(sum(sapply(1:num.markers, function(m){
    from <- cum.allele.counts[m]+1
    to <- cum.allele.counts[m+1]
    if(any(is.na(freqs.1[from:to]) | is.na(freqs.2[from:to]))){
      ifelse(missing.data.policy == "floor", 0.0, 2.0)
    } else {
      sum((freqs.1[from:to] - freqs.2[from:to])^2)
    }
  })) / (2*num.markers))
  return(dist)
}

cavalli.sforza.edwards <- function(freqs.1, freqs.2, num.markers, allele.counts,
                                   missing.data.policy = c("floor", "ceil")){
  modified.rogers(sqrt(freqs.1), sqrt(freqs.2), num.markers,
                  allele.counts, missing.data.policy)
}

coverage <- function(freqs){
  sum(colSums(freqs, na.rm = TRUE) > 0.0) / ncol(freqs)
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

# TODO: handle missing data
heterozygous.loci <- function(freqs, allele.counts){
  num.markers <- length(allele.counts)
  p <- colMeans(freqs)
  p.square <- p*p
  cum.allele.counts <- c(0, cumsum(allele.counts))
  p.square.sums <- sapply(1:num.markers, function(m){
    from <- cum.allele.counts[m]+1
    to <- cum.allele.counts[m+1]
    sum(p.square[from:to])
  })
  he <- mean(1 - p.square.sums)
  return(he)
}

###########
# GENERAL #
###########

distance.matrix <- function(data, distance.measure, missing.data.policy){
  n <- nrow(data$freqs)
  dist <- matrix(NA, nrow = n, ncol = n)
  for(i in 1:n){
    for(j in 1:n){
      if(i == j){
        dist[i,j] <- 0.0
      } else {
        dist[i,j] <- distance.measure(data$freqs[i,],
                                      data$freqs[j,],
                                      data$markers,
                                      data$allele.counts,
                                      missing.data.policy)
      }
    }
  }
  return(dist)
}
