package com.yian.stroke.handler.valuation;

public class BasicValuation implements Valuation {
    private Valuation valuation;

    public BasicValuation(Valuation valuation){
        this.valuation = valuation;
    }
    //计费规则：3公里以内起步价13元；3公里以上2.3元/公里；燃油附加费1次收取1元
    @Override
    public float calculation(float km) {
        return 13f;
    }
}
