// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.lxs.bigdata.es.core;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 *
 * @param <TEntity>
 */
public interface IElasticSearchClient<TEntity> extends AutoCloseable {

    void addIndex(TEntity entity);

    void addIndex(List<TEntity> entities);

    void addIndex(Stream<TEntity> entities);

    void flush();

    boolean awaitClose(long timeout, TimeUnit unit) throws InterruptedException;
}
