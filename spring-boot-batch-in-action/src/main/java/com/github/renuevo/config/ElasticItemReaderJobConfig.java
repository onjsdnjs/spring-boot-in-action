package com.github.renuevo.config;


import com.github.renuevo.reader.ElasticItemReader;
import com.github.renuevo.vo.ElasticReaderTestVo;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * @className : ElasticItemReaderJobConfig
 * @author : Deokhwa.Kim
 * @since : 2020-01-03
 * @summary : Elastic Item Reader Example
 * </pre>
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class ElasticItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RestHighLevelClient restHighLevelClient;

    private final static int chunkSize = 8;

    @Bean
    public Job elasticItemReaderJob() {
        return jobBuilderFactory.get("elasticItemReaderJob")
                .start(elasticItemReaderStep())
                .build();
    }

    @Bean
    public Step elasticItemReaderStep() {
        return stepBuilderFactory.get("elasticItemReaderStep")
                .<ElasticReaderTestVo, ElasticReaderTestVo>chunk(chunkSize)
                .reader(elasticItemReader())
                .writer(elasticItemReaderWriter())  //processor 까지 한번에 처리되고 list로 넘어옴
                .build();
    }

    @Bean
    @SneakyThrows
    public ElasticItemReader<ElasticReaderTestVo> elasticItemReader() {
        return ElasticItemReader.<ElasticReaderTestVo>builder()
                .sort("key")
                .restHighLevelClient(restHighLevelClient)
                .searchRequest(new SearchRequest("reader_test"))
                .classType(ElasticReaderTestVo.class)
                .pageSize(chunkSize)
                .name("elasticItemReader")
                .build();
    }

    public ItemWriter<ElasticReaderTestVo> elasticItemReaderWriter() {
        return list -> {
            for (ElasticReaderTestVo elasticReaderTestVo : list) {
                log.info("elastic = {}", elasticReaderTestVo);
            }
        };
    }


}
