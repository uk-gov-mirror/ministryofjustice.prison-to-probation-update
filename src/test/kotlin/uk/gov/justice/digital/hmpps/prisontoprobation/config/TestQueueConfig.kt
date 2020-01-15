package uk.gov.justice.digital.hmpps.prisontoprobation.config

import com.amazonaws.services.sqs.AmazonSQS
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test-queue")
open class TestQueueConfig(
                           private val sqsClient: AmazonSQS,
                           @Value("\${sqs.queue.name}")
                           private val queueName: String) {
    @Bean
    open fun queueUrl(): String {
        sqsClient.createQueue(queueName)
        return sqsClient.getQueueUrl(queueName).queueUrl
    }

}
