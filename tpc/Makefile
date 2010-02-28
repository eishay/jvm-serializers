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

protobuf_generator ?= protoc
thrift_generator ?= thrift
activemqprotobuf_cp := $(subst $(space),:,$(wildcard lib/activemq-*))
activemqprotobuf_generator ?= java -cp "$(activemqprotobuf_cp)" org.apache.activemq.protobuf.compiler.AltJavaGenerator
cks_cp := lib/cks.jar:lib/cakoose-util.jar
cks_generator ?= java -cp "$(cks_cp)" cks.tool.Main

# Default to 'no' because we might not have these tools available.
Generate_protobuf ?= n
Generate_thrift ?= n

# Default to 'yes' because the generator tools are included in 'lib/'.
Generate_activemqprotobuf ?= y
Generate_cks ?= y

##########################################################################

.PHONY: default compile
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

SchemaExtensions := .thrift .proto .tcks
Schemas = $(patsubst schema/%,%,$(wildcard $(SchemaExtensions:%=schema/*%)))

##########################################################################
# Figure out classpath

build/gen-cp: schema
	$(p2)mkdir -p build
	$(p1)echo "$(subst $(space),:,$(Schemas:%=build/bytecode/gen/%))" > build/gen-cp

build/lib-cp: lib
	$(p2)mkdir -p build
	$(p1)echo "$(subst $(space),:,$(wildcard lib/*.jar))" > "$@"

##########################################################################
# Generate and Compile Bindings
# $(1): name, $(2): filename

define EachSchema

ifneq ($(findstring .activemq.proto,$(2)),)
	NeedToGenerate_$(1) := $(Generate_activemqprotobuf)
else ifneq ($(findstring .proto,$(2)),)
	NeedToGenerate_$(1) := $(Generate_protobuf)
else ifneq ($(findstring .thrift,$(2)),)
	NeedToGenerate_$(1) := $(Generate_thrift)
else ifneq ($(findstring .tcks,$(2)),)
	NeedToGenerate_$(1) := $(Generate_cks)
else
endif

ifeq ($$(NeedToGenerate_$(1)),y)

# Generate Binding Files
JavaSourceRoots_$(1) = build/gen/$(2)
StampSuffix_$(1) := gen

build/gen/$(2): schema/$(2)
	# Generate: $(2)
	$(p2)[ ! -e $$@ ] || rm -r $$@
	$(p2)mkdir -p $$@
ifneq ($(findstring .activemq.proto,$(2)),)
	$(p1)$(activemqprotobuf_generator) --out=$$@ $$<
else ifneq ($(findstring .proto,$(2)),)
	$(p1)$(protobuf_generator) --java_out=$$@ $$<
else ifneq ($(findstring .thrift,$(2)),)
	$(p1)$(thrift_generator) -o $$@ --gen java:hashcode,beans $$<
else ifneq ($(findstring .tcks,$(2)),)
	$(p1)$(cks_generator) gen-java $$< serializers.cks.$(basename $(2)) $$@/serializers/cks/$(basename $(2))
else
endif

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
	$(p1)$(JavaC) $(JavaC_Opt) $$(JavaC_Opt_$(2)) -d build/bytecode/gen/$(2) \
		-classpath "$$(shell cat build/lib-cp)" \
		$$(shell find $$(JavaSourceRoots_$(1)) -type f -name '*.java')
	$(p2)touch "$$@"

build/bytecode/gen/$(2).compile: build/bytecode/gen/$(2).compile.$$(StampSuffix_$(1))
	$(p2)touch "$$@"

compile: build/bytecode/gen/$(2).compile

endef

$(foreach s,$(Schemas),$(eval $(call EachSchema,$(subst .,_,$(s)),$(s))))

##########################################################################
# Compile main code

JavaSourceRoots = src
JavaSourceDirs = $(shell find $(JavaSourceRoots) -type d)
JavaSourceFiles = $(shell find $(JavaSourceRoots) -type f -name '*.java')

build/bytecode/main.compile : $(JavaSourceRoots) $(JavaSourceDirs) $(JavaSourceFiles) lib build/lib-cp build/gen-cp $(wildcard lib/*.jar) $(Schemas:%=build/bytecode/gen/%.compile)
	$(p2)[ ! -e build/bytecode/main ] || rm -r build/bytecode/main
	$(p2)mkdir -p build/bytecode/main
ifeq ($(JavaSourceFiles),)
	# Compile: $(JavaSourceRoots) (empty)
else
	# Compile: $(JavaSourceRoots)
	$(p1)$(JavaC) $(JavaC_Opt) -d build/bytecode/main \
		-classpath "$(shell cat build/lib-cp):$(shell cat build/gen-cp)" \
		$(JavaSourceFiles)
	$(p2)touch "$@"
endif

compile: build/bytecode/main.compile

##########################################################################
# Clean
clean:
ifneq ($(wildcard build),)
	# Delete: build/**
	$(p1)-rm -r build
endif
