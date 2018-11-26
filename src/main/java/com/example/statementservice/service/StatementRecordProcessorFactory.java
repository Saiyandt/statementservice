package com.example.statementservice.service;

import com.example.statementservice.service.StatementRecordProcessor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

/**
 * Created by tnguyen on 11/20/18.
 */
@Component
public class StatementRecordProcessorFactory implements ShardRecordProcessorFactory {

    StatementService statementService;

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        StatementRecordProcessor processor = new StatementRecordProcessor();
        processor.setStatementService(statementService);
        return processor;
    }

    public StatementService getStatementService() {
        return statementService;
    }

    public void setStatementService(StatementService statementService) {
        this.statementService = statementService;
    }
}
