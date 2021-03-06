package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

import java.util.List;

public interface TerminatingMapReduceDecorator<T> extends ExecutableMapReduceOperation.TerminatingMapReduce<T> {
    ExecutableMapReduceOperation.TerminatingMapReduce<T> getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default List<T> all() {
        return getInvoker().invoke(() -> getImpl().all());
    }
}
