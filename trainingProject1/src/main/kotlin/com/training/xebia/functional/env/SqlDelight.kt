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
import javax.sql.DataSource

suspend fun ResourceScope.hikari(): HikariDataSource = autoCloseable {
    HikariDataSource(HikariConfig("/datasource.properties"))
}

suspend fun ResourceScope.hikariWithEnv(env: Env.Postgres): HikariDataSource = autoCloseable {
    val config = HikariConfig("/datasource.properties" )
    config.addDataSourceProperty("serverName", env.serverName);
    config.addDataSourceProperty("portNumber", env.portNumber);
    config.addDataSourceProperty("databaseName", env.databaseName);
    config.addDataSourceProperty("user", env.username);
    config.addDataSourceProperty("password", env.password);

    HikariDataSource(config)
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
