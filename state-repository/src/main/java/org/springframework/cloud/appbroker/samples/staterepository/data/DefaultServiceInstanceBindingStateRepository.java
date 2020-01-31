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

import org.springframework.cloud.appbroker.state.ServiceInstanceBindingStateRepository;
import org.springframework.cloud.appbroker.state.ServiceInstanceState;
import org.springframework.cloud.servicebroker.model.instance.OperationState;

class DefaultServiceInstanceBindingStateRepository implements ServiceInstanceBindingStateRepository {

	private final ServiceInstanceBindingRepository serviceInstanceBindingRepository;

	DefaultServiceInstanceBindingStateRepository(ServiceInstanceBindingRepository serviceInstanceBindingRepository) {
		this.serviceInstanceBindingRepository = serviceInstanceBindingRepository;
	}

	@Override
	public Mono<ServiceInstanceState> saveState(String serviceInstanceId, String bindingId, OperationState state,
		String description) {
		return serviceInstanceBindingRepository.findByServiceInstanceIdAndBindingId(serviceInstanceId, bindingId)
			.switchIfEmpty(Mono.just(new ServiceInstanceBinding()))
			.flatMap(binding -> {
				binding.setServiceInstanceId(serviceInstanceId);
				binding.setBindingId(bindingId);
				binding.setOperationState(state);
				binding.setDescription(description);
				return Mono.just(binding);
			})
			.flatMap(serviceInstanceBindingRepository::save)
			.map(DefaultServiceInstanceBindingStateRepository::toServiceInstanceState);
	}

	@Override
	public Mono<ServiceInstanceState> getState(String serviceInstanceId, String bindingId) {
		return serviceInstanceBindingRepository.findByServiceInstanceIdAndBindingId(serviceInstanceId, bindingId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException(
					"Unknown binding: serviceInstanceId=" + serviceInstanceId + ", bindingId=" + bindingId)))
			.map(DefaultServiceInstanceBindingStateRepository::toServiceInstanceState);
	}

	@Override
	public Mono<ServiceInstanceState> removeState(String serviceInstanceId, String bindingId) {
		return getState(serviceInstanceId, bindingId)
			.doOnNext(
				serviceInstanceState -> serviceInstanceBindingRepository
					.deleteByServiceInstanceIdAndBindingId(serviceInstanceId, bindingId));
	}

	private static ServiceInstanceState toServiceInstanceState(ServiceInstanceBinding binding) {
		return new ServiceInstanceState(binding.getOperationState(), binding.getDescription(), null);
	}

}
