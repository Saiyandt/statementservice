package com.example.statementservice.service;

import com.example.statementservice.config.AWSConfiguration;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.RetrievalConfig;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tnguyen on 11/20/18.
 */
@Component
public class StatementConsumer {
    private static final Logger log = LoggerFactory.getLogger(StatementConsumer.class);

    @Autowired
    AWSConfiguration awsConfiguration;

    @Autowired
    StatementService statementService;

    private KinesisAsyncClient kinesisClient;
    private Region region;
    private  String streamName;
    private Scheduler scheduler;
    private String appName;


    @PostConstruct
    public void init() throws Exception {
        this.streamName = awsConfiguration.getStreamName();
        this.appName = awsConfiguration.getAppName();
        this.region = Region.of(ObjectUtils.firstNonNull(awsConfiguration.getRegion(), "us-east-2"));


        this.kinesisClient = KinesisAsyncClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(this.region)
                .build();

        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

        StatementRecordProcessorFactory statementRecordProcessorFactory = new StatementRecordProcessorFactory();
        statementRecordProcessorFactory.setStatementService(this.statementService);
        ConfigsBuilder configsBuilder = new ConfigsBuilder(streamName, appName, kinesisClient
                , dynamoClient, cloudWatchClient, UUID.randomUUID().toString(), statementRecordProcessorFactory);


        //RetrievalConfig tmp = configsBuilder.retrievalConfig().retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient));
        //tmp.initialPositionInStreamExtended().newInitialPosition(InitialPositionInStream.TRIM_HORIZON);

        RetrievalConfig tmp = new RetrievalConfig(kinesisClient, streamName, appName);
        tmp.initialPositionInStreamExtended(InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON));

        this.scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                //configsBuilder.retrievalConfig().retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient))
                tmp.retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient))
        );

        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();

    }
}
