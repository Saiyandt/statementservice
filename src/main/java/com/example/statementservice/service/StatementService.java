package com.example.statementservice.service;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.example.statementservice.config.AWSConfiguration;
import com.example.statementservice.model.AuthorizedStatement;
import com.example.statementservice.model.StatementDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tnguyen on 11/20/18.
 */
@Service
public class StatementService {

    @Autowired
    private AmazonKinesis geAmazonKinesis;

    @Autowired
    private StatementWriter statementWriter;

    @Autowired
    private AWSConfiguration awsConfiguration;

    public void processAuthorizedStatement(AuthorizedStatement authorizedStatement) {
        // Using info from authorizedStatement, create StatementDetail and send to RS Service
        StatementDetail statementDetail = new StatementDetail(authorizedStatement.getStatementId(), authorizedStatement.getPun(), "2000.00");

        // Validate that the stream exists and is active
        statementWriter.validateStream(geAmazonKinesis, awsConfiguration.getStmStreamName());

        statementWriter.sendStatementDetail(statementDetail, geAmazonKinesis, awsConfiguration.getStmStreamName());
    }
}
