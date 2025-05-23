package xyz.tomorrowlearncamp.bookking.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import xyz.tomorrowlearncamp.bookking.common.enums.DataSourceType;

public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
			? DataSourceType.READER : DataSourceType.WRITER;
	}
}
