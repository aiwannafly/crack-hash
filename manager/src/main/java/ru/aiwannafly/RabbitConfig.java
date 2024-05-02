package ru.aiwannafly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String TASKS_EXCHANGE = "tasks";
    public static final String COMPLETED_KEY = "completed";
    public static final String TODO_KEY = "todo";
    public static final String COMPLETED_TASKS_QUERY = "completed-tasks";
    public static final String TODO_TASKS_QUERY = "todo-tasks";

    @Bean
    public MessageConverter messageConverter(ObjectMapper jsonMapper){
        return new Jackson2JsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.containerAckMode(AcknowledgeMode.AUTO);

        return template;
    }

    @Bean
    public Queue todoTasks() {
        return new Queue(TODO_TASKS_QUERY);
    }

    @Bean
    public Queue completedTasks() {
        return new Queue(COMPLETED_TASKS_QUERY);
    }

    @Bean
    public DirectExchange tasksExchange() {
        return new DirectExchange(TASKS_EXCHANGE, true, false);
    }

    @Bean
    public Binding completedTasksBinding(@Qualifier("completedTasks") Queue workerQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(workerQueue).to(exchange)
                .with(COMPLETED_KEY);
    }

    @Bean
    public Binding newTasksBinding(@Qualifier("todoTasks") Queue managerQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(managerQueue).to(exchange)
                .with(TODO_KEY);
    }
}
