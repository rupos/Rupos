#!/bin/sh

PROM_ORIGIN=../prom6_origin/ProM
PROM_ORIGIN_BASE=../prom6_origin

CPATH=./bindist/
CPATH=$CPATH:$PROM_ORIGIN/dist/ProM-Contexts.jar
CPATH=$CPATH:$PROM_ORIGIN/dist/ProM-Framework.jar
CPATH=$CPATH:$PROM_ORIGIN/dist/ProM-Models.jar
# CPATH=$CPATH:$PROM_ORIGIN/dist/ProM-Plugins.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/axis.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/bsh-2.0b4.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/collections-generic-4.01.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/colt.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/commons-compress-1.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/commons-math-1.2.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/FilterableSortableTablePanel.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/flanagan.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jargs.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jcommon-1.0.16.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jfreechart-1.0.13.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jgraph.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jlfgr-1_0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jung-algorithms-2.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jung-api-2.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jung-graph-impl-2.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jung-io-2.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/jung-visualization-2.0.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/OpenXES.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/OpenXES-XStream.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/simmetrics.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/slickerbox1.0rc1.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/Spex.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/TableLayout-20050920.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/Uitopia.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/UITopiaResources.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/weka.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/xpp3-1.1.4c.jar
CPATH=$CPATH:$PROM_ORIGIN/lib/xstream-1.3.1.jar

CPATH=$CPATH:$PROM_ORIGIN_BASE/Log/dist/LogFilters.jar

# CPATH=$CPATH:../Packages/OSService/dist/OSService.jar
# CPATH=$CPATH:../Packages/TransitionSystems/dist/TransitionSystems.jar
# CPATH=$CPATH:../Packages/PetriNets/dist/PetriNets.jar
# CPATH=$CPATH:../Packages/LogAbstractions/dist/LogAbstractions.jar
# CPATH=$CPATH:../Packages/AlphaMiner/dist/AlphaMiner.jar

# CPATH=$CPATH:../Packages/ETConformance/lib/javailp-1.1.jar
# CPATH=$CPATH:../Packages/ETConformance/dist/ETConformance.jar

CPATH=$CPATH:$PROM_ORIGIN_BASE/LogDialog/dist/LogDialog.jar
# CPATH=$CPATH:../Packages/PetriNetReplayer/dist/PetriNetReplayer.jar

# CPATH=$CPATH:../Packages/Performance/dist/Performance.jar

java -cp $CPATH org.processmining.contexts.uitopia.UI