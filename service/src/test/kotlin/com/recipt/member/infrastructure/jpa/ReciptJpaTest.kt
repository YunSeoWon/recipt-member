package com.recipt.member.infrastructure.jpa

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Repository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@DataJpaTest
@ComponentScan(
    basePackageClasses = [AbstractReciptRepository::class],
    useDefaultFilters = false,
    includeFilters = [ComponentScan.Filter(Repository::class)]
)
abstract class ReciptJpaTest