package com.example.demo.DTO;
public class CropAnalysisResult {
    
    private String cropName;
    private String parameter;
    private Double actualValue;
    private String status;

    public CropAnalysisResult(String cropName, String parameter, Double actualValue, String status){
        this.cropName = cropName;
        this.parameter = parameter;
        this.actualValue = actualValue;
        this.status = status;
    }

    public String getCropName(){
        return cropName;
    }
    public void setCropName(String cropName){
        this.cropName = cropName;
    }
    public String getParameter(){
        return parameter;
    }
    public void setParameter(String parameter){
        this.parameter = parameter;
    }
    public Double getActualValue(){
        return actualValue;
    }
    public void setActualValue(Double actualValue){
        this.actualValue = actualValue;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
