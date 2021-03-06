/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.instrument.future;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;

import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.FixedIncomeInstrumentConverter;
import com.opengamma.financial.instrument.FixedIncomeInstrumentDefinitionVisitor;
import com.opengamma.financial.interestrate.InterestRateDerivative;
import com.opengamma.financial.interestrate.future.definition.InterestRateFutureOptionMarginSecurity;
import com.opengamma.financial.interestrate.future.definition.InterestRateFuture;

/**
 * Description of an interest rate future option security with daily margining process (LIFFE and Eurex type). The option is of American type.
 */
public class InterestRateFutureOptionMarginSecurityDefinition implements FixedIncomeInstrumentConverter<InterestRateDerivative> {

  /**
   * Underlying future security.
   */
  private final InterestRateFutureSecurityDefinition _underlyingFuture;
  /**
   * Expiration date.
   */
  private final ZonedDateTime _expirationDate;
  /**
   * Cap (true) / floor (false) flag.
   */
  private final boolean _isCall;
  /**
   * Strike price.
   */
  private final double _strike;

  /**
   * Constructor of the option future from the details.
   * @param underlyingFuture The underlying future security.
   * @param expirationDate The expiration date.
   * @param strike The option strike.
   * @param isCall The cap (true) / floor (false) flag.
   */
  public InterestRateFutureOptionMarginSecurityDefinition(final InterestRateFutureSecurityDefinition underlyingFuture, final ZonedDateTime expirationDate, final double strike, final boolean isCall) {
    Validate.notNull(underlyingFuture, "underlying future");
    Validate.notNull(expirationDate, "expiration");
    this._underlyingFuture = underlyingFuture;
    this._expirationDate = expirationDate;
    this._strike = strike;
    _isCall = isCall;
  }

  /**
   * Gets the underlying future security.
   * @return The underlying future security.
   */
  public InterestRateFutureSecurityDefinition getUnderlyingFuture() {
    return _underlyingFuture;
  }

  /**
   * Gets the expiration date.
   * @return The expiration date.
   */
  public ZonedDateTime getExpirationDate() {
    return _expirationDate;
  }

  /**
   * Gets the cap (true) / floor (false) flag.
   * @return The cap/floor flag.
   */
  public boolean isCall() {
    return _isCall;
  }

  /**
   * Gets the option strike.
   * @return The option strike.
   */
  public double getStrike() {
    return _strike;
  }

  @Override
  public InterestRateFutureOptionMarginSecurity toDerivative(ZonedDateTime date, String... yieldCurveNames) {
    Validate.notNull(date, "date");
    Validate.notNull(yieldCurveNames, "yield curve names");
    Validate.isTrue(yieldCurveNames.length > 1, "at least two curves required");
    final DayCount actAct = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
    final double expirationTime = actAct.getDayCountFraction(date, _expirationDate);
    InterestRateFuture underlyingFuture = _underlyingFuture.toDerivative(date, yieldCurveNames);
    InterestRateFutureOptionMarginSecurity option = new InterestRateFutureOptionMarginSecurity(underlyingFuture, expirationTime, _strike, _isCall);
    return option;
  }

  @Override
  public <U, V> V accept(FixedIncomeInstrumentDefinitionVisitor<U, V> visitor, U data) {
    return visitor.visitInterestRateFutureOptionMarginSecurityDefinition(this, data);
  }

  @Override
  public <V> V accept(FixedIncomeInstrumentDefinitionVisitor<?, V> visitor) {
    return visitor.visitInterestRateFutureOptionMarginSecurityDefinition(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _expirationDate.hashCode();
    result = prime * result + (_isCall ? 1231 : 1237);
    long temp;
    temp = Double.doubleToLongBits(_strike);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + _underlyingFuture.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InterestRateFutureOptionMarginSecurityDefinition other = (InterestRateFutureOptionMarginSecurityDefinition) obj;
    if (!ObjectUtils.equals(_expirationDate, other._expirationDate)) {
      return false;
    }
    if (_isCall != other._isCall) {
      return false;
    }
    if (Double.doubleToLongBits(_strike) != Double.doubleToLongBits(other._strike)) {
      return false;
    }
    if (!ObjectUtils.equals(_underlyingFuture, other._underlyingFuture)) {
      return false;
    }
    return true;
  }

}
