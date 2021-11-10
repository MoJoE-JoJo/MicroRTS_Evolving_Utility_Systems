/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Philip Tucker on Jun 4, 2003
 */
package com.anji.nn;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerated type representing flavors of neurons: input, output, hidden. Values returned in
 * <code>toString()</code> correspond to values in <a href="http://nevt.sourceforge.net/">NEVT
 * </a> XML data model.
 * 
 * @author Philip Tucker
 */
public class ActivationFunctionType {

/**
 * for hibernate
 */
private Long id;

private String name = null;

private static Map types = null;

/**
 * linear
 */
public final static ActivationFunctionType LINEAR = new ActivationFunctionType( "linear" );

/**
 * sigmoid
 */
public final static ActivationFunctionType SIGMOID = new ActivationFunctionType( "sigmoid" );

/**
 * tanh
 */
public final static ActivationFunctionType TANH = new ActivationFunctionType( "tanh" );

/**
 * tanh cubic
 */
public final static ActivationFunctionType TANH_CUBIC = new ActivationFunctionType(
		"tanh-cubic" );

/**
 * clamped linear
 */
public final static ActivationFunctionType CLAMPED_LINEAR = new ActivationFunctionType(
		"clamped-linear" );

/**
 * signed clamped linear
 */
public final static ActivationFunctionType SIGNED_CLAMPED_LINEAR = new ActivationFunctionType(
		"signed-clamped-linear" );


// TODO === OWN FUNCTIONS! ===

public final static ActivationFunctionType DIVIDE = new ActivationFunctionType("DIVIDE");
public final static ActivationFunctionType MULTIPLY = new ActivationFunctionType("MULTIPLY");
public final static ActivationFunctionType SUM = new ActivationFunctionType("SUM");
public final static ActivationFunctionType SUBTRACT = new ActivationFunctionType("SUBTRACT");
public final static ActivationFunctionType MIN = new ActivationFunctionType("MIN");
public final static ActivationFunctionType MAX = new ActivationFunctionType("MAX");
public final static ActivationFunctionType POWER = new ActivationFunctionType("MAX");


/**
 * @param newName id of type
 */
private ActivationFunctionType( String newName ) {
	name = newName;
}

/**
 * @param name id of type
 * @return <code>ActivationFunctionType</code> enumerated type corresponding to
 * <code>name</code>
 */
public static ActivationFunctionType valueOf( String name ) {
	if ( types == null ) {
		types = new HashMap();
		types.put( ActivationFunctionType.LINEAR.toString(), ActivationFunctionType.LINEAR );
		types.put( ActivationFunctionType.SIGMOID.toString(), ActivationFunctionType.SIGMOID );
		types.put( ActivationFunctionType.TANH.toString(), ActivationFunctionType.TANH );
		types.put( ActivationFunctionType.TANH_CUBIC.toString(), ActivationFunctionType.TANH_CUBIC );
		types.put( ActivationFunctionType.CLAMPED_LINEAR.toString(),
				ActivationFunctionType.CLAMPED_LINEAR );
		types.put( ActivationFunctionType.SIGNED_CLAMPED_LINEAR.toString(),
				ActivationFunctionType.SIGNED_CLAMPED_LINEAR );
		// === OWN FUNCTIONS ===
		types.put( ActivationFunctionType.DIVIDE.toString(), ActivationFunctionType.DIVIDE );
		types.put( ActivationFunctionType.MULTIPLY.toString(), ActivationFunctionType.MULTIPLY );
		types.put( ActivationFunctionType.SUM.toString(), ActivationFunctionType.SUM );
		types.put( ActivationFunctionType.SUBTRACT.toString(), ActivationFunctionType.SUBTRACT );
		types.put( ActivationFunctionType.MIN.toString(), ActivationFunctionType.MIN );
		types.put( ActivationFunctionType.MAX.toString(), ActivationFunctionType.MAX );
		types.put( ActivationFunctionType.POWER.toString(), ActivationFunctionType.POWER );
	}
	return (ActivationFunctionType) types.get( name );
}

/**
 * @see Object#equals(java.lang.Object)
 */
public boolean equals( Object o ) {
	return ( this == o );
}

/**
 * @see Object#toString()
 */
public String toString() {
	return name;
}

/**
 * define this so objects may be used in hash tables
 * 
 * @see Object#hashCode()
 */
public int hashCode() {
	return name.hashCode();
}

/**
 * for hibernate
 * @return unique id
 */
private Long getId() {
	return id;
}

/**
 * for hibernate
 * @param aId
 */
private void setId( Long aId ) {
	id = aId;
}
}
