package com.opengamma.financial.analytics;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValueProperties.Builder;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;

public class PropertyPreservingFunctionTest {

  private MockPropertyPreservingFunction getFunction() {
    MockPropertyPreservingFunction func = new MockPropertyPreservingFunction(Lists.newArrayList("Prop", "A", "B", "C", "D", "E", "F"), new ArrayList<String>());
    return func;
  }

  @Test
  public void EmptyProperties() {
    MockPropertyPreservingFunction func = getFunction();
    ValueProperties props = ValueProperties.none();
    ValueProperties expected = ValueProperties.none();
    assertEqual(expected, func, props);
  }

  @Test
  public void SingleMatchingProperty() {
    MockPropertyPreservingFunction func = getFunction();
    assertEqual(ValueProperties.builder().with("A", "V").get(), func, ValueProperties.builder().with("A", "V").get(), ValueProperties.builder().with("A", "V").get());
  }

  @Test
  public void SingleNonMatchingProperty() {
    MockPropertyPreservingFunction func = getFunction();
    List<ValueSpecification> specses = getSpecs(ValueProperties.builder().with("A", "V").get(), ValueProperties.builder().with("A", "X").get());
    assertNull(func.getResultProperties(specses));
  }

  @Test
  public void SingleNonMatchingOtherProperty() {
    MockPropertyPreservingFunction func = getFunction();
    assertEqual(ValueProperties.none(), func, ValueProperties.builder().with("Z", "A").get(), ValueProperties.builder().with("Z", "B").get());
  }

  @Test
  public void SingleMatchingOtherProperty() {
    MockPropertyPreservingFunction func = getFunction();
    assertEqual(ValueProperties.none(), func, ValueProperties.builder().with("Z", "A").get(), ValueProperties.builder().with("Z", "A").get());
  }

  @Test
  public void OptionalProperty() {
    MockPropertyPreservingFunction func = getFunction();

    ValueProperties p = ValueProperties.builder().withOptional("C").withAny("C").with("D", "X").withOptional("D").get();
    assertEqual(p, func, p, p);
  }

  private void assertEqual(ValueProperties expected, MockPropertyPreservingFunction func, ValueProperties... inputs) {
    List<ValueSpecification> specses = getSpecs(inputs);
    //Check even empty sets
    assertEqualOrdered(expected, func, specses);
    //Try and check a few permutations
    //TODO: check non rotation permutations
    for (int i = 0; i < specses.size(); i++) {
      assertEqualOrdered(expected, func, specses);
      Collections.rotate(specses, 1);
    }
    //Check repeats, since there are 2 code branches
    List<ValueSpecification> doubled = new ArrayList<ValueSpecification>(inputs.length * 2);
    doubled.addAll(specses);
    doubled.addAll(specses);
    assertEqualOrdered(expected, func, doubled);
  }

  private void assertEqualOrdered(ValueProperties expected, MockPropertyPreservingFunction func, Collection<ValueSpecification> specses) {
    ValueProperties resultProperties = func.getResultProperties(specses);
    ValueProperties filteredResult = resultProperties.copy().withoutAny(ValuePropertyNames.FUNCTION).get();
    assertEquals(expected, filteredResult);
  }

  private List<ValueSpecification> getSpecs(ValueProperties... props) {
    return getSpecs(Lists.newArrayList(props));
  }

  private List<ValueSpecification> getSpecs(Collection<ValueProperties> props) {
    List<ValueSpecification> ret = new ArrayList<ValueSpecification>();
    for (ValueProperties valueProp : props) {
      ret.add(getSpec(valueProp));
    }
    return ret;
  }

  private ValueSpecification getSpec(ValueProperties props) {
    Builder realProps = props.copy().with(ValuePropertyNames.FUNCTION, "SomeFunc");
    ValueSpecification spec = ValueSpecification.of("X", null, realProps.get());
    return spec;
  }

  private class MockPropertyPreservingFunction extends PropertyPreservingFunction {

    private Collection<String> _preservedProperties;
    private Collection<String> _optionalPreservedProperties;

    public MockPropertyPreservingFunction(Collection<String> preservedProperties, Collection<String> optionalPreservedProperties) {
      super();
      _preservedProperties = preservedProperties;
      _optionalPreservedProperties = optionalPreservedProperties;
      setUniqueId("SOMEID");
    }

    @Override
    public Set<ComputedValue> execute(FunctionExecutionContext executionContext, FunctionInputs inputs,
        ComputationTarget target, Set<ValueRequirement> desiredValues) {
      throw new OpenGammaRuntimeException("Shouldn't be called");
    }

    @Override
    public ComputationTargetType getTargetType() {
      throw new OpenGammaRuntimeException("Shouldn't be called");
    }

    @Override
    public boolean canApplyTo(FunctionCompilationContext context, ComputationTarget target) {
      throw new OpenGammaRuntimeException("Shouldn't be called");
    }

    @Override
    public Set<ValueRequirement> getRequirements(FunctionCompilationContext context, ComputationTarget target,
        ValueRequirement desiredValue) {
      throw new OpenGammaRuntimeException("Shouldn't be called");
    }

    @Override
    public Set<ValueSpecification> getResults(FunctionCompilationContext context, ComputationTarget target) {
      throw new OpenGammaRuntimeException("Shouldn't be called");
    }

    @Override
    protected Collection<String> getPreservedProperties() {
      return _preservedProperties;
    }

    @Override
    protected Collection<String> getOptionalPreservedProperties() {
      return _optionalPreservedProperties;
    }

  }
}
