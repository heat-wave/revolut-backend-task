package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferService {
    public Transfer createTransfer(Transfer transfer);

    public Optional<Transfer> getTransferById(Long transferId);

    public List<Transfer> getTransfersByAccountId(Long accountId);
}
