package pl.kuponik.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import pl.kuponik.exception.CouponNotActiveException;
import pl.kuponik.exception.UnauthorizedCouponAccessException;

import java.util.UUID;

@Entity
@Getter
public class Coupon {

    @Id
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "loyalty_account_id")
    private LoyaltyAccount loyaltyAccount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NominalValue nominalValue;

    private boolean active;

    @Version
    private int version;

    public Coupon(LoyaltyAccount loyaltyAccount, NominalValue nominalValue) {
        this.id = UUID.randomUUID();
        this.loyaltyAccount = loyaltyAccount;
        this.nominalValue = nominalValue;
        this.active = true;
    }

    protected Coupon() {
    }

    public void redeem(UUID loyaltyAccountId) {
        if (!isOwnedBy(loyaltyAccountId)) {
            throw new UnauthorizedCouponAccessException(this.id, loyaltyAccountId);
        }

        if (!isActive()) {
            throw new CouponNotActiveException(this.id);
        }

        this.active = false;
    }

    private boolean isOwnedBy(UUID loyaltyAccountId) {
        return this.loyaltyAccount.getId().equals(loyaltyAccountId);
    }
}
