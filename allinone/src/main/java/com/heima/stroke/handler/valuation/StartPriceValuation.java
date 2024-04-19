package com.heima.stroke.handler.valuation;

public class StartPriceValuation implements Valuation {
    private Valuation valuation;

    public StartPriceValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        return 0;
    }
}
