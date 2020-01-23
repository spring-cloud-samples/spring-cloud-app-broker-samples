package org.springframework.cloud.appbroker.samples.creatingworkflows;

import java.util.Arrays;
import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.cloud.appbroker.service.CreateServiceInstanceWorkflow;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.appbroker.samples.creatingworkflows.ServiceInstanceServiceOrder.VALIDATE_CREATE_PARAMETERS;
import static org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse.CreateServiceInstanceResponseBuilder;

@Component
@Order(VALIDATE_CREATE_PARAMETERS)
class ServiceInstanceParametersValidatorWorkflow implements CreateServiceInstanceWorkflow {

	private static final String SERVICE_NAME = "example";

	private static final List<String> SUPPORTED_PARAMETERS = Arrays.asList("count", "memory", "routes"); // TODO java 14

	@Override
	public Mono<Boolean> accept(CreateServiceInstanceRequest request) {
		return Mono.just(SERVICE_NAME.equals(request.getServiceDefinition().getName()));
	}

	@Override
	public Mono<CreateServiceInstanceResponseBuilder> buildResponse(
		CreateServiceInstanceRequest request,
		CreateServiceInstanceResponseBuilder responseBuilder) {

		for (String parameter : request.getParameters().keySet()) {
			if (!SUPPORTED_PARAMETERS.contains(parameter)) {
				String errorMessage = String.format("Invalid parameter {%s}", parameter);
				return Mono.error(new ServiceBrokerInvalidParametersException(errorMessage));
			}
		}

		return Mono.just(responseBuilder);
	}

}
