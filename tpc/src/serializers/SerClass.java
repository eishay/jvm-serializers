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
 * Date: 09.03.14
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public enum SerClass {
    /**
     * nothing must be known in advance or language built-in (e.g. JDK Serializable)
     */
    ZERO_KNOWLEDGE,
    /**
     * requires knowledge in advance which classes can be serialized. Preconfiguration/Code Generation required
     */
    CLASSES_KNOWN,
    /**
     * requires knowledge in advance which classes can be serialized. Preconfiguration/Code Generation required
     * additionally field specific manually written read/write code 
     */
    MANUAL_OPT,
    /**
     * add new category if you fall into this :-)
     */
    MISC
}
