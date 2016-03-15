# Custom Jython Orchestrator Plugin #

# Overview #

This plugin allows orchestrator definitions with Jython scripts. It uses the XLDeploy Orchestration API.

The plugin comes with 3 embedded Jython orchestrator examples : 


- jython-default : this is the Orchestrator used to bootstrap the Jython orchestrators. It generates a simple interleaved plan.
- jython-sequential-by-container : a sequential by container orchestrator
- jython-parallel-by-container : a parallel by container orchestrator


Note : the Jython default orchestrator cannot be removed as it is used as a bootstrap. However you can rename it (see Orchestration annotation) and change the .py file to implement (one of) your orchestrator(s).

# Requirements #

* **XLDeploy requirements**
	* **XLDeploy**: version 5.1.0+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Example Orchestration #


The jython-default example is the following:

```
	#default jython orchestrator
	from  com.xebialabs.deployit.engine.spi.orchestration import Orchestrations;
	logger.info("jython-default begin")
	logger.info("orchestratorScriptName %s"%orchestratorScriptName)
	result = Orchestrations.interleaved(planLabel, deltasContainer)
	orchestration.setOrchestration(result)
	logger.info("jython-default end")
```
	
The parallel-by-container example is the following:	
```
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
```


	
	
## Variables injected ##

The following variables are injected in the Jython scripts:

- logger  : an org.slf4j.Logger
- specification : a DeltaSpecification 
- deltas : shortcut to spec.getDeltas()
- planLabel : a String containing a predefined planLabel
- orchestration : an OrchestrationWrapper. use orchestration.setOrchestration() to return the orchestration
- orchestratorName : String the orchestrator name
- orchestratorScriptName: String the orchestrator script name


## Extending ##
You can add a properties file to your /ext directory, which must be named jython-orchestrators.properties
Use this to define additional orchestrator in the ext directory :

jython-test=jython-orchestrators/sequentialByContainer.py
 
		
