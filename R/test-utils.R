data.dir <- "corehunter-base/src/test/resources"

#############
# LOAD DATA #
#############

load.multiallelic.data <- function(){
  # read
  freqs <- read.csv(paste(data.dir, "multiallelic/names-and-ids.csv", sep="/"), header = F, skip = 2)
  freqs <- freqs[,3:ncol(freqs)]
  colnames(freqs) <- 1:ncol(freqs)
  # set marker and allele counts
  markers <- 7
  alleles <- c(3,2,3,4,3,2,2)
  cum.alleles <- c(0, cumsum(alleles))
  # normalize
  for(i in 1:nrow(freqs)){
    for(m in 1:markers){
      from <- cum.alleles[m]+1
      to <- cum.alleles[m+1]
      marker.freqs <- freqs[i, from:to]
      if(!any(is.na(marker.freqs))){
        freqs[i, from:to] <- marker.freqs / sum(marker.freqs)
      }
    }
  }
  # combine
  list(freqs = freqs, markers = markers, allele.counts = alleles)
}

################
# MULTIALLELIC #
################

modified.rogers <- function(freqs.1, freqs.2, allele.counts,
                            missing.data.policy = c("floor", "ceil")){
  missing.data.policy <- match.arg(missing.data.policy)
  num.markers <- length(allele.counts)
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

cavalli.sforza.edwards <- function(freqs.1, freqs.2, allele.counts,
                                   missing.data.policy = c("floor", "ceil")){
  modified.rogers(sqrt(freqs.1), sqrt(freqs.2),
                  allele.counts, missing.data.policy)
}

coverage <- function(freqs){
  sum(colSums(freqs, na.rm = TRUE) > 0.0) / ncol(freqs)
}

shannon <- function(freqs, allele.counts){
  num.markers <- length(allele.counts)
  freqs[is.na(freqs)] <- 0
  p <- colMeans(freqs)
  cum.allele.counts <- c(0, cumsum(allele.counts))
  sh <- -sum(sapply(1:num.markers, function(m){
    from <- cum.allele.counts[m]+1
    to <- cum.allele.counts[m+1]
    p.marker <- p[from:to]
    # handle missing data
    a <- which.max(p.marker)
    p.marker[a] <- 1.0 - sum(p.marker[-a])
    p.marker <- p.marker/num.markers
    p.marker <- p.marker[p.marker > 0.0]
    sum(sapply(p.marker, function(f){
      f * log(f)
    }))
  }))
  return(sh)
}

heterozygous.loci <- function(freqs, allele.counts){
  num.markers <- length(allele.counts)
  freqs[is.na(freqs)] <- 0
  p <- colMeans(freqs)
  cum.allele.counts <- c(0, cumsum(allele.counts))
  p.square.sums <- sapply(1:num.markers, function(m){
    from <- cum.allele.counts[m]+1
    to <- cum.allele.counts[m+1]
    p.marker <- p[from:to]
    # handle missing data
    a <- which.max(p.marker)
    p.marker[a] <- 1.0 - sum(p.marker[-a])
    sum(p.marker^2)
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
                                      data$allele.counts,
                                      missing.data.policy)
      }
    }
  }
  return(dist)
}
