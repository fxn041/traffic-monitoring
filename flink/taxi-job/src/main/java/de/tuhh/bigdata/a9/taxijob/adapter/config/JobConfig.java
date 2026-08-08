package de.tuhh.bigdata.a9.taxijob.adapter.config;

public class JobConfig {
	private double taxiSpeedingAlertThreshold;
	private double taxiMaxSpeedThreshold;
	private double taxiAreaAlertRadiusThreshold;
	private double taxiAreaMaxRadiusThreshold;

	public double getTaxiSpeedingAlertThreshold() {
		return taxiSpeedingAlertThreshold;
	}

	public void setTaxiSpeedingAlertThreshold(double taxi_speed_threshold) {
		this.taxiSpeedingAlertThreshold = taxi_speed_threshold;
	}

	public double getTaxiMaxSpeedThreshold() {
		return taxiMaxSpeedThreshold;
	}

	public void setTaxiMaxSpeedThreshold(double taxiMaxSpeedThreshold) {
		this.taxiMaxSpeedThreshold = taxiMaxSpeedThreshold;
	}

	public double getTaxiAreaAlertRadiusThreshold() {
		return taxiAreaAlertRadiusThreshold;
	}

	public void setTaxiAreaAlertRadiusThreshold(double areaAlertRadius) {
		this.taxiAreaAlertRadiusThreshold = areaAlertRadius;
	}

	public double getTaxiAreaMaxRadiusThreshold() {
		return taxiAreaMaxRadiusThreshold;
	}

	public void setTaxiAreaMaxRadiusThreshold(double taxiAreaMaxRadius) {
		this.taxiAreaMaxRadiusThreshold = taxiAreaMaxRadius;
	}
}
