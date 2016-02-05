###########################
# load Core Hunter into R #
###########################

# function to run Core Hunter
corehunter.run <- function(options, mem="512m"){
    # set path to core hunter CLI
    cli = "corehunter.jar"
    if(!file.exists(cli)){
        cli = "corehunter-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/corehunter-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/corehunter.jar"
    }
    if(!file.exists(cli)){
        stop("Core Hunter CLI jar file not found.")
    }
    # run CLI
    mempar = paste("-Xmx", mem, sep="")
    system(paste("java", mempar, "-jar", cli, options))
}

# function to run Core Analyser
coreanalyser.run <- function(options, mem="512m"){
    # set path to core analyser CLI
    cli = "coreanalyser.jar"
    if(!file.exists(cli)){
        cli = "coreanalyser-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/coreanalyser-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/coreanalyser.jar"
    }
    if(!file.exists(cli)){
        stop("Core Analyser CLI jar file not found.")
    }
    # run CLI
    mempar = paste("-Xmx", mem, sep="")
    output = system(paste("java", mempar, "-jar", cli, options), intern=TRUE)
    # remove head and tail from output
    measures = output[2];
    output = output[3:(length(output)-4)]
    # parse measures
    measures = gsub("^\\s+|\\s+$", "", measures)
    measures = strsplit(measures, "\\s+")[[1]]
    # create results data frame
    results = data.frame()
    # parse dataset evaluations and fill data frame
    for(i in 1:length(output)){
        # get dataset name
        tmp = gregexpr(":", output[i])[[1]]
        tmp = tmp[length(tmp)]
        dataset = substr(output[i], 1, tmp-1)
        # get evaluations
        evaluations = substr(output[i], tmp+1, nchar(output[i]))
        evaluations = gsub("^\\s+|\\s+$", "", evaluations)
        evaluations = strsplit(evaluations, "\\s+")[[1]]
        # store in results data frame
        for(m in 1:length(measures)){
            results[dataset, measures[m]] = evaluations[m];
        }
    }
    #return results
    return(results);
}






