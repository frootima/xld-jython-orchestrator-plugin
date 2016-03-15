#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

#default jython orchestrator

from  com.xebialabs.deployit.engine.spi.orchestration import Orchestrations;

logger.info("jython-default begin")
logger.info("orchestratorScriptName %s"%orchestratorScriptName)

result = Orchestrations.interleaved(planLabel, deltas)
orchestration.setOrchestration(result)

logger.info("jython-default end")