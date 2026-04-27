package com.financetracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Cors cors = new Cors();
    private Pagination pagination = new Pagination();

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins;
    }

    @Getter
    @Setter
    public static class Pagination {
        private int defaultPageSize = 20;
        private int maxPageSize = 100;
    }
}