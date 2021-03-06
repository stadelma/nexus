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
package org.sonatype.nexus.configuration.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;
import org.sonatype.configuration.validation.ValidationResponse;
import org.sonatype.nexus.configuration.CoreConfiguration;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.test.PlexusTestCaseSupport;

public class Xpp3DomMergeTest
    extends PlexusTestCaseSupport
{
    private static final String XML_BASE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><externalConfiguration></externalConfiguration>";

    @Test
    public void testMergeOfCollection()
        throws Exception
    {
        List<String> empty = Collections.emptyList();

        SimpleXpp3ConfigHolder aHolder = new SimpleXpp3ConfigHolder( XML_BASE );
        SimpleXpp3ConfigHolder bHolder = new SimpleXpp3ConfigHolder( XML_BASE );

        aHolder.setCollection( bHolder.getRootNode(), "memberRepositories", empty );
        aHolder.addToCollection( aHolder.getRootNode(), "memberRepositories", "central-m1", true );
        aHolder.addToCollection( aHolder.getRootNode(), "memberRepositories", "m1h", true );
        aHolder.addToCollection( aHolder.getRootNode(), "memberRepositories", "m1p", true );

        bHolder.setCollection( bHolder.getRootNode(), "memberRepositories", empty );
        bHolder.addToCollection( bHolder.getRootNode(), "memberRepositories", "central-m1", true );
        bHolder.addToCollection( bHolder.getRootNode(), "memberRepositories", "m1h", true );
        bHolder.addToCollection( bHolder.getRootNode(), "memberRepositories", "m1p", true );

        bHolder.removeFromCollection( bHolder.getRootNode(), "memberRepositories", "m1p" );

        aHolder.apply( bHolder );

        SimpleXpp3ConfigHolder resultHolder = new SimpleXpp3ConfigHolder( XML_BASE );
        resultHolder.setCollection( resultHolder.getRootNode(), "memberRepositories", empty );
        resultHolder.addToCollection( resultHolder.getRootNode(), "memberRepositories", "central-m1", true );
        resultHolder.addToCollection( resultHolder.getRootNode(), "memberRepositories", "m1h", true );

        assertTrue( resultHolder.getRootNode().equals( aHolder.getRootNode() ) );
    }

    private static class SimpleXpp3ConfigHolder
        extends AbstractXpp3DomExternalConfigurationHolder
    {
        public SimpleXpp3ConfigHolder( String xml )
            throws XmlPullParserException, IOException
        {
            super( Xpp3DomBuilder.build( new StringReader( xml ) ) );
        }

        @Override
        public ValidationResponse doValidateChanges( ApplicationConfiguration applicationConfiguration,
                                                     CoreConfiguration owner, Xpp3Dom configuration )
        {
            return new ValidationResponse();
        }
    }
}
