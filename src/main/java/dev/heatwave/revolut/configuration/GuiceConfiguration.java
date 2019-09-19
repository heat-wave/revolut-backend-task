package dev.heatwave.revolut.configuration;

import com.google.inject.AbstractModule;
import dev.heatwave.revolut.service.AccountService;
import dev.heatwave.revolut.service.AccountServiceImpl;
import dev.heatwave.revolut.service.TransferService;
import dev.heatwave.revolut.service.TransferServiceImpl;

public class GuiceConfiguration extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(TransferService.class).to(TransferServiceImpl.class);
    }
}