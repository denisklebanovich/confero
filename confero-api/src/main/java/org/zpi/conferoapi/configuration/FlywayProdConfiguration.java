package org.zpi.conferoapi.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class FlywayProdConfiguration {

	@Bean
	protected Flyway flyway(DataSource dataSource) {
		Flyway flyway = Flyway.configure()
				.cleanDisabled(true)
				.dataSource(dataSource)
				.baselineOnMigrate(true)
				.locations("classpath:db/migration")
				.load();

		flyway.repair();
		flyway.migrate();
		return flyway;
	}
}
