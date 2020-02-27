/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.appbroker.samples.targetservice;

import java.util.Map;

import org.springframework.cloud.appbroker.deployer.DeploymentProperties;
import org.springframework.cloud.appbroker.extensions.targets.ArtifactDetails;
import org.springframework.cloud.appbroker.extensions.targets.Target;
import org.springframework.cloud.appbroker.extensions.targets.TargetFactory;

public class CustomSpaceTarget extends TargetFactory<CustomSpaceTarget.Config> {

	private final CustomSpaceService customSpaceService;

	public CustomSpaceTarget(CustomSpaceService customSpaceService) {
		super(Config.class);
		this.customSpaceService = customSpaceService;
	}

	@Override
	public Target create(Config config) {
		return this::apply;
	}

	private ArtifactDetails apply(Map<String, String> properties, String name, String serviceInstanceId) {
		String space = customSpaceService.retrieveSpaceName();
		properties.put(DeploymentProperties.TARGET_PROPERTY_KEY, space);

		return ArtifactDetails.builder()
			.name(name)
			.properties(properties)
			.build();
	}

	public static class Config {
	}

}
