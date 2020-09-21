// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class JsonUtilities {

    private static final Logger log = LogManager.getLogger(JsonUtilities.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <TEntity> Optional<byte[]> convertJsonToBytes(TEntity entity) {
        try {
            return Optional.of(mapper.writeValueAsBytes(entity));
        } catch(Exception e) {
            if(log.isErrorEnabled()) {
                log.error(String.format("Failed to convert entity %s to JSON", entity), e);
            }
        }
        return Optional.empty();
    }
}
