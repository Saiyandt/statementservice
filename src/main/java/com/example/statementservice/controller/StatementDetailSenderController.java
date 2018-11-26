package com.example.statementservice.controller;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.example.statementservice.config.AWSConfiguration;
import com.example.statementservice.model.StatementDetail;
import com.example.statementservice.service.StatementWriter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tnguyen on 11/20/18.
 */
@RestController
public class StatementDetailSenderController {

    @Autowired
    private AmazonKinesis geAmazonKinesis;

    @Autowired
    private StatementWriter statementWriter;

    @Autowired
    private AWSConfiguration awsConfiguration;


    @RequestMapping("/sendstatementdetail")
    public String send() throws Exception {
        // Validate that the stream exists and is active
        statementWriter.validateStream(geAmazonKinesis, awsConfiguration.getStmStreamName());

        StatementDetail stm = new StatementDetail("stm123", "123", "2000.00");
        statementWriter.sendStatementDetail(stm, geAmazonKinesis, awsConfiguration.getStreamName());

        return "Sent!";
    }
}
