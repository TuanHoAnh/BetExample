/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.repository;

import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> items) {
        if (CollectionUtils.isEmpty(items)) {
            return "";
        }

        return String.join(",", items);
    }

    @Override
    public List<String> convertToEntityAttribute(String joinedItems) {
        if (joinedItems != null) {
            if("".equals(joinedItems))
                return Collections.emptyList();

            return Arrays.asList(joinedItems.split(","));
        }
        return Collections.emptyList();
    }
}
