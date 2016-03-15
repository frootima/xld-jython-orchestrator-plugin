/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.community.orchestrator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.xebialabs.deployit.engine.spi.orchestration.Orchestrator;
import com.xlabs.xldeploy.plugin.jythonorchestrator.BaseJythonOrchestrator;
import java.net.URL;

public class OrchestratorLoader {
	
	public synchronized static void load(){
		Properties orchestratorDefinitions = null;
		orchestratorDefinitions = loadProperties("plugin-jython-orchestrators.properties");
		logger.info("Jython orchestrator definitions loaded from plugin");
		logger.info("Checking file ext/jython-orchestrator.properties...");
		File fProperties = new File("ext/jython-orchestrators.properties");
		if (fProperties.exists() && fProperties.isFile()){
			Properties orchestratorDefinitionsFs = loadFsProperties("ext/jython-orchestrators.properties");
			logger.info("Jython listener definitions loaded from filesystem");
			orchestratorDefinitions.putAll(orchestratorDefinitionsFs);
		}			
		if (orchestratorDefinitions.size()==0){
			logger.info("Jython listener definitions is empty, no orchestration registration to perform");
			return;
		}
		logger.info("Using Jython orchestrator properties "+orchestratorDefinitions.toString());
		for (Entry<Object, Object> entry: orchestratorDefinitions.entrySet()){
			if (!entry.getKey().toString().endsWith(".scriptClassPath")){
				try{
					String key = entry.getKey().toString();
					String value = entry.getValue().toString();
					String scriptClasspath=orchestratorDefinitions.getProperty(entry.getKey().toString()+".scriptClassPath", "");
					logger.info("Registering Jython orchestrator {} ({})",key,value);
					registerOrchestrator(key,value,scriptClasspath); 
				}	
				catch (Throwable t){
					logger.error(t.getMessage(), t);
				}
			}
		}
	}	
	
	
	private static void registerOrchestrator(String name, String scriptName, String scriptClasspath){
		Field f;
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName("com.xebialabs.deployit.deployment.orchestrator.OrchestratorRegistry");
			f = c.getDeclaredField("lookup");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, Orchestrator>  lookup = (Map<String, Orchestrator> ) f.get(c);
			lookup.put(name, new BaseJythonOrchestrator(name, scriptName, scriptClasspath));
			logger.debug("Lookup field is now : "+lookup.toString());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	protected static Properties loadProperties(String fileName){
		Properties props  = new Properties();
		InputStream is    = null;
		try {
			URL u = Resources.getResource(fileName);
			is = u.openStream();
			props.load(is);
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		finally{
			if (is!=null){
				try{
				  is.close();	
				}
				catch (IOException e){
					logger.error(e.getMessage(), e);
				}
			}
		}
		return props;
	}
	
	protected static Properties loadFsProperties(String fileName){
		Properties props  = new Properties();
		FileInputStream input = null;
		try {
			input = new FileInputStream(fileName);
			props.load(input);
			
		} catch (IOException e1) {
			logger.warn("Cannot locate script on filesystem "+fileName);
		}
		finally {
			if (input!=null){
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return props;
	}
		
	
	protected static final Logger logger = LoggerFactory.getLogger(OrchestratorLoader.class);
}
