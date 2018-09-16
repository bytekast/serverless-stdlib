package com.bytekast.demo

import spock.lang.Specification

class DemoKtTest extends Specification {

  def "test area"() {

    when:
    def area = DemoKt.area(6, 6)

    then:
    area == 36
  }
}