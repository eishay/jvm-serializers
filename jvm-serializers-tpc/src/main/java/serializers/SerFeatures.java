package serializers;

import java.util.EnumSet;
import java.util.Formatter;

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
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class SerFeatures {
    SerFormat format = SerFormat.MISC;
    SerGraph graph = SerGraph.UNKNOWN;
    SerClass clz = SerClass.MISC;
    // FIXME: should have used one EnumSet from the beginning so we could get rid of 
    // SerFormat,SerGraph,SerClass .. sigh. Too lazy to change this now :-). ruediger
    EnumSet<MiscFeatures> miscFeatures = EnumSet.noneOf(MiscFeatures.class);
    String description;

    public SerFeatures() {
    }

    public SerFeatures(SerFormat format, SerGraph graph, SerClass clz) {
        this.format = format;
        this.graph = graph;
        this.clz = clz;
    }

    public SerFeatures(SerFormat format, SerGraph graph, SerClass clz, String description) {
        this.format = format;
        this.graph = graph;
        this.clz = clz;
        this.description = description;
    }

    public SerFeatures(SerFormat format, SerGraph graph, SerClass clz, String description, EnumSet<MiscFeatures> features) {
        this.format = format;
        this.graph = graph;
        this.clz = clz;
        this.miscFeatures = features;
        this.description = description;
    }

    public EnumSet<MiscFeatures> getMiscFeatures() {
        return miscFeatures;
    }

    public void setMiscFeatures(EnumSet<MiscFeatures> miscFeatures) {
        this.miscFeatures = miscFeatures;
    }

    public String toString(String name) {
        Formatter format = new Formatter().format(
                "%-34s %-15s %-14s %-10s %-60s",
                name,
                getClz(),
                getFormat(),
                getGraph(),
                getMiscFeatures()+" "+getDescription()
        );
        return format.toString();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SerFormat getFormat() {
        return format;
    }

    public void setFormat(SerFormat format) {
        this.format = format;
    }

    public SerGraph getGraph() {
        return graph;
    }

    public void setGraph(SerGraph graph) {
        this.graph = graph;
    }

    public SerClass getClz() {
        return clz;
    }

    public void setClz(SerClass clz) {
        this.clz = clz;
    }
}
