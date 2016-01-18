package org.corehunter.tests;
/*******************************************************************************
 * Copyright 2014 Guy Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/


import uno.informatics.common.model.DataType;
import uno.informatics.common.model.Feature;
import uno.informatics.common.model.Method;
import uno.informatics.common.model.OntologyTerm;
import uno.informatics.common.model.Property;
import uno.informatics.common.model.Scale;
import uno.informatics.common.model.ScaleType;

/**
 * @author Guy Davenport
 *
 */
public class MockFeature implements Feature
{
	private Scale scale;

	public MockFeature()
  {
	  super();
	  
  }


	/**
	 * @param dataType
	 * @param scaleType
	 */
  public MockFeature(DataType dataType, ScaleType scaleType)
  {
  	scale = new MockScale(dataType, scaleType) ;
  }
  
	/**
	 * @param dataType
	 * @param scaleType
	 * @param min
	 * @param max
	 */
  public MockFeature(DataType dataType, ScaleType scaleType, double min, double max)
  {
  	scale = new MockRangeScale(dataType, scaleType, min, max) ;
  }
  
	/**
	 * @param dataType
	 * @param scaleType
	 * @param min
	 * @param max
	 */
  public MockFeature(DataType dataType, ScaleType scaleType, int min, int max)
  {
  	scale = new MockRangeScale(dataType, scaleType, min, max) ;
  }
  
	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifier#getAbbreviation()
	 */
	@Override
	public String getAbbreviation()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifier#setAbbreviation(java.lang.String)
	 */
	@Override
	public void setAbbreviation(String value)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifier#getType()
	 */
	@Override
	public OntologyTerm getType()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifier#setType(uno.informatics.model.OntologyTerm)
	 */
	@Override
	public void setType(OntologyTerm value)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Namable#getName()
	 */
	@Override
	public String getName()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Namable#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifiable#getUniqueIdentifier()
	 */
	@Override
	public String getUniqueIdentifier()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Identifiable#setUniqueIdentifier(java.lang.String)
	 */
	@Override
	public void setUniqueIdentifier(String uniqueIdentifier)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Describable#getDescription()
	 */
	@Override
	public String getDescription()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Describable#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#getProperty()
	 */
	@Override
	public Property getProperty()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#setProperty(uno.informatics.model.Property)
	 */
	@Override
	public void setProperty(Property value)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#getScale()
	 */
	@Override
	public Scale getScale()
	{
		
		return scale ;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#setScale(uno.informatics.model.Scale)
	 */
	@Override
	public void setScale(Scale value)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#getMethod()
	 */
	@Override
	public Method getMethod()
	{
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Feature#setMethod(uno.informatics.model.Method)
	 */
	@Override
	public void setMethod(Method value)
	{
		

	}

}
