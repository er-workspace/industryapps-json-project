package com.industryapps.json_partial_updates.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class DeviceSettings {

	private Integer brightness;
	
	private Integer volume;

	public Integer getBrightness() {
		return brightness;
	}

	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	
	
	@Override
	public String toString() {
		return "DeviceSettings [brightness=" + brightness + ", volume=" + volume + "]";
	}

}
