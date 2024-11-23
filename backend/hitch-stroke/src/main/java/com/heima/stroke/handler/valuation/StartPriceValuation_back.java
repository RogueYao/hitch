package com.heima.stroke.handler.valuation;

public class StartPriceValuation_back implements Valuation {
    private Valuation valuation;

    private float startingPrice = 13.0F;

    public StartPriceValuation_back(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        float beforeCost = (valuation == null ? 0f : valuation.calculation(km));
        return beforeCost + startingPrice;
    }
}
