package org.nelson.valuation.calc;

import org.nelson.valuation.exception.CalculationException;

@FunctionalInterface
public interface CalculationFunction<T, R> {

    R apply(T t) throws CalculationException, InterruptedException;

}
