package com.heima.stroke.handler.valuation;

public class BasicValuation implements Valuation {
    private Valuation valuation;

    public BasicValuation(Valuation valuation){
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {

        return 0;
    }
}
