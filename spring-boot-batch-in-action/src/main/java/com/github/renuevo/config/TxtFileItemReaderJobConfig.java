package com.github.renuevo.config;

import com.github.renuevo.vo.ItemVo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


/**
 * <pre>
 * @className : TxtFileItemReaderJobConfig
 * @author : Deokhwa.Kim
 * @since : 2020-02-14
 * @summary : TXT FILE ITEM READER EXAMPLE
 * </pre>
 */
@Configuration
@AllArgsConstructor
public class TxtFileItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final int chunkSize = 2;

    @Bean
    public Job TxtFileItemReaderJob() {
        return jobBuilderFactory.get("txtFileItemReaderJob")
                .start(txtFileItemReaderStep())
                .build();
    }

    @Bean
    public Step txtFileItemReaderStep() {
        return stepBuilderFactory.get("txtFileItemReaderStep")
                .<ItemVo, ItemVo>chunk(chunkSize)
                .reader(txtFileItemReader())
                .writer(itemVo -> itemVo.forEach(System.out::println))
                .build();
    }

    @Bean
    public FlatFileItemReader<ItemVo> txtFileItemReader() {
        FlatFileItemReader<ItemVo> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/sample.txt"));
        flatFileItemReader.setLineMapper((line, lineNumber) -> new ItemVo(line));
        return flatFileItemReader;
    }

}
