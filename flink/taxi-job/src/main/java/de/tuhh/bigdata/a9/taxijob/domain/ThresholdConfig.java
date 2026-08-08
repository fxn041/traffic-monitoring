package de.tuhh.bigdata.a9.taxijob.domain;

public class ThresholdConfig {
	private double taxiSpeedingAlertThreshold;
	private double taxiMaxSpeedThreshold;
	private double taxiAreaAlertRadiusThreshold;
	private double taxiAreaMaxRadiusThreshold;

	public ThresholdConfig(double taxiSpeedingAlertThreshold, double taxiMaxSpeedThreshold,
			double taxiAreaAlertRadiusThreshold, double taxiAreaMaxRadiusThreshold) {
		this.taxiSpeedingAlertThreshold = taxiSpeedingAlertThreshold;
		this.taxiMaxSpeedThreshold = taxiMaxSpeedThreshold;
		this.taxiAreaAlertRadiusThreshold = taxiAreaAlertRadiusThreshold;
		this.taxiAreaMaxRadiusThreshold = taxiAreaMaxRadiusThreshold;
	}

	public double getTaxiSpeedingAlertThreshold() {
		return taxiSpeedingAlertThreshold;
	}

	public void setTaxiSpeedingAlertThreshold(double taxiSpeedingAlertThreshold) {
		this.taxiSpeedingAlertThreshold = taxiSpeedingAlertThreshold;
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

	public void setTaxiAreaAlertRadiusThreshold(double taxiAreaAlertRadiusThreshold) {
		this.taxiAreaAlertRadiusThreshold = taxiAreaAlertRadiusThreshold;
	}

	public double getTaxiAreaMaxRadiusThreshold() {
		return taxiAreaMaxRadiusThreshold;
	}

	public void setTaxiAreaMaxRadiusThreshold(double taxiAreaMaxRadiusThreshold) {
		this.taxiAreaMaxRadiusThreshold = taxiAreaMaxRadiusThreshold;
	}
}
