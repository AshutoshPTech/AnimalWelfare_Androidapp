package com.Pie4u.animalcare;

public class ProductModel {

    private String ed1, ed2, ed3, ed4;
    private String imgView;

    public ProductModel() {
    }

    public ProductModel(String ed1, String ed2, String ed3, String ed4, String imgView) {
        this.ed1 = ed1;
        this.ed2 = ed2;
        this.ed3 = ed3;
        this.ed4 = ed4;
        this.imgView = imgView;
    }

    public String getEd1() {
        return ed1;
    }

    public void setEd1(String ed1) {
        this.ed1 = ed1;
    }

    public String getEd2() {
        return ed2;
    }

    public void setEd2(String ed2) {
        this.ed2 = ed2;
    }

    public String getEd3() {
        return ed3;
    }

    public void setEd3(String ed3) {
        this.ed3 = ed3;
    }

    public String getEd4() {
        return ed4;
    }

    public void setEd4(String ed4) {
        this.ed4 = ed4;
    }

    public String getImgView() {
        return imgView;
    }

    public void setImgView(String imgView) {
        this.imgView = imgView;
    }

}
