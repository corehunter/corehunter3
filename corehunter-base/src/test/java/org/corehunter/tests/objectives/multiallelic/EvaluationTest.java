package org.corehunter.tests.objectives.multiallelic;

import static org.corehunter.tests.TestData.PRECISION;

import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.junit.Assert;

public class EvaluationTest
{
	protected void assertEquals(String message, Evaluation expected,
			Evaluation actual, double precision)
		{
			Assert.assertEquals(message, expected.getValue(), actual.getValue(), PRECISION) ;
		}
}
