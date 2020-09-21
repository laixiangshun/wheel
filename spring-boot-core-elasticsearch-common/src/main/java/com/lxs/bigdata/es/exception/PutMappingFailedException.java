// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.exception;

public class PutMappingFailedException extends ESException {

    public PutMappingFailedException(String indexName, Throwable cause) {
        super(String.format("Put Mapping failed for Index '%s'", indexName), cause);
    }
}
