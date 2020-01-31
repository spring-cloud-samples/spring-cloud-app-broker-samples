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

import reactor.core.publisher.Mono;

import org.springframework.cloud.appbroker.state.ServiceInstanceState;
import org.springframework.cloud.appbroker.state.ServiceInstanceStateRepository;
import org.springframework.cloud.servicebroker.model.instance.OperationState;

class DefaultServiceInstanceStateRepository implements ServiceInstanceStateRepository {

	private final ServiceInstanceRepository serviceInstanceRepository;

	DefaultServiceInstanceStateRepository(ServiceInstanceRepository serviceInstanceRepository) {
		this.serviceInstanceRepository = serviceInstanceRepository;
	}

	@Override
	public Mono<ServiceInstanceState> saveState(String serviceInstanceId, OperationState state, String description) {
		return serviceInstanceRepository.findByServiceInstanceId(serviceInstanceId)
			.switchIfEmpty(Mono.just(new ServiceInstance()))
			.flatMap(serviceInstance -> {
				serviceInstance.setServiceInstanceId(serviceInstanceId);
				serviceInstance.setOperationState(state);
				serviceInstance.setDescription(description);
				return Mono.just(serviceInstance);
			})
			.flatMap(serviceInstanceRepository::save)
			.map(DefaultServiceInstanceStateRepository::toServiceInstanceState);
	}

	@Override
	public Mono<ServiceInstanceState> getState(String serviceInstanceId) {
		return serviceInstanceRepository.findByServiceInstanceId(serviceInstanceId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown service instance ID " + serviceInstanceId)))
			.map(DefaultServiceInstanceStateRepository::toServiceInstanceState);
	}

	@Override
	public Mono<ServiceInstanceState> removeState(String serviceInstanceId) {
		return getState(serviceInstanceId)
			.doOnNext(serviceInstanceState -> serviceInstanceRepository.deleteByServiceInstanceId(serviceInstanceId));
	}

	private static ServiceInstanceState toServiceInstanceState(ServiceInstance serviceInstance) {
		return new ServiceInstanceState(serviceInstance.getOperationState(), serviceInstance.getDescription(), null);
	}

}
