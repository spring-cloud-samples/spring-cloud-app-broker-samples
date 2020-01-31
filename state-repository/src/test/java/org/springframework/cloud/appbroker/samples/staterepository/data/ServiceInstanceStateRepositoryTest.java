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

package org.springframework.cloud.appbroker.samples.staterepository.data;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.model.instance.OperationState;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServiceInstanceStateRepositoryTest {

	@Autowired
	private DefaultServiceInstanceStateRepository defaultServiceInstanceStateRepository;

	@Test
	void getState() {
		defaultServiceInstanceStateRepository.getState("1")
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void getStateInstanceNotFound() {
		defaultServiceInstanceStateRepository.getState("abc123")
				.as(StepVerifier::create)
				.verifyErrorMessage("Unknown service instance ID abc123");
	}

	@Test
	void saveState() {
		defaultServiceInstanceStateRepository.saveState("1", OperationState.IN_PROGRESS, "let's do this")
				.as(StepVerifier::create)
				.consumeNextWith(serviceInstanceState -> {
					assertThat(serviceInstanceState.getOperationState()).isEqualTo(OperationState.IN_PROGRESS);
					assertThat(serviceInstanceState.getDescription()).isEqualTo("let's do this");
				})
				.verifyComplete();
	}

	@Test
	void saveStateInstanceNotFound() {
		defaultServiceInstanceStateRepository.saveState("2", OperationState.FAILED, "let's do this")
				.as(StepVerifier::create)
				.consumeNextWith(serviceInstanceState -> {
					assertThat(serviceInstanceState.getOperationState()).isEqualTo(OperationState.FAILED);
					assertThat(serviceInstanceState.getDescription()).isEqualTo("let's do this");
				})
				.verifyComplete();
	}

	@Test
	void removeState() {
		defaultServiceInstanceStateRepository.saveState("99", OperationState.IN_PROGRESS, null)
				.then(defaultServiceInstanceStateRepository.removeState("99"))
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void removeStateInstanceNotFound() {
		defaultServiceInstanceStateRepository.removeState("999")
				.as(StepVerifier::create)
				.verifyErrorMessage("Unknown service instance ID 999");
	}

}
