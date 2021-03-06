package com.github.renuevo.config;

import com.github.renuevo.entity.Pay;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Map;


/**
 * <pre>
 * @className : JdbcPagingItemReaderJobConfig
 * @author : Deokhwa.Kim
 * @since : 2019-12-27
 * @summary : Jdbc Paging Item Reader Example
 * </pre>
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class JdbcPagingItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunkSize = 2;

    @Bean
    public Job jdbcPaginItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() throws Exception {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<Pay, Pay>chunk(chunkSize)
                .reader(jdbcPagingItemReader())
                .writer(jdbcPagingItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Pay> jdbcPagingItemReader() throws Exception {
        Map<String, Object> parameterValues = Maps.newHashMap();
        parameterValues.put("amount", 2000);    //조건절 파라미터

        return new JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .queryProvider(createQueryProvider())
                .parameterValues(parameterValues)   //Provider Where에 조건 세팅
                .name("jdbcPagingItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Pay> jdbcPagingItemWriter() {
        return list -> {
            for (Pay pay : list) {
                log.info("Current Pay = {}", pay);
            }
        };
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource); //Datasource를 통한 DB Type 인식해서 Provider 자동 인식
        queryProviderFactoryBean.setSelectClause("id, amount, tx_name, tx_date_time");
        queryProviderFactoryBean.setFromClause("from pay");
        queryProviderFactoryBean.setWhereClause("where amount >= :amount"); //조건

        Map<String, Order> sortKeys = Maps.newHashMap();

        //Paging은 매번 다른 Connection을 맺기 때문에 Order가 필수
        sortKeys.put("id", Order.ASCENDING);

        queryProviderFactoryBean.setSortKeys(sortKeys);
        return queryProviderFactoryBean.getObject();
    }

}
