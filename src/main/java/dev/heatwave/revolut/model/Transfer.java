package dev.heatwave.revolut.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "Transfer")
public class Transfer {
    @Id
    @GeneratedValue
    private Long transferId;

    private Long senderId;
    private Long recipientId;
    private BigDecimal amount;

    public Transfer(Long senderId, Long recipientId, BigDecimal amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        if (amount != null) {
            this.amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        }
    }

    public Transfer() {
    }

    public Long getTransferId() {
        return transferId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
