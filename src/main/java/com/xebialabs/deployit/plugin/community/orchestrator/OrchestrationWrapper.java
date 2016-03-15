/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.community.orchestrator;

import com.xebialabs.deployit.engine.spi.orchestration.Orchestration;

public class OrchestrationWrapper {
	private Orchestration orchestration;

	public Orchestration getOrchestration() {
		return orchestration;
	}

	public void setOrchestration(Orchestration orchestration) {
		this.orchestration = orchestration;
	}
	
	
}
