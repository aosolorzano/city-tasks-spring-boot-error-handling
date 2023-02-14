package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.DevicesUtil;
import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Repository
public class DeviceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRepository.class);

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public DeviceRepository(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Boolean> updateStatusByTaskOperation(Task task) {
        LOGGER.debug("updateStatusByTaskOperation(): {} - {}", task.getDeviceId(), task.getDeviceAction());
        return this.findById(task.getDeviceId())
                .map(deviceFound -> DevicesUtil.changeDeviceStatus(deviceFound, task))
                .flatMap(deviceUpdated -> Mono.fromFuture(this.dynamoDbAsyncClient.putItem(DevicesUtil.putDeviceRequest(deviceUpdated))))
                .map(putItemResponse -> putItemResponse.sdkHttpResponse().isSuccessful());
    }

    public Mono<Device> findById(String id) {
        LOGGER.debug("findById(): {}", id);
        return Mono.fromFuture(this.dynamoDbAsyncClient.getItem(DevicesUtil.getDeviceRequest(id)))
                .doOnNext(itemResponse -> {
                    if(!itemResponse.hasItem()) throw new ResourceNotFoundException(ResourceErrorEnum.DEVICE_NOT_FOUND, id);
                })
                .map(DevicesUtil::getFromItemResponse);
    }
}
