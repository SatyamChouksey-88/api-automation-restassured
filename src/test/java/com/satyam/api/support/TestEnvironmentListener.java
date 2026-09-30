package com.satyam.api.support;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/** Boots WireMock before the suite when {@code -Dmode=mock}. */
public class TestEnvironmentListener implements ISuiteListener {

  @Override
  public void onStart(ISuite suite) {
    WireMockSupport.startIfMockMode();
    LiveApiProbe.ensureLiveApiReachableOrSkip();
  }

  @Override
  public void onFinish(ISuite suite) {
    WireMockSupport.stopIfStarted();
  }
}
