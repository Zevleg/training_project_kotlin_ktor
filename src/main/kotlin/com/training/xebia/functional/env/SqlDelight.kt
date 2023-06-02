package com.training.xebia.functional.env

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.closeable
import com.training.xebia.functional.domain.*
import com.training.xebia.functional.persistence.TrainingKotlin
import com.training.xebia.functional.persistence.repositories.Repositories
import com.training.xebia.functional.persistence.subscriptions.Subscriptions
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.lang.System.getenv
import javax.sql.DataSource

suspend fun ResourceScope.hikari(): HikariDataSource = autoCloseable {
    HikariDataSource(HikariConfig(getenv("DATA_SOURCE_PROPERTIES") ?: "/datasource.properties"))
}

suspend fun ResourceScope.hikariWithEnv(env: Env.PostgresContainer): HikariDataSource = autoCloseable {
    HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = env.url
            username = env.username
            password = env.password
            driverClassName = env.driver
        }
    )
}

suspend fun ResourceScope.trainingKotlinDataSource(dataSource: DataSource): TrainingKotlin {
    val driver = closeable { dataSource.asJdbcDriver() }
    Flyway.configure().dataSource(dataSource).load().migrate()
    TrainingKotlin.Schema.create(driver)

    return TrainingKotlin(
        driver,
        Repositories.Adapter(RepositoryId.adapter, Owner.adapter, Repo.adapter),
        Subscriptions.Adapter(UserId.adapter, RepositoryId.adapter),
        Users.Adapter(UserId.adapter, SlackUserId.adapter)
    )
}
