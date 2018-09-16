package com.bytekast.demo

import spock.lang.Specification

class DemoKtIntegrationTest extends Specification {

  def "test area"() {

    when:
    // Simulate integration test
    def area = DemoKt.area(6, 6)

    then:
    area == 36
  }
}