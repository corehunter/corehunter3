package org.corehunter;

public enum CorehunterObjective
{
	MR ("Modified Rogers distance"), 
	CE ("Cavalli-Sforza and Edwards distance"), 
	SH ("Shannon diversity index"), 
	HE ("Expected proportion of heterozygous loci per individual"), 
	NE ("Number of effective alleles"), 
	PN ("Proportion of non-informative alleles"), 
	CV ("Coverage of alleles"),
	GD ("Gowers Distance") ;
	
	private String name ;
	
	CorehunterObjective(String name)
	{
		this.name = name ;
	}

	public final String getName()
	{
		return name;
	}
}
