package com.industryapps.json_partial_updates.service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.industryapps.json_partial_updates.entities.ElectronicDevice;
import com.industryapps.json_partial_updates.repository.ElectronicDeviceRepository;

import jakarta.transaction.Transactional;

@Service
public class ElectronicDeviceService {
	private final ElectronicDeviceRepository repository;
    private final ObjectMapper objectMapper;

    public ElectronicDeviceService(ElectronicDeviceRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ElectronicDevice createDevice(ElectronicDevice device) {
        device.setVersion(1);
        device.setTimestamp(LocalDateTime.now());
        return repository.save(device);
    }

    public List<ElectronicDevice> getAllDevices() {
        return repository.findAll();
    }
    
    
    @Transactional
    public ElectronicDevice saveOrUpdate(String deviceId, JsonNode updateData) {
        List<ElectronicDevice> versions = repository.findByDeviceIdOrderByVersionDesc(deviceId);
        JsonNode existingData = objectMapper.createObjectNode();
        int newVersion = 1;

        if (!versions.isEmpty()) {
            ElectronicDevice latestVersion = versions.get(0);
            try {
                existingData = objectMapper.readTree(objectMapper.writeValueAsString(latestVersion));
            } catch (Exception e) {
                throw new RuntimeException("Error parsing existing JSON data: " + e.getMessage(), e);
            }
            newVersion = latestVersion.getVersion() + 1;
        }

        JsonNode mergedData = mergeJson(existingData, updateData);

        ElectronicDevice updatedDevice;
        try {
            updatedDevice = objectMapper.treeToValue(mergedData, ElectronicDevice.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting merged JSON to entity: " + e.getMessage(), e);
        }

        updatedDevice.setId(null);
        updatedDevice.setDeviceId(deviceId);
        updatedDevice.setVersion(newVersion);
        updatedDevice.setTimestamp(LocalDateTime.now());

        return repository.save(updatedDevice);
    }

    
    public Optional<ElectronicDevice> getVersionAt(String deviceId, int version) {
        return repository.findByDeviceIdAndVersion(deviceId, version);
    }

    public List<ElectronicDevice> getVersionHistory(String deviceId) {
        return repository.findByDeviceIdOrderByVersionDesc(deviceId);
    }
    
    

    public Optional<ElectronicDevice> getDeviceStateAtTimestamp(String deviceId, LocalDateTime timestamp) {
        List<ElectronicDevice> versions = repository.findByDeviceIdOrderByTimestampAsc(deviceId);

        if (versions.isEmpty()) {
            return Optional.empty();
        }

        JsonNode baseJson = objectMapper.valueToTree(versions.get(0));

        for (ElectronicDevice version : versions) {
            if (!version.getTimestamp().isAfter(timestamp)) { 
                baseJson = mergeJson(baseJson, objectMapper.valueToTree(version));
            } else {
                break;
            }
        }

        try {
            return Optional.of(objectMapper.treeToValue(baseJson, ElectronicDevice.class));
        } catch (Exception e) {
            throw new RuntimeException("Error converting merged JSON to entity", e);
        }
    }

    
    private JsonNode mergeJson(JsonNode existing, JsonNode updates) {
        updates.fields().forEachRemaining(entry -> {
            if (existing.has(entry.getKey()) && existing.get(entry.getKey()).isObject() && entry.getValue().isObject()) {
                ((ObjectNode) existing)
                        .set(entry.getKey(), mergeJson(existing.get(entry.getKey()), entry.getValue()));
            } else {
                ((ObjectNode) existing).set(entry.getKey(), entry.getValue());
            }
        });
        return existing;
    }
    
    
}