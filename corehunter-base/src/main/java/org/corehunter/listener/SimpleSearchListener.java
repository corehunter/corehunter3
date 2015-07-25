package org.corehunter.listener;

import java.io.PrintStream;

import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.listeners.SearchListener;

public class SimpleSearchListener<SolutionType extends Solution> implements SearchListener<SolutionType> {

	private PrintStream printStream ;
	
	public SimpleSearchListener()
	{
		this(System.err);
	}
	
	public SimpleSearchListener(PrintStream printStream)
	{
		super();
		this.printStream = printStream;
	}

	@Override
	public void searchStarted(Search<? extends SolutionType> search)
	{
		printStream.println(" >>> Search started");
	}

	@Override
	public void searchStopped(Search<? extends SolutionType> search)
	{
		printStream.println(" >>> Search stopped (" + search.getRuntime()/1000 + " sec, " + search.getSteps() + " steps)");
  }

	@Override
	public void newBestSolution(Search<? extends SolutionType> search,
		SolutionType newBestSolution, Evaluation newBestSolutionEvaluation,
		Validation newBestSolutionValidation)
	{
		printStream.println(" >>> New best solution: " + newBestSolutionEvaluation);
	}
}
