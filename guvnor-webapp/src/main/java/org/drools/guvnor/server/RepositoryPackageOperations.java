/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

/**
 * Handles operations for packages
 */
@Name("org.drools.guvnor.server.RepositoryPackageOperations")
@AutoCreate
public class RepositoryPackageOperations {
    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper
                                                   .getLogger( RepositoryPackageOperations.class );

    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    protected PackageConfigData[] listPackages(boolean archive,
                                               String workspace,
                                               RepositoryFilter filter) {
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        PackageIterator pkgs = getRulesRepository().listPackages();
        handleIteratePackages( archive,
                               workspace,
                               filter,
                               result,
                               pkgs );

        sortPackages( result );
        return result.toArray( new PackageConfigData[result.size()] );
    }

    private void handleIteratePackages(boolean archive,
                                       String workspace,
                                       RepositoryFilter filter,
                                       List<PackageConfigData> result,
                                       PackageIterator pkgs) {
        pkgs.setArchivedIterator( archive );
        while ( pkgs.hasNext() ) {
            PackageItem pkg = pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.uuid = pkg.getUUID();
            data.name = pkg.getName();
            data.archived = pkg.isArchived();
            data.workspaces = pkg.getWorkspaces();
            handleIsPackagesListed( archive,
                                    workspace,
                                    filter,
                                    result,
                                    data );

            data.subPackages = listSubPackages( pkg,
                                                archive,
                                                null,
                                                filter );
        }
    }

    private PackageConfigData[] listSubPackages(PackageItem parentPkg,
                                                boolean archive,
                                                String workspace,
                                                RepositoryFilter filter) {
        List<PackageConfigData> children = new LinkedList<PackageConfigData>();

        PackageIterator pkgs = parentPkg.listSubPackages();
        handleIteratePackages( archive,
                               workspace,
                               filter,
                               children,
                               pkgs );

        sortPackages( children );
        return children.toArray( new PackageConfigData[children.size()] );
    }

    void sortPackages(List<PackageConfigData> result) {
        Collections.sort( result,
                          new Comparator<PackageConfigData>() {

                              public int compare(final PackageConfigData d1,
                                                 final PackageConfigData d2) {
                                  return d1.name.compareTo( d2.name );
                              }

                          } );
    }

    private void handleIsPackagesListed(boolean archive,
                                        String workspace,
                                        RepositoryFilter filter,
                                        List<PackageConfigData> result,
                                        PackageConfigData data) {
        if ( !archive && (filter == null || filter.accept( data,
                                                           RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                               data.workspaces )) ) {
            result.add( data );
        } else if ( archive && data.archived && (filter == null || filter.accept( data,
                                                                                  RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                                                      data.workspaces )) ) {
            result.add( data );
        }
    }

    private boolean isWorkspace(String workspace,
                                String[] workspaces) {
        for ( String w : workspaces ) {
            if ( w.equals( workspace ) ) {
                return true;
            }
        }
        return false;
    }

}