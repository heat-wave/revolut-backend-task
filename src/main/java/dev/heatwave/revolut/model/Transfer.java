package dev.heatwave.revolut.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "Transfer")
public class Transfer {
    @Id
    @GeneratedValue
    private long transferId;

    private long senderId;
    private long recipientId;
    private BigDecimal amount;
    //private Currency currency;


    public Transfer(long senderId, long recipientId, BigDecimal amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public Transfer() {
    }

    public long getTransferId() {
        return transferId;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
