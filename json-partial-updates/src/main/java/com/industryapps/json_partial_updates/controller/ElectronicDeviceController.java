package com.industryapps.json_partial_updates.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.industryapps.json_partial_updates.entities.ElectronicDevice;
import com.industryapps.json_partial_updates.service.ElectronicDeviceService;

@RestController
@RequestMapping("/api/devices")
public class ElectronicDeviceController {

	private final ElectronicDeviceService service;

	public ElectronicDeviceController(ElectronicDeviceService service) {
		this.service = service;
	}

	
	@PostMapping
	public ResponseEntity<ElectronicDevice> createDevice(@RequestBody ElectronicDevice device) {
		return ResponseEntity.ok(service.createDevice(device));
	}
	
	@GetMapping
	public ResponseEntity<List<ElectronicDevice>> getAllDevices() {
	    return ResponseEntity.ok(service.getAllDevices());
	}

	@PatchMapping("/{deviceId}")
	public ResponseEntity<ElectronicDevice> updateDevice(@PathVariable String deviceId,
			@RequestBody JsonNode updateData) {
		return ResponseEntity.ok(service.saveOrUpdate(deviceId, updateData));
	}

	@GetMapping("/{deviceId}/history")
	public ResponseEntity<List<ElectronicDevice>> getVersionHistory(@PathVariable String deviceId) {
		return ResponseEntity.ok(service.getVersionHistory(deviceId));
	}

	@GetMapping("/{deviceId}/{version}")
	public ResponseEntity<ElectronicDevice> getVersionAt(@PathVariable String deviceId, @PathVariable int version) {
		Optional<ElectronicDevice> device = service.getVersionAt(deviceId, version);
		return device.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	
	@GetMapping("/{deviceId}/history/{timestamp}")
	public ResponseEntity<ElectronicDevice> getDeviceAtTimestamp(@PathVariable String deviceId,
			@PathVariable String timestamp) {
		LocalDateTime requestedTime = LocalDateTime.parse(timestamp);
		Optional<ElectronicDevice> device = service.getDeviceStateAtTimestamp(deviceId, requestedTime);
		return device.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

}