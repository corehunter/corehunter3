package org.corehunter;

import uno.informatics.common.model.Dataset;

public class CorehunterArguments
{
	private int minimumSubsetSize;

	private int maximumSubsetSize;
	
	private CorehunterObjective objective ;

	private Dataset dataset;

	public CorehunterArguments(int subsetSize)
	{
		this(subsetSize, subsetSize);
	}

	public CorehunterArguments(int minimumSubsetSize, int maximumSubsetSize)
	{
		super();
		this.minimumSubsetSize = minimumSubsetSize;
		this.maximumSubsetSize = maximumSubsetSize;
	}

	public final int getMinimumSubsetSize()
	{
		return minimumSubsetSize;
	}

	public final void setMinimumSubsetSize(int minimumSubsetSize)
	{
		this.minimumSubsetSize = minimumSubsetSize;
	}

	public final int getMaximumSubsetSize()
	{
		return maximumSubsetSize;
	}

	public final void setMaximumSubsetSize(int maximumSubsetSize)
	{
		this.maximumSubsetSize = maximumSubsetSize;
	}

	public final Dataset getDataset()
	{
		return dataset;
	}

	public final void setDataset(Dataset dataset)
	{
		this.dataset = dataset;
	}

	public final CorehunterObjective getObjective()
	{
		return objective;
	}

	public final void setObjective(CorehunterObjective objective)
	{
		this.objective = objective;
	}
}
