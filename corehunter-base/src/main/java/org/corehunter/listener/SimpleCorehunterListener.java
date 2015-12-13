package org.corehunter.listener;

import java.io.PrintStream;

import org.corehunter.CorehunterListener;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.subset.SubsetSolution;

public class SimpleCorehunterListener implements CorehunterListener{

	private static final String DEFAULT_PREFIX = "";
	
  private String prefix;
  private PrintStream printStream ;
  
  public SimpleCorehunterListener()
  {
    this(System.err);
  }
  
  public SimpleCorehunterListener(PrintStream printStream)
  {
    super();
    this.printStream = printStream;
    
    prefix = DEFAULT_PREFIX ;
  }

  public final String getPrefix()
  {
    return prefix;
  }

  public final void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }

  @Override
  public void searchStarted(Search<? extends SubsetSolution> search)
  {
    printStream.println(prefix + "Search started");
  }

  @Override
  public void searchStopped(Search<? extends SubsetSolution> search)
  {
    printStream.println(prefix + "Search stopped (" + search.getRuntime()/1000 + " sec, " + search.getSteps() + " steps)");
  }

  @Override
  public void newBestSolution(Search<? extends SubsetSolution> search, SubsetSolution newBestSolution,
      Evaluation newBestSolutionEvaluation, Validation newBestSolutionValidation)
  {
    printStream.println(prefix + "New best solution: " + newBestSolutionEvaluation);
  }

  @Override
  public void preprocessingStarted(String message)
  {
    printStream.println(prefix + message);
  }

  @Override
  public void preprocessingStopped(String message)
  {
    printStream.println(prefix + message);
  }
}
