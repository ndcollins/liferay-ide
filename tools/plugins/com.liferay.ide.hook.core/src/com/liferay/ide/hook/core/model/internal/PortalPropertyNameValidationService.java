/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class PortalPropertyNameValidationService extends ValidationService
{

    private List<String> hookPropertiesNames;

    @Override
    protected void init()
    {
        super.init();
        final IProject project = context( IModelElement.class ).root().adapt( IFile.class ).getProject();

        final ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject != null )
        {
            this.hookPropertiesNames = Arrays.asList( liferayProject.getHookSupportedProperties() );
        }
    }

    private boolean isValidPortalPropertyName( Value<?> value )
    {
        return hookPropertiesNames.contains( value.getContent() );
    }

    private boolean isValueEmpty( Value<?> value )
    {
        return value.getContent( false ) == null;
    }

    @Override
    public Status validate()
    {
        final Value<?> value = (Value<?>) context( IModelElement.class ).read( context( ModelProperty.class ) );
        final ValueProperty property = value.getProperty();
        final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );

        if( isValueEmpty( value ) )
        {
            final String msg = NLS.bind( Msgs.nonEmptyValueRequired, label );
            return Status.createErrorStatus( msg );
        }
        else if( !isValidPortalPropertyName( value ) )
        {
            final String msg = NLS.bind( Msgs.invalidPortalPropertyName, label );
            return Status.createErrorStatus( msg );
        }

        return Status.createOkStatus();
    }

    private static class Msgs extends NLS
    {
        public static String invalidPortalPropertyName;
        public static String nonEmptyValueRequired;

        static
        {
            initializeMessages( PortalPropertyNameValidationService.class.getName(), Msgs.class );
        }
    }
}
