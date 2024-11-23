package com.heima.stroke.handler.valuation;

public class FuelCostValuation implements Valuation {

    private Valuation valuation;

    public FuelCostValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        return valuation.calculation(km)+1;
    }
}
