##########################################################################

-include Config.mk

# Settings you can put in "Config.mk"
#
# - By default, we'll use pregenerated code for the different formats'
#   bindings.  To run the bindings generator each time, do:
#      Generate_protobuf := y
#
# - We have some default names for the bindings generator executables.
#   If your executable is called something else, do:
#      protobuf_generator := protoc

# Need 'javac' version 5 or greater.
JavaC ?= javac
JavaC_Opt := -Xlint:unchecked

empty :=
space := $(empty) $(empty)

# On Unix, ":" (colon), on Windows, ";" (semicolon)
path_sep := $(shell if expr "$$(uname -s)" : 'CYGWIN' >/dev/null ; then printf ';' ; else printf ':' ; fi)

mkcp = $(subst $(space),$(path_sep),$(1))

protobuf_generator ?= protoc
thrift_generator ?= thrift
activemqprotobuf_cp := $(call mkcp,$(wildcard lib/activemq-*.jar))
activemqprotobuf_generator ?= java -cp "$(activemqprotobuf_cp)" org.apache.activemq.protobuf.compiler.AltJavaGenerator
cks_generator ?= java -jar lib/cks-tool.jar
avro_cp ?= $(call mkcp,$(wildcard lib/avro-*.jar lib/jackson-*.jar lib/jopt-simple-*.jar lib/velocity-*.jar lib/slf4j-*.jar))
avro_generator ?= java -cp "$(avro_cp)" org.apache.avro.tool.Main
protostuff_compiler ?= $(wildcard lib/protostuff-compiler-*.jar)
dslplatform_compiler = java -jar lib/dsl-clc.jar
dslplatform_dir = build/dslplatform

# Default to 'no' because we might not have these tools available.
Generate_protobuf ?= n
Generate_thrift ?= n
Generate_dslplatform ?= n # Requires Mono 3.10+

# Default to 'yes' because the generator tools are included in 'lib/'.
Generate_activemqprotobuf ?= y
Generate_cks ?= y
Generate_avro ?= y
Generate_protostuff ?= y

##########################################################################

.PHONY: default compile
.DELETE_ON_ERROR:
default: compile build/gen-cp build/lib-cp


p1 := @
p2 := @
ifeq ($(SHOW),y)
	p1 :=
endif
ifeq ($(SHOW),a)
	p1 :=
	p2 :=
endif

##########################################################################

