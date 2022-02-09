package com.example.section5springbatch.FlatFilesExample;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class DefaultLineMapper<T> implements LineMapper<T> {
    private LineTokenizer lineTokenizer;
    private FieldSetMapper<T> fieldSetMapper;

    public void setLineTokenize(LineTokenizer lineTokenize) {
        this.lineTokenizer = lineTokenize;
    }

    public void setFieldSetMapper(FieldSetMapper<T> fieldSetMapper) {
        this.fieldSetMapper = fieldSetMapper;
    }

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(line));
    }
}
