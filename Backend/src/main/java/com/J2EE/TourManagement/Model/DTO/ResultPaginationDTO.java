package com.J2EE.TourManagement.Model.DTO;

public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    public Meta getMeta() {
        return this.meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
