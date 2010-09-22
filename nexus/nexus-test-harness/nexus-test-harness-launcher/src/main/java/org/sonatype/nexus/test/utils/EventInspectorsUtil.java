package org.sonatype.nexus.test.utils;

import java.io.IOException;

import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;

public class EventInspectorsUtil
    extends ITUtil
{
    public EventInspectorsUtil( AbstractNexusIntegrationTest test )
    {
        super( test );
    }

    public boolean isCalmPeriod()
        throws IOException
    {
        final Response response = RequestFacade.doGetRequest( "service/local/eventInspectors/isCalmPeriod" );

        if ( response.getStatus().isSuccess() )
        {
            // only 200 Ok means calm period,
            // otherwise 202 Accepted is returned
            return response.getStatus().getCode() == Status.SUCCESS_OK.getCode();
        }
        else
        {
            throw new IOException( "The isCalmPeriod REST resource reported an error ("
                + response.getStatus().toString() + "), bailing out!" );
        }
    }

    public void waitForCalmPeriod()
        throws IOException, InterruptedException
    {
        final int RETRIES = 10;

        for ( int i = 0; i < RETRIES; i++ )
        {
            if ( isCalmPeriod() )
            {
                return;
            }

            Thread.sleep( 800 );
        }

        throw new IOException( "Was not able to get to calm period even afer " + RETRIES + " retries!" );
    }
}
