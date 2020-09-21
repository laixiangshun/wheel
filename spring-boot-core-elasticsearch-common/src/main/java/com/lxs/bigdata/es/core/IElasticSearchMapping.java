// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.core;

import org.elasticsearch.Version;
import org.elasticsearch.common.xcontent.XContentBuilder;

public interface IElasticSearchMapping {

    XContentBuilder getMapping();

    String getIndexType();

    Version getVersion();

}
