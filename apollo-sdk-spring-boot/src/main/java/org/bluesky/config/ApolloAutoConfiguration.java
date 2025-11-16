package org.bluesky.config;

import org.bluesky.apollo.core.ApolloClient;
import org.bluesky.apollo.core.ApolloConfigServiceCore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApolloProperties.class)
public class ApolloAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ApolloClient apolloClient(ApolloProperties p) {
        return new ApolloClient(p.getPortalUrl(), p.getToken());
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloConfigServiceCore apolloConfigServiceCore(ApolloClient client) {
        return new ApolloConfigServiceCore(client);
    }

//    @Bean
//    public YourHighLevelService apolloWriteService(ApolloConfigServiceCore core, ApolloProperties p) {
//        return new YourHighLevelService(core, p);
//    }
}
