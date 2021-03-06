/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.integrationtests.report;

import com.thoughtworks.qdox.model.JavaClass;

public class ReportBean implements Comparable<ReportBean>
{

    private String testId;
    
    private JavaClass javaClass;
    

    public String getTestId()
    {
        return testId;
    }

    public void setTestId( String testId )
    {
        this.testId = testId;
    }

    public JavaClass getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass( JavaClass javaClass )
    {
        this.javaClass = javaClass;
    }

    public int compareTo( ReportBean bean )
    {
        return this.testId.compareTo( bean.testId );
    }
    
    
    
}
