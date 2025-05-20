package com.tutoras.tutoras.service;

import com.tutoras.tutoras.entity.SourceEntity;

import java.util.List;

public class PdfGeneratorService {

    SourceEntity generateCombinedSource(List<SourceEntity> sources){
        return  new SourceEntity(1L, "Title1", "Desc1", "Body1");
    } //combines all sources into one source

    String generatePdf(SourceEntity source){
        return "default";
    } //generates pdf from source, stores it in manio and returns url
}