SchemaExtensions := .thrift .proto .tcks .avro .dsl
Schemas = $(patsubst schema/%,%,$(wildcard $(SchemaExtensions:%=schema/*%)))

##########################################################################
# Figure out classpath

build/gen-cp: schema
	$(p2)mkdir -p build
	$(p1)echo "$(call mkcp,$(Schemas:%=build/bytecode/gen/%))" > build/gen-cp

build/lib-cp: lib
	$(p2)mkdir -p build
	$(p1)echo "$(call mkcp,$(wildcard lib/*.jar))" > "$@"

##########################################################################
# Generate and Compile Bindings
# $(1): name, $(2): filename

define EachSchema

ifneq ($(findstring .activemq.proto,$(2)),)
	NeedToGenerate_$(1) := $(Generate_activemqprotobuf)
else ifneq ($(findstring .protostuff.proto,$(2)),)
	NeedToGenerate_$(1) := $(Generate_protostuff)
else ifneq ($(findstring .proto,$(2)),)
	NeedToGenerate_$(1) := $(Generate_protobuf)
else ifneq ($(findstring .thrift,$(2)),)
	NeedToGenerate_$(1) := $(Generate_thrift)
else ifneq ($(findstring .tcks,$(2)),)
	NeedToGenerate_$(1) := $(Generate_cks)
else ifneq ($(findstring .avro,$(2)),)
	NeedToGenerate_$(1) := $(Generate_avro)
else ifneq ($(findstring .dsl,$(2)),)
	NeedToGenerate_$(1) := $(Generate_dslplatform)
else
endif

ifeq ($$(NeedToGenerate_$(1)),y)

# Generate Binding Files
JavaSourceRoots_$(1) = build/gen/$(2)
StampSuffix_$(1) := gen

build/gen/$(2): build/gen/$(2).gen

build/gen/$(2).gen: schema/$(2)
	# Generate: $(2)
	$(p2)[ ! -e build/gen/$(2) ] || rm -r build/gen/$(2)
	$(p2)mkdir -p build/gen/$(2)
	$(p2)touch $$@
ifneq ($(findstring .activemq.proto,$(2)),)
	$(p1)$(activemqprotobuf_generator) --out=build/gen/$(2) $$<
else ifneq ($(findstring .protostuff.proto,$(2)),)
	$(p1)java -Dsource=$$< -Doutput=java_bean -Doptions=builder_pattern,generate_field_map,separate_schema -DoutputDir=build/gen/$(2) -jar $(protostuff_compiler)
else ifneq ($(findstring .proto,$(2)),)
	$(p1)$(protobuf_generator) --java_out=build/gen/$(2) $$<
else ifneq ($(findstring .thrift,$(2)),)
	$(p1)$(thrift_generator) -o build/gen/$(2) --gen java:hashcode,beans $$<
else ifneq ($(findstring .tcks,$(2)),)
	$(p1)$(cks_generator) gen-java $$< serializers.cks.$(basename $(2)) build/gen/$(2)/serializers/cks/$(basename $(2))
else ifneq ($(findstring .avro,$(2)),)
	$(p1)$(avro_generator) compile schema $$< build/gen/$(2)
else ifneq ($(findstring .dsl,$(2)),)
	$(p1)mkdir -p $(dslplatform_dir)/tmp
	$(p1)$(dslplatform_compiler) \
		temp=$(dslplatform_dir)/tmp \
		dsl=schema \
		manual-json \
		namespace=serializers.dslplatform \
		java_pojo \
		no-prompt \
		force \
		source-only \
		download
	$(p1)cp -r $(dslplatform_dir)/tmp/JAVA_POJO/serializers build/gen/$(2)
else
endif

build/bytecode/gen/$(2).compile.gen: build/gen/$(2).gen

else

# Use Pre-Generated Binding Files
JavaSourceRoots_$(1) = pregen/$(2)
StampSuffix_$(1) := pregen
JavaSourceDirs_$(1) = $$(shell find $$(JavaSourceRoots_$(1)) -type d)
JavaSourceFiles_$(1) = $$(shell find $$(JavaSourceRoots_$(1)) -type f -name '*.java')

endif

# Compile to bytecode
build/bytecode/gen/$(2).compile.$$(StampSuffix_$(1)): $$(JavaSourceRoots_$(1)) $$(JavaSourceDirs_$(1)) $$(JavaSourceFiles_$(1)) lib build/lib-cp $(wildcard lib/*.jar)
	$(p2)[ ! -e build/bytecode/gen/$(2) ] || rm -r build/bytecode/gen/$(2)
	$(p2)mkdir -p build/bytecode/gen/$(2)
	$(p2)rm -f build/bytecode/gen/$(2).compile.*
	# Compile: $$(JavaSourceRoots_$(1))
	$(p2)touch "$$@"
	$(p1)$(JavaC) $(JavaC_Opt) $$(JavaC_Opt_$(2)) -d build/bytecode/gen/$(2) \
		-classpath "$$(shell cat build/lib-cp)" \
		$$(shell find $$(JavaSourceRoots_$(1)) -type f -name '*.java')

build/bytecode/gen/$(2).compile: build/bytecode/gen/$(2).compile.$$(StampSuffix_$(1))
	$(p2)touch "$$@"

build/bytecode/main.compile: build/bytecode/gen/$(2).compile

endef

$(foreach s,$(Schemas),$(eval $(call EachSchema,$(subst .,_,$(s)),$(s))))

##########################################################################
# Compile main code

JavaSourceRoots = src
JavaSourceDirs = $(shell find $(JavaSourceRoots) -type d)
JavaSourceFiles = $(shell find $(JavaSourceRoots) -type f -name '*.java')
ScalaSourceFiles = $(shell find $(JavaSourceRoots) -type f -name '*.scala')

build/bytecode/main.compile: $(JavaSourceRoots) $(JavaSourceDirs) $(JavaSourceFiles) $(ScalaSourceFiles) lib build/lib-cp build/gen-cp $(wildcard lib/*.jar) $(Schemas:%=build/bytecode/gen/%.compile)
	$(p2)[ ! -e build/bytecode/main ] || rm -r build/bytecode/main
	$(p2)mkdir -p build/bytecode/main
	$(p2)touch "$@"
ifeq ($(JavaSourceFiles),)
	# Compile: $(JavaSourceRoots) (empty)
else
	# Compile: $(JavaSourceRoots) [scalac]
	$(p1)support/scala/scalac -d build/bytecode/main \
		-deprecation \
		-cp "$(call mkcp,$(shell cat build/lib-cp) $(shell cat build/gen-cp))" \
		$(ScalaSourceFiles) $(JavaSourceFiles)
	# Compile: $(JavaSourceRoots) [javac]
	$(p1)$(JavaC) $(JavaC_Opt) -d build/bytecode/main \
		-deprecation \
		-classpath "$(call mkcp,build/bytecode/main $(shell cat build/lib-cp) $(shell cat build/gen-cp))" \
		$(JavaSourceFiles)
	# Copy resources
	$(p2)cp $(JavaSourceRoots)/log4j.xml build/bytecode/main
endif

compile: build/bytecode/main.compile

##########################################################################
# Clean
clean:
ifneq ($(wildcard build),)
	# Delete: build/**
	$(p1)-rm -r build
endif

