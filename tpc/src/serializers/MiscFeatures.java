package serializers;

/**
 * Copyright (c) 2012, Ruediger Moeller. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p/>
 * Date: 11.03.14
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */

/**
 * describes misc miscFeatures a serializer might have. 
 */
public enum MiscFeatures {
    VERSIONING_BACKWARD_COMPATIBLE,
    VERSIONING_FORWARD_COMPATIBLE,
    VERSIONING_MISMATCH_DETECTION,
    CONTAINS_SCHEMA,
    OPTIMIZATION_BY_ANNOTATION
}
