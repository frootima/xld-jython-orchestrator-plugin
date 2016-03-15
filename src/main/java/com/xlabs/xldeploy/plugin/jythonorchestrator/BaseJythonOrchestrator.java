/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xlabs.xldeploy.plugin.jythonorchestrator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.engine.spi.exception.DeployitException;
import com.xebialabs.deployit.engine.spi.orchestration.Orchestration;
import com.xebialabs.deployit.engine.spi.orchestration.Orchestrator;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.community.orchestrator.OrchestrationWrapper;
import com.xebialabs.deployit.plugin.community.orchestrator.ScriptRunner;


@Orchestrator.Metadata(name = "not-used")
public class BaseJythonOrchestrator implements Orchestrator {
	
	private String name;
	private String scriptName;
	private String scriptClasspath;
	
	public BaseJythonOrchestrator(String name, String scriptName, String scriptClasspath){
		this.name = name;
		this.scriptName = scriptName;
		this.scriptClasspath = scriptClasspath;
	}

	public static String getOperationLabel(DeltaSpecification spec){
		if (spec.getOperation().name().equalsIgnoreCase(spec.getOperation().CREATE.name())){
			return "Deploy";
		}
		if (spec.getOperation().name().equalsIgnoreCase(spec.getOperation().MODIFY.name())){
			return "Update";
		}
		if (spec.getOperation().name().equalsIgnoreCase(spec.getOperation().DESTROY.name())){
			return "Undeploy";
		}
		return spec.getOperation().name();
	}
	
	public static String getPlanLabel(DeltaSpecification spec){
		String opLabel = getOperationLabel(spec);
		String verb    = "on";
		if (opLabel.equalsIgnoreCase("Deploy")){
			verb = "to";
		}
		if (opLabel.equalsIgnoreCase("Undeploy")){
			verb = "from";
		}
		String label = String.format("%s %s %s %s environment %s", opLabel, spec.getDeployedApplication().getName(), spec.getDeployedApplication().getVersion().getName(), verb, spec.getDeployedApplication().getEnvironment().getName());
		return label;
	}
	
	
	
	public String getName() {
		return name;
	}

	public String getScriptName() {
		return scriptName;
	}

	public String getScriptClasspath() {
		return scriptClasspath;
	}

	public Orchestration orchestrate(DeltaSpecification spec) {
		List<Delta> deltas = new ArrayList<Delta>(spec.getDeltas());
		String planLabel = getPlanLabel(spec);
		OrchestrationWrapper wrapper = new OrchestrationWrapper();
		try{
			ScriptRunner.executeScript(wrapper, spec, deltas, planLabel, scriptName, scriptClasspath, spec.getDeployedApplication().getId(), name);
			if (wrapper.getOrchestration()==null){
				throw new DeployitException("Empty orchestration for "+scriptName);
			}
			return wrapper.getOrchestration();
		}
		catch (DeployitException dex){
			logger.error("Script execution error :",dex.getMessage());
			throw dex;
		}
		catch (Throwable t){
			logger.error("Script execution error :",t);
			throw new DeployitException(t);
		}
		
	}
		
	protected static final Logger logger = LoggerFactory.getLogger(BaseJythonOrchestrator.class);
	

}