#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

#Example : orchestration parallel by container

from  com.xebialabs.deployit.engine.spi.orchestration import Orchestrations;

def deployedOrPrevious(delta):
	return delta.getDeployed() if  delta.getDeployed() else delta.getPrevious()

def to_container(delta):
	return deployedOrPrevious(delta).getContainer()

def containers():
	return set(map(to_container, deltas))

def deltasForContainer(container):
	return filter( lambda delta: deployedOrPrevious(delta).getContainer().getId() == container.id, deltas)


logger.info("parallelByContainer.py begin")
orchestrations = []
logger.info("Deltas %s"%deltas)
sortedContainers = sorted(containers(), key=lambda container: container.name)
for container in sortedContainers:
	deltasContainer = deltasForContainer(container)
	orchestrations.append(Orchestrations.interleaved("Deploy to container %s"%container.name, deltasContainer))


orchestration.setOrchestration(Orchestrations.parallel(planLabel, orchestrations))

logger.info("orchestratorName %s"%orchestratorName)
logger.info("orchestratorScriptName %s"%orchestratorScriptName)
logger.info("planlabel %s"%planLabel)
logger.info("parallelByContainer.py end")