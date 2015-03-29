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
import uno.informatics.common.model.OntologyTerm;
import uno.informatics.common.model.Scale;
import uno.informatics.common.model.ScaleType;

/**
 * @author Guy Davenport
 *
 */
public class MockScale implements Scale
{
	private DataType dataType ;
	private ScaleType scaleType ;
	/**
	 * @param dataType
	 * @param scaleType
	 */
  public MockScale(DataType dataType, ScaleType scaleType)
  {
	  this.dataType = dataType ;
	  this.scaleType = scaleType ;
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
	 * @see uno.informatics.model.Scale#getDataType()
	 */
	@Override
	public DataType getDataType()
	{
		
		return dataType;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Scale#setDataType(uno.informatics.model.DataType)
	 */
	@Override
	public void setDataType(DataType dataType)
	{
		

	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Scale#getScaleType()
	 */
	@Override
	public ScaleType getScaleType()
	{
		
		return scaleType ;
	}

	/* (non-Javadoc)
	 * @see uno.informatics.model.Scale#setScaleType(uno.informatics.model.ScaleType)
	 */
	@Override
	public void setScaleType(ScaleType scaleType)
	{
		

	}

}
