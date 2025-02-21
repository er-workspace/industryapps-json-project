package com.industryapps.json_partial_updates.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.industryapps.json_partial_updates.entities.ElectronicDevice;

public interface ElectronicDeviceRepository extends JpaRepository<ElectronicDevice, Long> {

	List<ElectronicDevice> findByDeviceIdOrderByVersionDesc(String deviceId);

	Optional<ElectronicDevice> findByDeviceIdAndVersion(String deviceId, Integer version);
	
	
	List<ElectronicDevice> findByDeviceIdAndTimestampBeforeOrderByTimestampDesc(String deviceId,
			LocalDateTime timestamp);
	
	 List<ElectronicDevice> findByDeviceIdOrderByTimestampAsc(String deviceId);
}