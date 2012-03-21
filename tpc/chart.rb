#!/usr/bin/env ruby

# This script produces the equivalent charts using the data output by running
# the benchmark. Just add your output lines to the end of this file and run it.
# Take a look below for an example...

require 'uri'

TITLES = [ 
    "create (nanos)",
    "ser (nanos)",
    "ser+same (nanos)",
    "deser (nanos)",
    "deser+shal (nanos)",
    "deser+deep (nanos)",
    "total (nanos)",
    "size (bytes)",
    "size+dfl (bytes)"
]

data = {}
names = []

name_str = ""
DATA.readlines.each do |x|
    x.chomp!
    next if x =~ /^#/ or x.empty?
    y = x.split
    name = y.shift
    data[name] = y.collect { |v| v.to_i }
    names << name
    name_str = "#{name}|#{name_str}"
end

name_str = name_str[0..-2]
name_str = URI.encode_www_form_component(name_str)

idx = 0
width = 500
height = names.size * 20 + 30
bar_thickness = 10
bar_spacing = 10
TITLES.each do |title|
    max = 0

    val_str = ""
    names.each do |n|
        max = data[n][idx] if data[n][idx] > max 
        val_str += "#{data[n][idx]},"
    end

    val_str = val_str[0..-2]
    scale = max * 1.1

    puts "<img src='https://chart.googleapis.com/chart?chtt=#{URI.encode_www_form_component(title)}&chf=c||lg||0||FFFFFF||1||76A4FB||0|bg||s||EFEFEF&chs=#{width}x#{height}&chd=t:#{val_str}&chds=0,#{scale}&chxt=y&chxl=0:|#{name_str}&chm=N *f*,000000,0,-1,10&lklk&chdlp=t&chco=660000|660033|660066|660099|6600CC|6600FF|663300|663333|663366|663399|6633CC|6633FF|666600|666633|666666&cht=bhg&chbh=#{bar_thickness},0,#{bar_spacing}&nonsense=aaa.png'/>"

    idx += 1
end


__END__
#                                create     ser   +same   deser   +shal   +deep   total   size  +dfl
java-built-in                       213   11785   10656   50080   50026   50766   62551    889   514
bson/mongodb                        138    7815    7829   30058   30484   30622   38437    495   278
avro                                173    4380    4158    2058    2815    3839    8219    221   133
protobuf                            450    4197    1874    1697    1742    2253    6449    239   149
