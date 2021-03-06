/**
 * Copyright (c) 2008-2011 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.nexus.plugins.migration.nexus1434;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.sonatype.nexus.plugins.migration.AbstractMigrationIntegrationTest;
import org.sonatype.nexus.plugins.migration.util.PlexusUserMessageUtil;
import org.sonatype.nexus.rest.model.RepositoryTargetListResource;
import org.sonatype.nexus.test.utils.GroupMessageUtil;
import org.sonatype.nexus.test.utils.PrivilegesMessageUtil;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.RoleMessageUtil;
import org.sonatype.nexus.test.utils.TargetMessageUtil;
import org.sonatype.security.rest.model.PlexusRoleResource;
import org.sonatype.security.rest.model.PlexusUserResource;
import org.sonatype.security.rest.model.PrivilegeStatusResource;
import org.sonatype.security.rest.model.RoleResource;
import org.testng.annotations.Test;

public abstract class AbstractImportSecurityIT
    extends AbstractMigrationIntegrationTest
{

    protected PlexusUserMessageUtil userUtil;

    protected TargetMessageUtil repoTargetUtil;

    protected RoleMessageUtil roleUtil;

    protected PrivilegesMessageUtil privilegeUtil;

    protected RepositoryMessageUtil repoUtil;

    protected GroupMessageUtil groupUtil;

    /**
     * System user, role, privilege, repoTarget, before importing artifactory
     */
    protected List<PrivilegeStatusResource> prePrivilegeList;

    protected List<RoleResource> preRoleList;

    protected List<PlexusUserResource> preUserList;

    protected List<RepositoryTargetListResource> preTargetList;

    public AbstractImportSecurityIT()
    {
        // initialize the utils
        userUtil = new PlexusUserMessageUtil();
        repoTargetUtil = new TargetMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
        privilegeUtil = new PrivilegesMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
        roleUtil = new RoleMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
        repoUtil = new RepositoryMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
        groupUtil = new GroupMessageUtil( this, getXMLXStream(), MediaType.APPLICATION_XML );
    }

    abstract protected void importSecurity()
        throws Exception;

    abstract protected void verifySecurity()
        throws Exception;

    @Test
    public void testImportSecurity()
        throws Exception
    {
        loadPreResources();

        importSecurity();

        verifySecurity();
    }

    @SuppressWarnings( "static-access" )
    protected void loadPreResources()
        throws Exception
    {
        // load PREs
        preUserList = userUtil.getList();
        prePrivilegeList =
            privilegeUtil.getResourceListFromResponse( privilegeUtil.sendMessage( Method.GET, null, "" ) );
        preRoleList = roleUtil.getList();
        preTargetList = repoTargetUtil.getList();
    }

    @SuppressWarnings( "static-access" )
    protected List<RepositoryTargetListResource> getImportedRepoTargetList()
        throws Exception
    {
        List<RepositoryTargetListResource> targetList = repoTargetUtil.getList();

        List<RepositoryTargetListResource> addedList = new ArrayList<RepositoryTargetListResource>();

        for ( RepositoryTargetListResource target : targetList )
        {
            if ( !containRepoTarget( preTargetList, target.getId() ) )
            {
                addedList.add( target );
            }
        }
        return addedList;
    }

    protected List<RoleResource> getImportedRoleList()
        throws Exception
    {
        List<RoleResource> roleList = roleUtil.getList();

        List<RoleResource> addedList = new ArrayList<RoleResource>();

        for ( RoleResource role : roleList )
        {
            if ( !containRole( preRoleList, role.getId() ) )
            {
                addedList.add( role );
            }
        }
        return addedList;
    }

    protected List<PrivilegeStatusResource> getImportedTargetPrivilegesList()
        throws Exception
    {
        List<PrivilegeStatusResource> newPrivs = getImportedPrivilegeList();
        List<PrivilegeStatusResource> targetPrivs = new ArrayList<PrivilegeStatusResource>();
        for ( PrivilegeStatusResource priv : newPrivs )
        {
            if ( "target".equals( priv.getType() ) )
            {
                targetPrivs.add( priv );
            }
        }
        return targetPrivs;
    }

    protected List<PrivilegeStatusResource> getImportedPrivilegeList()
        throws Exception
    {
        List<PrivilegeStatusResource> privilegeList =
            privilegeUtil.getResourceListFromResponse( privilegeUtil.sendMessage( Method.GET, null, "" ) );

        List<PrivilegeStatusResource> addedList = new ArrayList<PrivilegeStatusResource>();

        for ( PrivilegeStatusResource privilege : privilegeList )
        {
            if ( !containPrivilege( prePrivilegeList, privilege.getId() ) )
            {
                addedList.add( privilege );
            }
        }
        return Collections.unmodifiableList( addedList );
    }

    protected List<PlexusUserResource> getImportedUserList()
        throws Exception
    {
        List<PlexusUserResource> userList = userUtil.getList();

        List<PlexusUserResource> addedList = new ArrayList<PlexusUserResource>();

        for ( PlexusUserResource user : userList )
        {
            if ( !containUser( preUserList, user.getUserId() ) )
            {
                addedList.add( user );
            }
        }
        return addedList;
    }

    protected boolean containRepoTarget( List<RepositoryTargetListResource> repoTargetList, String repoTargetId )
    {
        for ( RepositoryTargetListResource target : repoTargetList )
        {
            if ( target.getId().equals( repoTargetId ) )
            {
                return true;
            }
        }
        return false;
    }

    protected boolean containRole( List<RoleResource> roleList, String roleId )
    {
        for ( RoleResource role : roleList )
        {
            if ( role.getId().equals( roleId ) )
            {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containRoleStartAndEndWith( List<RoleResource> roleList, String start, String end )
    {
        for ( RoleResource role : roleList )
        {
            if ( role.getId().startsWith( start ) && role.getId().endsWith( end ))
            {
                return true;
            }
        }
        return false;
    }


    protected boolean containPlexusRole( List<PlexusRoleResource> roleList, String roleId )
    {
        for ( PlexusRoleResource role : roleList )
        {
            if ( role.getRoleId().equals( roleId ) )
            {
                return true;
            }
        }
        return false;
    }

    protected boolean containUser( List<PlexusUserResource> userList, String userId )
    {
        for ( PlexusUserResource user : userList )
        {
            if ( user.getUserId().equals( userId ) )
            {
                return true;
            }
        }
        return false;
    }

    protected boolean containPrivilege( List<PrivilegeStatusResource> privList, String privId )
    {
        for ( PrivilegeStatusResource priv : privList )
        {
            if ( priv.getId().equals( privId ) )
            {
                return true;
            }
        }
        return false;
    }

    protected boolean containPrivilegeName( List<PrivilegeStatusResource> privList, String privName )
    {
        for ( PrivilegeStatusResource priv : privList )
        {
            if ( priv.getName().equals( privName ) )
            {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containPrivilegeStartAndEndWith( List<PrivilegeStatusResource> privList, String start, String end )
    {
        for ( PrivilegeStatusResource priv : privList )
        {
            if ( priv.getName().startsWith( start ) && priv.getName().endsWith( end ) )
            {
                return true;
            }
        }
        return false;
    }

    protected PlexusUserResource getUserById( List<PlexusUserResource> userList, String id )
    {
        for ( PlexusUserResource user : userList )
        {
            if ( user.getUserId().equals( id ) )
            {
                return user;
            }
        }
        return null;
    }

    protected RoleResource getRoleById( List<RoleResource> roleList, String id )
    {
        for ( RoleResource role : roleList )
        {
            if ( role.getId().equals( id ) )
            {
                return role;
            }
        }
        return null;
    }

    protected boolean containRoleEndWith( List<PlexusRoleResource> roleList, String suffix )
    {
        for ( PlexusRoleResource role : roleList )
        {
            if ( role.equals( suffix ) )
            {
                return true;
            }
        }
        return false;
    }

}
