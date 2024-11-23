package com.yian.stroke.handler.valuation;

public class FuelCostValuation_back implements Valuation {

    private Valuation valuation;
    private float fuelCosPrice = 1.0F;

    public FuelCostValuation_back(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        float beforeCost = (valuation == null ? 0f : valuation.calculation(km));
        return beforeCost + fuelCosPrice;
    }
}
